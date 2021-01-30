package co.simonkenny.airtable

import android.content.Context
import co.simonkenny.row.core.article.ArticleRepo
import co.simonkenny.row.coresettings.SettingsRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AirtableExportHandler(
    private val context: Context,
    private val url: String,
    private val articleRepo: ArticleRepo,
    private val settingsRepo: SettingsRepo,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private val scope = CoroutineScope(dispatcher)

    init {
        scope.launch {
            // TODO
        }
    }
}