package co.simonkenny.row.core.article.network

internal interface IArticleNetworkDataSpec {
    val url: String //this is the ID used to communicate back to backend
    val title: String?
    val attribution: String?
    val date: Long?
    val publisher: String?
    val body: String?
}