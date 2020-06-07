package co.simonkenny.row.core.article.db

import androidx.room.*
import co.simonkenny.row.core.article.DbArticle

@Dao
internal interface ArticleDatabaseDao {

    @Insert
    fun insert(dbArticle: DbArticle)

    @Update
    fun update(dbArticle: DbArticle)

    @Delete
    fun delete(dbArticle: DbArticle)

    @Query("SELECT * from " + ArticleDatabase.TABLE_ARTICLE + " WHERE url = :key")
    fun get(key: String): DbArticle?

    @Query("SELECT * FROM " + ArticleDatabase.TABLE_ARTICLE + " ORDER BY added DESC")
    fun getAll(): List<DbArticle>

    @Query("DELETE FROM " + ArticleDatabase.TABLE_ARTICLE)
    fun clear()
}