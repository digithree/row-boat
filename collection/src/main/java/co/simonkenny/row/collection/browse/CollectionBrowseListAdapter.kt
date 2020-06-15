package co.simonkenny.row.collection.browse

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import co.simonkenny.row.collection.R
import co.simonkenny.row.core.article.Article

internal class CollectionBrowseListAdapter(
    private val callback: Callback
): ListAdapter<Article, CollectionBrowseListAdapter.ViewHolder>(
    object: DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean =
            oldItem.url == newItem.url

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean =
            oldItem.toString() == newItem.toString()
    }
) {

    interface Callback {
        fun onTap(url: String)
        fun onReadStateChange(url: String, read: Boolean)
        fun onHasSelectedChanged(selected: List<String>)
    }

    private val _selected = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(CollectionBrowseItemView(parent.context))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with (holder.view) {
            with(getItem(position)) {
                clearHandlers()
                setBackgroundStyle(position % 2 != 0, _selected.contains(url))
                setPermission(context.getString(R.string.browse_collection_item_permission_local))
                setLoadState(context.getString(
                    if (body != null) R.string.browse_collection_item_load_state_all_data
                    else R.string.browse_collection_item_load_state_just_metadata
                ))
                setTitle(title ?: context.getString(R.string.browse_collection_item_no_title))
                setSubtitle(url)
                setTags(
                    tags?.takeIf { it.isNotBlank() }?.split(",") ?: emptyList()
                )
                setReadState(read ?: false)
                setClickHandler {
                    if (_selected.isNotEmpty()) {
                        toggleSelection(url, position)
                    } else {
                        callback.onTap(url)
                    }
                }
                setLongClickHandler { toggleSelection(url, position) }
                setReadToggleHandler { read -> callback.onReadStateChange(url, read) }
            }
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val view: CollectionBrowseItemView get() = itemView as CollectionBrowseItemView
    }

    private fun toggleSelection(url: String, adapterPosition: Int?) {
        if (_selected.contains(url)) {
            _selected.remove(url)
        } else {
            _selected.add(url)
        }
        callback.onHasSelectedChanged(_selected.toList())
        adapterPosition?.run {
            notifyItemChanged(adapterPosition)
        }
    }
}