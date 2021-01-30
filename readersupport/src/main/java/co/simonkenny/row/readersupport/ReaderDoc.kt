package co.simonkenny.row.readersupport

import android.content.res.Resources
import android.text.SpannableString
import android.text.Spanned
import co.simonkenny.row.base.fromHtml
import co.simonkenny.row.core.article.Article
import java.text.SimpleDateFormat
import java.util.*

data class ReaderDoc(
    val title: String,
    val url: String,
    val attribution: String?,
    val publisher: String?,
    val body: Spanned
)

private val dataFormatter = SimpleDateFormat("cccc d MMMM yyyy", Locale.US)

fun Article.toReaderDoc(resources: Resources): ReaderDoc {
    val dateFormatted: String? = date?.run {
        dataFormatter.format(Date(this * 1000L))
    }

    return ReaderDoc(
        title ?: resources.getString(R.string.reader_doc_no_title),
        url,
        if (attribution != null && dateFormatted != null) {
            resources.getString(R.string.reader_doc_attribution_at_date_format, attribution, dateFormatted)
        } else if (attribution != null) {
            resources.getString(R.string.reader_doc_attribution_only_format, attribution)
            "Attribution: $attribution"
        } else if (dateFormatted != null) {
            resources.getString(R.string.reader_doc_attribution_date_only_format, dateFormatted)
        } else null,
        publisher?.run {
            resources.getString(R.string.reader_doc_publisher_format, this)
        },
        body?.fromHtml() ?: SpannableString(resources.getString(R.string.reader_doc_no_body))
    )
}