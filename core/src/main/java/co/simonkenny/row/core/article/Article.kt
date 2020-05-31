package co.simonkenny.row.core.article

import androidx.room.Entity
import androidx.room.PrimaryKey

// Communicated to feature modules
data class Article (
    override val title: String?,
    override val url: String,
    override val attribution: String?,
    override val date: Long?,
    override val publisher: String?,
    override val body: String?
): IArticle

// used for DB
@Entity(tableName = ArticleDatabase.TABLE_ARTICLE)
internal data class DbArticle (
    @PrimaryKey(autoGenerate = true)
    var dbId: Long = 0L,
    override val title: String?,
    override val url: String,
    override val attribution: String?,
    override val date: Long?,
    override val publisher: String?,
    override val body: String?
): IArticle

internal fun Article.toDbArticle(dbId: Long = 0L): DbArticle {
    return DbArticle(dbId, title, url, attribution, date, publisher, body)
}

internal interface IArticle {
    val title: String?
    val url: String //this is the ID used to communicate back to backend
    val attribution: String?
    val date: Long?
    val publisher: String?
    val body: String?
}