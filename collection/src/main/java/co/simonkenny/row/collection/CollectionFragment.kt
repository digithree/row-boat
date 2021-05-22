package co.simonkenny.row.collection

import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import co.simonkenny.row.collection.browse.CollectionBrowseListAdapter
import co.simonkenny.row.collection.browse.CollectionBrowseViewModel
import co.simonkenny.row.collection.databinding.FragCollectionBinding
import co.simonkenny.row.collectionsupport.CollectionShareHandler
import co.simonkenny.row.core.article.Article
import co.simonkenny.row.core.di.FakeDI
import co.simonkenny.row.navigation.Navigate
import co.simonkenny.row.util.UiState


private const val UI_UPDATE_DELAY_MS = 300L

class CollectionFragment : Fragment() {

    private val articleRepo by lazy {
        FakeDI.instance.articleRepo.apply {
            init(requireActivity().application)
        }
    }

    private lateinit var binding: FragCollectionBinding

    private var showHasSelection = false
    private var showOnlyUnread = false

    private val viewModel: CollectionBrowseViewModel by lazy {
        ViewModelProvider(viewModelStore, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                if (modelClass == CollectionBrowseViewModel::class.java) {
                    return CollectionBrowseViewModel(
                            articleRepo
                    ) as T
                }
                throw IllegalArgumentException("Cannot create ViewMode of class ${modelClass.canonicalName}")
            }
        }).get(CollectionBrowseViewModel::class.java)
    }

    private val collectionBrowseListAdapter = CollectionBrowseListAdapter(
            object : CollectionBrowseListAdapter.Callback {
                override fun onTap(url: String) {
                    Navigate.toReader(requireContext(), url)
                }

                override fun onReadStateChange(url: String, read: Boolean) {
                    viewModel.updateArticleReadState(url, read)
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (showOnlyUnread && read) {
                            viewModel.fetchArticles(unreadOnly = showOnlyUnread)
                        }
                    }, UI_UPDATE_DELAY_MS)
                }

                override fun onHasSelectedChanged(selected: List<String>) {
                    showHasSelection = selected.isNotEmpty()
                    requireActivity().invalidateOptionsMenu()
                }
            }
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_collection, container, false)
        with(binding) {
            tvCollectionWelcomeTitle.typeface = Typeface.createFromAsset(context?.assets, "fonts/LibreBaskerville-Bold.ttf")
            tvCollectionWelcomeBody.typeface = Typeface.createFromAsset(context?.assets, "fonts/LibreBaskerville-Regular.ttf")
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            rvCollectionBrowse.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = collectionBrowseListAdapter
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }
            fabOptionAddArticle.setOnClickListener {
                AlertDialog.Builder(requireContext()).apply {
                    setTitle("Open an article")
                    val etInput = EditText(requireContext()).apply {
                        inputType = InputType.TYPE_CLASS_TEXT // or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    }
                    setView(etInput)
                    setPositiveButton(getString(R.string.add_article_dialog_go)) {
                        _, _ -> Navigate.toReader(requireContext(), etInput.text.toString())
                    }
                    setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }
                }.show()
            }
            fabOptionFromClipboard.setOnClickListener { _ ->
                (requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager)
                    .primaryClip
                    ?.takeIf { it.itemCount > 0 }
                    ?.getItemAt(0)?.text?.toString()
                    ?.takeIf { it.startsWith("http") }
                    ?.run { Navigate.toReader(requireContext(), this) }
                    ?: Toast.makeText(requireContext(), getString(R.string.from_clipboard_error), Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.articleList.observe(viewLifecycleOwner, Observer {
            processObserved(it)
        })
        viewModel.deleteArticlesEvent.observe(viewLifecycleOwner, Observer {
            if (it.first != null) {
                Toast.makeText(requireContext(), "Articles deleted from local Collection", Toast.LENGTH_LONG).show()
                collectionBrowseListAdapter.clearSelected()
                viewModel.fetchArticles(unreadOnly = showOnlyUnread)
                requireActivity().invalidateOptionsMenu()
            } else if (it.second != null) {
                requireNotNull(it.second).printStackTrace()
                Toast.makeText(requireContext(), "Failed to delete articles", Toast.LENGTH_LONG).show()
            }
        })
        viewModel.fetchArticles(unreadOnly = showOnlyUnread)
        requireActivity().invalidateOptionsMenu()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        with(menu) {
            findItem(R.id.action_close).isVisible = showHasSelection
            findItem(R.id.action_delete_all).isVisible = showHasSelection
            findItem(R.id.action_add_to_collection).isVisible = false
            findItem(R.id.action_share).isVisible = showHasSelection
            findItem(R.id.action_upload).isVisible = false
            findItem(R.id.action_filter_list).isVisible = !showOnlyUnread
            findItem(R.id.action_unfilter_list).isVisible = showOnlyUnread
            findItem(R.id.action_settings).isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_close -> {
                Toast.makeText(requireContext(), "Clearing selection", Toast.LENGTH_SHORT).show()
                requireActivity().invalidateOptionsMenu()
                collectionBrowseListAdapter.clearSelected()
                return true
            }
            R.id.action_delete_all -> {
                deleteArticlesWithConfirm()
                return true
            }
            R.id.action_share -> {
                collectionBrowseListAdapter.selected.takeIf { it.isNotEmpty() }
                        ?.run {
                            CollectionShareHandler(requireContext(), collectionBrowseListAdapter.selected)
                        }
                        ?: Toast.makeText(requireContext(), "Nothing to share", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.action_filter_list -> {
                Toast.makeText(requireContext(), "Showing only unread items", Toast.LENGTH_SHORT).show()
                showOnlyUnread = true
                requireActivity().invalidateOptionsMenu()
                viewModel.fetchArticles(unreadOnly = showOnlyUnread)
                return true
            }
            R.id.action_unfilter_list -> {
                Toast.makeText(requireContext(), "Showing all items", Toast.LENGTH_SHORT).show()
                showOnlyUnread = false
                requireActivity().invalidateOptionsMenu()
                viewModel.fetchArticles(unreadOnly = showOnlyUnread)
                return true
            }
            R.id.action_settings -> {
                Navigate.toSettings(requireContext())
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun processObserved(observed: UiState<List<Article>>) {
        when (observed) {
            is UiState.Loading -> {
                binding.pbCollectionBrowse.isVisible = true
                showWelcomeState(false)
            }
            is UiState.Success -> {
                binding.pbCollectionBrowse.isGone = true
                collectionBrowseListAdapter.submitListProxy(observed.data)
                binding.rvCollectionBrowse.post {
                    showWelcomeState(collectionBrowseListAdapter.itemCount == 0)
                }
            }
            is UiState.Error -> {
                observed.exception.printStackTrace()
                Toast.makeText(requireContext(), "Failed to fetch Collection", Toast.LENGTH_LONG).show()
                binding.pbCollectionBrowse.isGone = true
                showWelcomeState(true)
            }
        }
    }

    private fun showWelcomeState(show: Boolean) {
        with(binding) {
            llCollectionWelcome.isVisible = show
            rvCollectionBrowse.isGone = show
            if (show) {
                pbCollectionBrowse.isVisible = false
            }
        }
    }

    private fun deleteArticlesWithConfirm() {
        if (collectionBrowseListAdapter.selected.isEmpty()) {
            Toast.makeText(requireContext(), "No selection to delete", Toast.LENGTH_LONG).show()
            return
        }
        val dialogClickListener =
            DialogInterface.OnClickListener { _, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> viewModel.deleteArticles(
                            collectionBrowseListAdapter.selected
                    )
                    DialogInterface.BUTTON_NEGATIVE -> Log.d("CollectionFragment", "deletion cancelled")
                }
            }

        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.browse_collection_item_delete_message))
            .setPositiveButton(getString(R.string.delete), dialogClickListener)
            .setNegativeButton(getString(R.string.cancel), dialogClickListener)
            .show()
    }
}