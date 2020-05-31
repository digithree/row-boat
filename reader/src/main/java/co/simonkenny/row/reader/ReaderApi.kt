package co.simonkenny.row.reader

import co.simonkenny.row.core.article.Article
import retrofit2.http.GET
import retrofit2.http.Query

interface ReaderApi {

    @GET("url-json")
    suspend fun fetchArticle(@Query("q") url: String): Article
}