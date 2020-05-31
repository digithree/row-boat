package co.simonkenny.row.search

import android.content.Intent
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
import co.simonkenny.row.search.databinding.FragSearchBinding
import java.lang.IllegalArgumentException

class SearchFragment : Fragment() {

    private val retrofit = FakeDI.instance.retrofit

    private lateinit var binding: FragSearchBinding
    private val args: SearchFragmentArgs by navArgs()

    private val viewModel: SearchViewModel by lazy {
        ViewModelProvider(this, object: ViewModelProvider.Factory {
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
                startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)))
            }
        }
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_search, container, false)
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
        viewModel.searchResultList.observe(viewLifecycleOwner, Observer {
            when (it) {
                is UiState.Success -> {
                    binding.pbSearch.isGone = true
                    searchListAdapter.submitList(it.data)
                }
                is UiState.Error -> {
                    binding.pbSearch.isGone = true
                    it.exception.printStackTrace()
                    Toast.makeText(context, "Error searching for query ${args.query}", Toast.LENGTH_SHORT).show()
                }
                is UiState.Loading -> binding.pbSearch.isVisible = true
            }
        })
        Log.d("SearchFragment", "query: $args.query")
        viewModel.search(args.query)
    }
}