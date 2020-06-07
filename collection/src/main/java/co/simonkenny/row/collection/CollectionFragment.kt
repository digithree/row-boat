package co.simonkenny.row.collection

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.simonkenny.row.collection.databinding.FragCollectionBinding
import co.simonkenny.row.core.UiState
import co.simonkenny.row.core.di.FakeDI

class CollectionFragment : Fragment() {

    private val articleRepo by lazy {
        FakeDI.instance.articleRepo.apply {
            init(requireActivity().application)
        }
    }

    private val viewModel: CollectionBrowseViewModel by lazy {
        ViewModelProvider(viewModelStore, object: ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                if (modelClass == CollectionBrowseViewModel::class.java) {
                    return CollectionBrowseViewModel(articleRepo) as T
                }
                throw IllegalArgumentException("Cannot create ViewMode of class ${modelClass.canonicalName}")
            }
        }).get(CollectionBrowseViewModel::class.java)
    }

    private lateinit var binding: FragCollectionBinding

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
        requireActivity().invalidateOptionsMenu()

        // TODO : remove, just for debug
        viewModel.articleList.observe(viewLifecycleOwner, Observer {
            when (it) {
                is UiState.Success -> Log.d("CollectionFragment", "Collection: ${it.data}")
                is UiState.Error -> Toast.makeText(requireContext(), "Failed to fetch Collection", Toast.LENGTH_LONG).show()
            }
        })
        viewModel.fetchArticles()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        with (menu) {
            findItem(R.id.action_add_to_collection).isVisible = false
            findItem(R.id.action_search).isVisible = false
        }
    }
}