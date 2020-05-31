package co.simonkenny.row.search

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
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
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import co.simonkenny.row.core.UiState
import co.simonkenny.row.core.di.FakeDI
import co.simonkenny.row.navigation.Navigate
import co.simonkenny.row.search.databinding.FragSearchBinding
import java.lang.IllegalArgumentException

class SearchFragment : Fragment() {

    private val retrofit = FakeDI.instance.retrofit

    private lateinit var binding: FragSearchBinding
    private val args: SearchFragmentArgs by navArgs()

    private val viewModel: SearchViewModel by lazy {
        ViewModelProvider(requireActivity().viewModelStore, object: ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                if (modelClass == SearchViewModel::class.java) {
                    return SearchViewModel(retrofit) as T
                }
                throw IllegalArgumentException("Cannot create ViewMode of class ${modelClass.canonicalName}")
            }
        }).get(SearchViewModel::class.java)
    }

    private val searchListAdapter = SearchListAdapter(
        object: SearchListAdapter.Callback {
            override fun onTap(url: String) {
                Log.d("SearchFragment", "tap on item with URL: $url")
                //startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)))
                Navigate.toReader(requireContext(), url)
            }
        }
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_search, container, false)
        with (binding) {
            tvSearchWelcomeTitle.typeface = Typeface.createFromAsset(context?.assets, "fonts/LibreBaskerville-Bold.ttf")
            tvSearchWelcomeBody.typeface = Typeface.createFromAsset(context?.assets, "fonts/LibreBaskerville-Regular.ttf")
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with (binding) {
            rvSearch.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = searchListAdapter
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }
        }
        viewModel.searchResultList.observe(viewLifecycleOwner,
            Observer { processObserved(it) })
        viewModel.errorEvent.observe(viewLifecycleOwner, Observer {
            binding.pbSearch.isGone = true
            showWelcomeState(false)
            it.printStackTrace()
            Toast.makeText(context, "Error searching for query ${args.query}", Toast.LENGTH_SHORT).show()
        })
        Log.d("SearchFragment", "query: $args.query")
        when {
            args.query.isNotBlank() -> {
                viewModel.search(args.query)
                showWelcomeState(false)
            }
            viewModel.searchResultList.value != null ->
                processObserved(requireNotNull(viewModel.searchResultList.value))
            else -> showWelcomeState(true)
        }
    }

    private fun processObserved(observed: UiState<List<SearchResultItem>>) {
        when (observed) {
            is UiState.Success -> {
                binding.pbSearch.isGone = true
                showWelcomeState(false)
                searchListAdapter.submitList(observed.data)
            }
            // moved error handling to separate LiveData, so get last working result here
            is UiState.Loading -> {
                binding.pbSearch.isVisible = true
                showWelcomeState(false)
            }
        }
    }

    private fun showWelcomeState(show: Boolean) {
        with (binding) {
            llSearchWelcome.isVisible = show
            rvSearch.isGone = show
            if (show) {
                pbSearch.isVisible = false
            }
        }
    }
}