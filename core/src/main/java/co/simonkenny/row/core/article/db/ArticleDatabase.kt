package co.simonkenny.row.core.article.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import co.simonkenny.row.core.article.Article

@Database(entities = [Article::class], version = 1, exportSchema = false)
internal abstract class ArticleDatabase: RoomDatabase() {

    abstract val articleDatabaseDao: ArticleDatabaseDao

    companion object {
        const val DB = "article_database"
        const val TABLE_ARTICLE = "article_table"

        @Volatile
        private var INSTANCE: ArticleDatabase? = null

        fun getInstance(context: Context): ArticleDatabase {
            synchronized(this) {
                var instance =
                    INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            ArticleDatabase::class.java,
                        DB
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}