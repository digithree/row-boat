package co.simonkenny.row.collection.browse

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import co.simonkenny.row.collection.R
import co.simonkenny.row.core.article.Article
import java.util.*

internal class CollectionBrowseListAdapter(
    private val callback: Callback
): ListAdapter<CollectionBrowseListAdapter.SelectableArticleWrapper, CollectionBrowseListAdapter.ViewHolder>(
    object: DiffUtil.ItemCallback<SelectableArticleWrapper>() {
        override fun areItemsTheSame(oldItem: SelectableArticleWrapper, newItem: SelectableArticleWrapper): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: SelectableArticleWrapper, newItem: SelectableArticleWrapper): Boolean =
            oldItem.article.toString() == newItem.article.toString()
                    && oldItem.selected == newItem.selected
    }
) {

    interface Callback {
        fun onTap(url: String)
        fun onReadStateChange(url: String, read: Boolean)
        fun onHasSelectedChanged(selected: List<String>)
    }

    private val _selected = mutableListOf<String>()
    val selected: List<String> = _selected

    fun submitListProxy(list: List<Article>?) {
        super.submitList(list?.map { SelectableArticleWrapper(it, _selected.contains(it.url)) })
        list?.map { it.url }?.run {
            _selected.removeIf { !contains(it) }
        }
    }

    fun clearSelected() {
        if (_selected.isEmpty()) return
        _selected.clear()
        callback.onHasSelectedChanged(_selected)
        super.submitList(mutableListOf<SelectableArticleWrapper>().apply {
            for (i in 0 until itemCount) add(getItem(i).apply {
                selected = false
            })
        })
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(CollectionBrowseItemView(parent.context))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with (holder.view) {
            with(getItem(position)) {
                clearHandlers()
                setBackgroundStyle(position % 2 != 0, selected)
                setPermission(context.getString(R.string.browse_collection_item_permission_local))
                setLoadState(context.getString(
                    if (article.body != null) R.string.browse_collection_item_load_state_all_data
                    else R.string.browse_collection_item_load_state_just_metadata
                ))
                setTitle(article.title ?: context.getString(R.string.browse_collection_item_no_title))
                setSubtitle(article.url)
                setTags(
                    article.tags?.takeIf { it.isNotBlank() }?.split(",") ?: emptyList()
                )
                setReadState(article.read ?: false)
                setClickHandler {
                    if (_selected.isNotEmpty()) {
                        toggleSelection(article.url, position)
                    } else {
                        callback.onTap(article.url)
                    }
                }
                setLongClickHandler { toggleSelection(article.url, position) }
                setReadToggleHandler { read -> callback.onReadStateChange(article.url, read) }
            }
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val view: CollectionBrowseItemView get() = itemView as CollectionBrowseItemView
    }

    data class SelectableArticleWrapper(
        val article: Article,
        var selected: Boolean = false,
    )

    private fun toggleSelection(url: String, adapterPosition: Int?) {
        if (_selected.contains(url)) {
            _selected.remove(url)
        } else {
            _selected.add(url)
        }
        callback.onHasSelectedChanged(_selected)
        super.submitList(mutableListOf<SelectableArticleWrapper>().apply {
            for (i in 0 until itemCount) add(getItem(i).apply {
                if (i == adapterPosition) selected = !selected
            })
        })
        adapterPosition?.run {
            notifyItemChanged(this)
        }
    }
}