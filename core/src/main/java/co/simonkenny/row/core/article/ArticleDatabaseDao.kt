package co.simonkenny.row.core.article

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
internal interface ArticleDatabaseDao {

    @Insert
    fun insert(dbArticle: DbArticle)

    @Update
    fun update(dbArticle: DbArticle)

    @Query("SELECT * from " + ArticleDatabase.TABLE_ARTICLE + " WHERE dbId = :key")
    fun get(key: Long): DbArticle?

    @Query("SELECT * from " + ArticleDatabase.TABLE_ARTICLE + " WHERE url = :key")
    fun get(key: String): DbArticle?

    @Query("SELECT * FROM " + ArticleDatabase.TABLE_ARTICLE + " ORDER BY dbId DESC")
    fun getAll(): LiveData<List<DbArticle>>

    @Query("DELETE FROM " + ArticleDatabase.TABLE_ARTICLE)
    fun clear()
}