package co.simonkenny.row.reader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import co.simonkenny.row.reader.databinding.FragReaderBinding

class ReaderFragment : Fragment() {

    private lateinit var binding: FragReaderBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_reader, container, false)
        return binding.root
    }
}