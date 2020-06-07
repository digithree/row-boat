package co.simonkenny.row.collection

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import co.simonkenny.row.core.UiState
import co.simonkenny.row.core.article.Article
import co.simonkenny.row.core.di.FakeDI
import co.simonkenny.row.navigation.Navigate

class CollectionFragment : Fragment() {

    private val articleRepo by lazy {
        FakeDI.instance.articleRepo.apply {
            init(requireActivity().application)
        }
    }

    private lateinit var binding: FragCollectionBinding

    private val viewModel: CollectionBrowseViewModel by lazy {
        ViewModelProvider(viewModelStore, object: ViewModelProvider.Factory {
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
        object: CollectionBrowseListAdapter.Callback {
            override fun onTap(url: String) {
                Navigate.toReader(requireContext(), url)
            }

            override fun onLongTap(url: String): Boolean {
                // TODO : delete item, with confirmation
                return false
            }
        }
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_collection, container, false)
        with (binding) {
            tvCollectionWelcomeTitle.typeface = Typeface.createFromAsset(context?.assets, "fonts/LibreBaskerville-Bold.ttf")
            tvCollectionWelcomeBody.typeface = Typeface.createFromAsset(context?.assets, "fonts/LibreBaskerville-Regular.ttf")
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with (binding) {
            rvCollectionBrowse.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = collectionBrowseListAdapter
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }
        }
        viewModel.articleList.observe(viewLifecycleOwner, Observer {
            processObserved(it)
        })
        viewModel.fetchArticles()
        requireActivity().invalidateOptionsMenu()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        with (menu) {
            findItem(R.id.action_add_to_collection).isVisible = false
            findItem(R.id.action_search).isVisible = false
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
                collectionBrowseListAdapter.submitList(observed.data)
                binding.rvCollectionBrowse.post {
                    showWelcomeState(collectionBrowseListAdapter.itemCount == 0)
                }
            }
            is UiState.Error -> {
                Toast.makeText(requireContext(), "Failed to fetch Collection", Toast.LENGTH_LONG).show()
                showWelcomeState(false)
            }
        }
    }

    private fun showWelcomeState(show: Boolean) {
        with (binding) {
            llCollectionWelcome.isVisible = show
            rvCollectionBrowse.isGone = show
            if (show) {
                pbCollectionBrowse.isVisible = false
            }
        }
    }
}