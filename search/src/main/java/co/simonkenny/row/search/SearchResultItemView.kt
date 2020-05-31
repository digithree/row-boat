package co.simonkenny.row.search

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import co.simonkenny.row.search.databinding.ViewSearchResultItemBinding


internal class SearchResultItemView(context: Context): LinearLayout(context) {

    private val binding: ViewSearchResultItemBinding = ViewSearchResultItemBinding.inflate(
        LayoutInflater.from(context), this, true)

    init {
        layoutParams = RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    fun setClickHandler(handler: () -> Unit) =
        binding.llSearchResultItem.setOnClickListener { handler() }

    fun setTitle(title: String) {
        binding.tvSearchResultItemTitle.text = title
    }

    fun setSubtitle(subtitle: String) {
        binding.tvSearchResultItemSubtitle.text = subtitle
    }

    fun setAlternateBackgroundStyle(alternate: Boolean) {
        binding.flSearchResultItem.setBackgroundColor(ContextCompat.getColor(context,
            if (!alternate) R.color.background_light else R.color.background_alternate ))
    }
}