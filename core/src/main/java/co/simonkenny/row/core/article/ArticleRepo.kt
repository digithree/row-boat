package co.simonkenny.row.core.article

import android.app.Application
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import java.util.*

class ArticleRepo {

    private var articleDatabase: ArticleDatabase? = null

    fun init(application: Application) {
        if (articleDatabase == null) {
            articleDatabase = ArticleDatabase.getInstance(application)
        }
    }

    fun addArticle(article: Article): Completable {
        dbCheck()

        return Observable.fromCallable {
            articleDatabase?.articleDatabaseDao?.insert(article.toDbArticle(Date().time))
                ?: error { "Could not add Article" }
        }.ignoreElements()
    }

    fun getArticleList(from: Article? = null): Single<List<Article>> {
        dbCheck()

        // TODO : use from value and page
        return Single.fromCallable {
            articleDatabase?.articleDatabaseDao?.getAll()
                ?.map { it.toArticle() }
                ?: error { "Could not get Articles" }
        }
    }

    private fun dbCheck() {
        if (articleDatabase == null) {
            error { "Article DB was not initialised" }
        }
    }
}