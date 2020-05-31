package co.simonkenny.row.search

import retrofit2.http.GET
import retrofit2.http.Query

internal interface SearchApi {

    @GET("search-json")
    suspend fun search(@Query("q") query: String): SearchResultsResponse
}