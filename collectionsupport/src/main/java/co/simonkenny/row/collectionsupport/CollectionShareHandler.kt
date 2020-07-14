package co.simonkenny.row.collectionsupport

import android.content.Context
import android.content.Intent
import android.util.Log

internal const val MIME_PLAIN_TEXT = "text/plain"

class CollectionShareHandler(
    context: Context,
    urls: List<String>
) {

    init {
        if (urls.isEmpty()) {
            Log.e(this::class.java.simpleName, "No urls to share");
        } else {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                urls.joinToString(separator = "\n* ", prefix = "* ", postfix = "\n")
            )
            shareIntent.type = MIME_PLAIN_TEXT
            context.startActivity(
                Intent.createChooser(
                    shareIntent,
                    context.resources.getString(R.string.collection_urls_share_dialog_title)
                )
            )
        }
    }
}