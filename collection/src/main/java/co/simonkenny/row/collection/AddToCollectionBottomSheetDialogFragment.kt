package co.simonkenny.row.collection

import android.app.Dialog
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import co.simonkenny.row.collection.databinding.FragBottomSheetAddToCollectionBinding
import co.simonkenny.row.tags.TagsStore
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip

private const val ARG_URL = "arg_url";

class AddToCollectionBottomSheetDialogFragment: BottomSheetDialogFragment() {

    companion object {
        fun newInstance(url: String) =
            AddToCollectionBottomSheetDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_URL, url)
                }
            }
        }

    private lateinit var binding: FragBottomSheetAddToCollectionBinding

    private val selectedTagsList = mutableListOf<String>()
    private var chipSelectHandlerFreeze = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_bottom_sheet_add_to_collection, container, false)
        with (binding) {
            tvAddToCollectionTitle.text = SpannableStringBuilder(requireNotNull(requireArguments().getString(ARG_URL))).apply {
                setSpan(UnderlineSpan(), 0, length, 0)
            }
            btnAddToCollectionCancel.setOnClickListener { dismiss() }
            btnAddToCollectionAdd.setOnClickListener { doSubmit() }
            TagsStore.instance.getAllTags()
                .forEach {
                    (layoutInflater.inflate(R.layout.chip_selectable, cgAddToCollectionTags, false) as Chip).apply {
                        text = it
                        cgAddToCollectionTags.addView(this)
                        setOnCheckedChangeListener { _, isChecked -> handleChipClick(this, isChecked) }
                    }
                }
        }
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener {
            dialog.findViewById<FrameLayout>(R.id.design_bottom_sheet)?.run {
                with (BottomSheetBehavior.from(this)) {
                    state = BottomSheetBehavior.STATE_COLLAPSED
                    peekHeight = resources.getDimension(R.dimen.bottom_sheet_peek_height).toInt()
                }
            }
        }

        return dialog
    }


    private fun handleChipClick(chip: Chip, isChecked: Boolean) {
        if (chipSelectHandlerFreeze) return
        if (isChecked) {
            if (selectedTagsList.size >= 3) {
                chipSelectHandlerFreeze = true
                chip.isChecked = false
                chipSelectHandlerFreeze = false
                Toast.makeText(context, getString(R.string.error_too_many_tags), Toast.LENGTH_SHORT).show()
            } else {
                selectedTagsList += chip.text.toString()
            }
        } else {
            selectedTagsList.remove(chip.text.toString())
        }
    }

    private fun doSubmit() {
        Toast.makeText(context, "Would submit URL with tags: $selectedTagsList", Toast.LENGTH_LONG).show()
        dismiss()
    }
}