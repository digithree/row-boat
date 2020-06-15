package co.simonkenny.row.core.article

import androidx.room.Entity
import androidx.room.PrimaryKey
import co.simonkenny.row.core.article.db.ArticleDatabase
import co.simonkenny.row.core.article.network.ArticleNetworkData
import co.simonkenny.row.core.article.network.IArticleNetworkDataSpec

// used for DB
@Entity(tableName = ArticleDatabase.TABLE_ARTICLE)
data class Article (
    @PrimaryKey
    override val url: String,
    val added: Long? = null,
    override val title: String? = null,
    override val attribution: String? = null,
    override val date: Long? = null,
    override val publisher: String? = null,
    override val body: String? = null,
    val tags: String? = null,
    val permission: String? = null,
    val read: Boolean? = null
): IArticleNetworkDataSpec

internal fun ArticleNetworkData.toArticle() =
    Article(
        url = url,
        title = title,
        attribution = attribution,
        date = date,
        publisher = publisher,
        body = body
    )

fun Article.replaceTags(tags: String?): Article =
    Article(url, added, title, attribution, date, publisher, body, tags, permission, read)

fun Article.replaceTitle(title: String?): Article =
    Article(url, added, title, attribution, date, publisher, body, tags, permission, read)

fun Article.replaceRead(read: Boolean): Article =
    Article(url, added, title, attribution, date, publisher, body, tags, permission, read)