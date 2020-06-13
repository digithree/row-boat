package co.simonkenny.row.core.article.db

import androidx.room.*
import co.simonkenny.row.core.article.Article

@Dao
internal interface ArticleDatabaseDao {

    @Insert
    fun insert(article: Article)

    @Update
    fun update(article: Article)

    @Delete
    fun delete(article: Article)

    @Query("SELECT * from " + ArticleDatabase.TABLE_ARTICLE + " WHERE url = :key")
    fun get(key: String): Article?

    @Query("SELECT * FROM " + ArticleDatabase.TABLE_ARTICLE + " ORDER BY added DESC")
    fun getAll(): List<Article>

    @Query("DELETE FROM " + ArticleDatabase.TABLE_ARTICLE)
    fun clear()
}