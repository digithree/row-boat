package co.simonkenny.airtable

import android.content.Context
import android.util.Log
import android.widget.Toast
import co.simonkenny.row.core.article.ArticleRepo
import co.simonkenny.row.core.article.RepoFetchOptions
import co.simonkenny.row.coresettings.SettingsRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import java.lang.Exception

class AirtableExportHandler(
    private val context: Context,
    private val url: String,
    private val articleRepo: ArticleRepo,
    private val settingsRepo: SettingsRepo,
    private val airtableRepo: AirtableRepo,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private val scope = CoroutineScope(dispatcher)

    init {
        scope.launch {
            val settings = settingsRepo.getAirtableSettings()
            if (settings.apiKey == null || settings.tableUrl == null) {
                launch(Dispatchers.Main.immediate) {
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.airtable_share_error_settings),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return@launch
            }
            try {
                airtableRepo.upload(
                    settings.tableUrl!!,
                    settings.apiKey!!,
                    articleRepo.getArticle(url, RepoFetchOptions(network = false)).await()
                )
                launch(Dispatchers.Main.immediate) {
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.airtable_share_success),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                launch(Dispatchers.Main.immediate) {
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.airtable_share_error_general),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}