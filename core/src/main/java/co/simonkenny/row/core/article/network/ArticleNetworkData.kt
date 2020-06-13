package co.simonkenny.row.core.article.network

internal data class ArticleNetworkData(
    override val url: String, //this is the ID used to communicate back to backend
    override val title: String?,
    override val attribution: String?,
    override val date: Long?,
    override val publisher: String?,
    override val body: String?
): IArticleNetworkDataSpec