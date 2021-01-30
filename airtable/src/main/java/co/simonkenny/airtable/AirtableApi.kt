package co.simonkenny.airtable

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

interface AirtableApi {

    @POST
    suspend fun upload(@Url tableUrl: String, @Header("Authorization") apiKey: String, @Body body: AirtableTableUploadBody)
}