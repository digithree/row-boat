package co.simonkenny.row.core.article.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import co.simonkenny.row.core.article.Article

@Database(entities = [Article::class], version = 2, exportSchema = false)
internal abstract class ArticleDatabase: RoomDatabase() {

    abstract val articleDatabaseDao: ArticleDatabaseDao

    companion object {
        const val DB = "article_database"
        const val TABLE_ARTICLE = "article_table"

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE $TABLE_ARTICLE ADD COLUMN scroll INTEGER")
            }
        }

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
                        .addMigrations(MIGRATION_1_2)
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}