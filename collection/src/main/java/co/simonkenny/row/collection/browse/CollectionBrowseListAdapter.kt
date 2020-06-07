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
        fun onLongTap(url: String): Boolean
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(CollectionBrowseItemView(parent.context))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with (holder.view) {
            setAlternateBackgroundStyle(position % 2 != 0)
            with(getItem(position)) {
                setPermission(context.getString(R.string.browse_collection_item_permission_local))
                setLoadState(context.getString(
                    if (body != null) R.string.browse_collection_item_load_state_all_data
                    else R.string.browse_collection_item_load_state_just_metadata
                ))
                setTitle(title ?: context.getString(R.string.browse_collection_item_no_title))
                setSubtitle(url)
                setTags(
                    tags?.split(",") ?: emptyList()
                )
                setClickHandler { callback.onTap(url) }
                setLongClickHandler { callback.onLongTap(url) }
            }
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val view: CollectionBrowseItemView get() = itemView as CollectionBrowseItemView
    }
}