package co.simonkenny.row.search

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

internal class SearchListAdapter(
    private val callback: Callback
) : ListAdapter<SearchResultItem, SearchListAdapter.ViewHolder>(
    object: DiffUtil.ItemCallback<SearchResultItem>() {
        override fun areItemsTheSame(oldItem: SearchResultItem, newItem: SearchResultItem): Boolean =
            oldItem.url == newItem.url

        override fun areContentsTheSame(oldItem: SearchResultItem, newItem: SearchResultItem): Boolean =
            oldItem.toString() == newItem.toString()
    }
) {
    interface Callback {
        fun onTap(url: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(SearchResultItemView(parent.context))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with (holder.view) {
            setAlternateBackgroundStyle(position % 2 != 0)
            with (getItem(position)) {
                setTitle(title)
                setSubtitle(url)
                setClickHandler { callback.onTap(url) }
            }
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val view: SearchResultItemView get() = itemView as SearchResultItemView
    }
}