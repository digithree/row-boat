package co.simonkenny.row.readersupport

import android.content.res.Resources
import android.text.SpannableString
import android.text.Spanned
import co.simonkenny.row.base.toHtml
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

fun Article.toReaderDoc(resources: Resources): ReaderDoc {
    val dataFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val _title = title ?: resources.getString(R.string.reader_doc_no_title)

    val dateFormatted: String? = date?.run {
        dataFormatter.format(Date(this))
    }
    val _attribution = if (attribution != null && dateFormatted != null) {
        resources.getString(R.string.reader_doc_attribution_at_date_format, attribution, dateFormatted)
    } else if (attribution != null) {
        resources.getString(R.string.reader_doc_attribution_only_format, attribution)
        "Attribution: $attribution"
    } else if (dateFormatted != null) {
        resources.getString(R.string.reader_doc_attribution_date_only_format, dateFormatted)
    } else null

    val _publisher = publisher?.run {
        resources.getString(R.string.reader_doc_publisher_format, this)
    }

    val _body: Spanned = body
        ?.replace("\t", "")
        ?.replace(Regex.fromLiteral("(?m)(^ *| +(?= |$))"), "")
        ?.replace(Regex.fromLiteral("(?m)^$([\r\n]+?)(^$[\r\n]+?^)+"), "$1")
        ?.run { toHtml(this) }
            ?: SpannableString(resources.getString(R.string.reader_doc_no_body))

    return ReaderDoc(_title, url, _attribution, _publisher, _body)
}