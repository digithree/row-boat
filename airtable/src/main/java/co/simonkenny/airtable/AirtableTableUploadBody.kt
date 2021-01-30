package co.simonkenny.airtable

import co.simonkenny.row.base.cleanHtml
import co.simonkenny.row.base.fromHtml
import co.simonkenny.row.core.article.Article
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

data class AirtableTableUploadBody(
    val records: List<ArticleTableFieldsWrapper>
)


data class ArticleTableFieldsWrapper(
    val fields: ArticleTableFields
)

data class ArticleTableFields(
    //val id: String, -- automatically set on server side with Autonumber
    val url: String,
    val title: String,
    val description: String?,
    val published: String?,
    val added: String,
    val nsfw: Boolean?,
    val comment: String?
)

private val paragraphTagPattern = Pattern.compile("<p>(.+?)</p>", Pattern.DOTALL)
private val dataFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)

internal fun Article.toFields(comment: String? = null, nsfw: Boolean? = null) =
    ArticleTableFields(
        url,
        title ?: "[No Title]",
        body?.cleanHtml()?.let { paragraphTagPattern.matcher(it) }?.run {
            if (find()) group(1).fromHtml().toString()
            else null
        },
        date?.let { dataFormatter.format(Date(it * 1000L)) },
        dataFormatter.format(Date()),
        nsfw,
        comment
    )