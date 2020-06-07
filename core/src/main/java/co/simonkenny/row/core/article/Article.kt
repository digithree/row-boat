package co.simonkenny.row.core.article

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

// Communicated to feature modules
data class Article (
    override val url: String,
    val added: Long? = null,
    override val title: String? = null,
    override val attribution: String? = null,
    override val date: Long? = null,
    override val publisher: String? = null,
    override val body: String? = null,
    val tags: String? = null,
    val permission: String? = null
): IArticle

// used for DB
@Entity(tableName = ArticleDatabase.TABLE_ARTICLE)
internal data class DbArticle (
    @PrimaryKey
    override val url: String,
    val added: Long,
    override val title: String?,
    override val attribution: String?,
    override val date: Long?,
    override val publisher: String?,
    override val body: String?,
    val tags: String?,
    val permission: String?
): IArticle

internal fun Article.toDbArticle(added: Long? = null): DbArticle =
    DbArticle(url, this.added ?: added ?: Date().time, title, attribution, date, publisher, body, tags, permission)

internal fun DbArticle.toArticle(): Article =
    Article(url, added, title, attribution, date, publisher, body, tags, permission)

// this is all we get from the ROW service
internal interface IArticle {
    val url: String //this is the ID used to communicate back to backend
    val title: String?
    val attribution: String?
    val date: Long?
    val publisher: String?
    val body: String?
}