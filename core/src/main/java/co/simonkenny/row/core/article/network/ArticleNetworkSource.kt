package co.simonkenny.row.core.article.network

import retrofit2.Retrofit

internal class ArticleNetworkSource(
    retrofit: Retrofit
) {

    private val articleNetworkApi = retrofit.create(ArticleNetworkApi::class.java)

    suspend fun fetchArticle(url: String) = articleNetworkApi.fetchArticle(url)
}