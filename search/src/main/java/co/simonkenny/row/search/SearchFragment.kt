package co.simonkenny.row.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import co.simonkenny.row.search.databinding.FragSearchBinding

class SearchFragment : Fragment() {

    private lateinit var binding: FragSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_search, container, false)
        return binding.root
    }
}