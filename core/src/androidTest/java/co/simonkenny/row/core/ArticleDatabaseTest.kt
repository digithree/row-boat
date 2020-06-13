package co.simonkenny.row.core

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import co.simonkenny.row.core.article.*
import co.simonkenny.row.core.article.db.ArticleDatabase
import co.simonkenny.row.core.article.db.ArticleDatabaseDao
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

@RunWith(AndroidJUnit4::class)
class SleepDatabaseTest {

    private lateinit var articleDao: ArticleDatabaseDao
    private lateinit var db: ArticleDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, ArticleDatabase::class.java)
                // Allowing main thread queries, just for testing.
                .allowMainThreadQueries()
                .build()
        articleDao = db.articleDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetArticleUsingUrl() {
        val article = Article(
            "A Really Cool Article",
            Date().time,
            "https://example.com/path/to/cool/article",
            "by A Cool Writer",
            Date().time,
            "Cool Publishing Inc.",
            "This should be some cool body of text",
            "tag1,tag2,tag3"
        )
        articleDao.insert(article)
        val articleFromDb = articleDao.get("https://example.com/path/to/cool/article")
        assertEquals(articleFromDb?.url, "https://example.com/path/to/cool/article")
    }
}