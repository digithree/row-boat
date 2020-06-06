package co.simonkenny.row.search

internal data class SearchResultsResponse(
    val results: List<SearchResultItem>
)

internal data class SearchResultsWrapper(
    val query: String,
    val results: List<SearchResultItem>
)

internal data class SearchResultItem(
    val url: String,
    val title: String,
    val description: String
)