package co.simonkenny.row.collection

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
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
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().invalidateOptionsMenu()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        with (menu) {
            findItem(R.id.action_add_to_collection).isVisible = false
            findItem(R.id.action_search).isVisible = false
        }
    }
}