package co.simonkenny.row.collection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import co.simonkenny.row.collection.databinding.FragLocalCollectionBinding

class LocalCollectionFragment : Fragment() {

    private lateinit var binding: FragLocalCollectionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.frag_local_collection, container, false)
        return binding.root
    }
}