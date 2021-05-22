package co.simonkenny.row.navigation

import android.content.Context
import android.content.Intent

const val DESTINATION_READER = "destination_reader"
const val DESTINATION_SEARCH = "destination_search"
const val DESTINATION_SETTINGS = "destination_settings"
const val DIALOG_ADD_TO_COLLECTION = "dialog_add_to_collection"
const val ACTION_EXPORT_TO_PDF = "action_export_to_pdf"
const val ACTION_UPLOAD_TO_AIRTABLE = "action_upload_to_airtable"

val NAVIGATION_ENDPOINTS = listOf(
    DESTINATION_READER,
    DESTINATION_SEARCH,
    DESTINATION_SETTINGS,
    DIALOG_ADD_TO_COLLECTION,
    ACTION_EXPORT_TO_PDF,
    ACTION_UPLOAD_TO_AIRTABLE
)

const val ARG_URL = "arg_url"
const val ARG_TITLE = "arg_title"
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

        fun toSettings(context: Context) {
            context.sendBroadcast(internalIntent(context, DESTINATION_SETTINGS))
        }

        fun addToCollection(context: Context, url: String, title: String? = null) {
            context.sendBroadcast(internalIntent(context, DIALOG_ADD_TO_COLLECTION)
                .putExtra(ARG_URL, url).apply {
                    title?.run { putExtra(ARG_TITLE, this) }
                })
        }

        fun exportToPdf(context: Context, url: String) {
            context.sendBroadcast(internalIntent(context, ACTION_EXPORT_TO_PDF)
                .putExtra(ARG_URL, url))
        }

        fun uploadToAirtable(context: Context, url: String) {
            context.sendBroadcast(internalIntent(context, ACTION_UPLOAD_TO_AIRTABLE)
                .putExtra(ARG_URL, url))
        }


        // private helpers

        private fun internalIntent(context: Context, action: String) =
            Intent(action).apply {
                `package` = context.packageName
            }
    }
}