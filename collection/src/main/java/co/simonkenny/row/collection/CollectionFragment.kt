package co.simonkenny.row.collection

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import co.simonkenny.row.collection.databinding.FragCollectionBinding

class CollectionFragment : Fragment() {

    private lateinit var binding: FragCollectionBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_collection, container, false)
        with (binding) {
            tvCollectionWelcomeTitle.typeface = Typeface.createFromAsset(context?.assets, "fonts/LibreBaskerville-Bold.ttf")
            tvCollectionWelcomeBody.typeface = Typeface.createFromAsset(context?.assets, "fonts/LibreBaskerville-Regular.ttf")
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO : temporarily hardcoded test for add to collection bottomsheet dialog fragment
        AddToCollectionBottomSheetDialogFragment
            .newInstance("https://io9.gizmodo.com/as-the-punisher-skull-re-emerges-on-cops-in-u-s-protes-1843911179")
            .show(parentFragmentManager, AddToCollectionBottomSheetDialogFragment::class.java.name)
    }
}