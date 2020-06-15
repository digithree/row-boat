package co.simonkenny.row.collection.browse

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import co.simonkenny.row.collection.R
import co.simonkenny.row.collection.databinding.ViewCollectionBrowseItemBinding
import com.google.android.material.chip.Chip

class CollectionBrowseItemView(context: Context): LinearLayout(context) {

    private val binding: ViewCollectionBrowseItemBinding = ViewCollectionBrowseItemBinding.inflate(
        LayoutInflater.from(context), this, true)

    init {
        layoutParams = RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    fun clearHandlers() {
        binding.llCollectionBrowseItem.setOnClickListener(null)
        binding.llCollectionBrowseItem.setOnLongClickListener(null)
        binding.tbCollectionBrowseItemRead.setOnCheckedChangeListener(null)
    }

    fun setClickHandler(handler: () -> Unit) =
        binding.llCollectionBrowseItem.setOnClickListener { handler() }

    fun setLongClickHandler(handler: () -> Unit) =
        binding.llCollectionBrowseItem.setOnLongClickListener {
            handler()
            true
        }

    fun setReadToggleHandler(handler: (read: Boolean) -> Unit) =
        binding.tbCollectionBrowseItemRead.setOnCheckedChangeListener { _, isChecked -> handler(isChecked) }

    fun setPermission(permission: String) {
        binding.tvCollectionBrowseItemPermission.text = permission
    }

    fun setLoadState(loadState: String) {
        binding.tvCollectionBrowseItemLoadState.text = loadState
    }

    fun setTitle(title: String) {
        binding.tvCollectionBrowseItemTitle.text = title
    }

    fun setSubtitle(subtitle: String) {
        binding.tvCollectionBrowseItemSubtitle.text = subtitle
    }

    fun setBackgroundStyle(alternate: Boolean, selected: Boolean) {
        binding.flCollectionBrowseItem.setBackgroundColor(
            ContextCompat.getColor(context,
                if (selected) {
                    R.color.colorSelected
                } else {
                    if (!alternate) R.color.background_light else R.color.background_alternate
                }
            ))
    }

    fun setReadState(read: Boolean) {
        binding.tbCollectionBrowseItemRead.isChecked = read
    }

    fun setTags(tagsList: List<String>) {
        with (binding) {
            cgCollectionBrowseTags.removeAllViews()
            cgCollectionBrowseTags.isVisible = tagsList.isNotEmpty()
            tagsList.forEach {
                (LayoutInflater.from(context).inflate(R.layout.chip_basic, cgCollectionBrowseTags, false) as Chip).apply {
                    text = it
                    cgCollectionBrowseTags.addView(this)
                }
            }
        }
    }
}