package co.simonkenny.airtable

import co.simonkenny.row.core.article.Article
import retrofit2.Retrofit

class AirtableRepo(
    retrofit: Retrofit
) {

    private val api = retrofit.create(AirtableApi::class.java)

    suspend fun upload(tableUrl: String, apiKey: String, article: Article) {
        api.upload(tableUrl, "Bearer $apiKey", AirtableTableUploadBody(listOf(ArticleTableFieldsWrapper(article.toFields()))))
    }
}