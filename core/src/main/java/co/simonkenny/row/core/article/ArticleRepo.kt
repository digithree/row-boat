package co.simonkenny.row.core.article

import android.app.Application
import co.simonkenny.row.core.article.db.ArticleDatabase
import co.simonkenny.row.core.article.network.ArticleNetworkSource
import co.simonkenny.row.core.di.FakeDI
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.runBlocking
import java.util.*

private const val MAX_MEM_CACHE_SIZE = 10

class ArticleRepo {

    private var articleDatabase: ArticleDatabase? = null

    private val networkSource by lazy {
        ArticleNetworkSource(FakeDI.instance.retrofit)
    }

    fun init(application: Application) {
        if (articleDatabase == null) {
            articleDatabase = ArticleDatabase.getInstance(application)
        }
    }

    // custom fixed size queue
    private val articleMemCacheQueue: Queue<Article> =
        object : LinkedList<Article>() {
            override fun add(element: Article): Boolean {
                if (super.size >= MAX_MEM_CACHE_SIZE) {
                    super.removeFirst()
                }
                return super.add(element)
            }
        }

    private fun addToMemCache(article: Article) {
        if (articleMemCacheQueue.contains(article)) {
            // keeps recently added items at top, so old items are pushed out
            articleMemCacheQueue.remove(article)
        }
        articleMemCacheQueue.add(article)
    }

    // general

    fun getArticle(url: String, repoFetchOptions: RepoFetchOptions? = null): Single<Article> {
        return Single.fromCallable {
            runBlocking {
                var article: Article? = null
                if (repoFetchOptions?.db != false) {
                    article = articleDatabase?.articleDatabaseDao?.get(url)?.toArticle()
                }
                if (article == null && repoFetchOptions?.mem != false) {
                    article = articleMemCacheQueue.find { it.url == url }
                }
                if (article == null && repoFetchOptions?.network != false) {
                    article = networkSource.fetchArticle(url)
                        .apply { addToMemCache(this) } // cache network only
                }
                if (article == null && repoFetchOptions?.errorOnFail != true) {
                    article = Article(url)
                }
                article
            }
        }
    }


    // local articles

    fun addLocalArticle(article: Article): Completable {
        dbCheck()
        return Observable.fromCallable {
            articleDatabase?.articleDatabaseDao?.insert(article.toDbArticle(Date().time))
                ?: error { "Could not add Article" }
        }.ignoreElements()
    }

    fun getLocalArticleList(from: Article? = null): Single<List<Article>> {
        dbCheck()
        // TODO : use from value and page
        return Single.fromCallable {
            articleDatabase?.articleDatabaseDao?.getAll()
                ?.map { it.toArticle() }
                ?: error { "Could not get Articles" }
        }
    }

    fun deleteLocalArticle(url: String): Single<Article> {
        dbCheck()
        return Single.fromCallable {
            articleDatabase?.articleDatabaseDao?.get(url)?.run {
                articleDatabase?.articleDatabaseDao?.delete(this)
                toArticle()
            } ?: error { "Could not add Article" }
        }
    }

    private fun dbCheck() {
        if (articleDatabase == null) {
            error { "Article DB was not initialised" }
        }
    }
}