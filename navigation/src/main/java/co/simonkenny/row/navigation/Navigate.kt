package co.simonkenny.row.navigation

import android.content.Context
import android.content.Intent

const val DESTINATION_READER = "destination_reader"
const val DESTINATION_SEARCH = "destination_search"
const val DIALOG_ADD_TO_COLLECTION = "dialog_add_to_collection"

const val ARG_URL = "arg_url"
const val ARG_QUERY = "arg_query"

class Navigate {

    companion object {
        fun toReader(context: Context, url: String) {
            context.sendBroadcast(internalIntent(context, DESTINATION_READER)
                .putExtra(ARG_URL, url))
        }

        fun toSearch(context: Context, query: String) {
            context.sendBroadcast(internalIntent(context, DESTINATION_SEARCH)
                .putExtra(ARG_QUERY, query))
        }

        fun addToCollection(context: Context, url: String) {
            context.sendBroadcast(internalIntent(context, DIALOG_ADD_TO_COLLECTION)
                .putExtra(ARG_URL, url))
        }

        private fun internalIntent(context: Context, action: String) =
            Intent(action).apply {
                `package` = context.packageName
            }
    }
}