package co.simonkenny.row.pdf

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import co.simonkenny.row.core.FILE_PROVIDER_AUTHORITY
import co.simonkenny.row.core.article.ArticleRepo
import co.simonkenny.row.core.article.RepoFetchOptions
import co.simonkenny.row.coresettings.PdfSettingsData
import co.simonkenny.row.coresettings.SettingsRepo
import co.simonkenny.row.readersupport.ReaderDoc
import co.simonkenny.row.readersupport.toReaderDoc
import kotlinx.coroutines.*
import kotlinx.coroutines.rx2.await

class PdfExportHandler(
    private val context: Context,
    private val url: String,
    private val articleRepo: ArticleRepo,
    private val settingsRepo: SettingsRepo,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private val scope = CoroutineScope(dispatcher)

    init {
        scope.launch {
            val readerDoc: ReaderDoc
            val pdfExportConfig: PdfExportConfig
            try {
                readerDoc = articleRepo.getArticle(url, RepoFetchOptions(network = false))
                    .await()
                    .toReaderDoc(context.resources)
                pdfExportConfig = settingsRepo.getPdfSettings().toExportConfig()
            } catch (e: Exception) {
                Log.e("PdfExportHandler", "Couldn't get article for URL")
                Toast.makeText(context,
                        context.resources.getString(R.string.pdf_share_error_message),
                        Toast.LENGTH_LONG)
                    .show()
                return@launch
            }
            PdfConverter(context, readerDoc, pdfExportConfig)
                .save()
                ?.run {
                    val uri = FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, this)
                    val shareIntent = Intent()
                    shareIntent.action = Intent.ACTION_SEND
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                    shareIntent.type = MIME_TYPE_PDF
                    context.startActivity(Intent.createChooser(shareIntent,
                        context.resources.getString(R.string.pdf_share_dialog_title)))
                } ?: postToast(context.resources.getString(R.string.pdf_share_error_message))
        }
    }

    private fun postToast(message: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(
                context,
                message,
                Toast.LENGTH_LONG
            ).show()
        }
    }
}