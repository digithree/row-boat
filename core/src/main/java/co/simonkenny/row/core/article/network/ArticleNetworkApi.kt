package co.simonkenny.row.core.article.network

import co.simonkenny.row.core.article.Article
import retrofit2.http.GET
import retrofit2.http.Query

internal interface ArticleNetworkApi {

    @GET("url-json")
    suspend fun fetchArticle(@Query("q") url: String): Article
}