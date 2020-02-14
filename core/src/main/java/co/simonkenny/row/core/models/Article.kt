package co.simonkenny.row.core.models

// Communicated to feature modules
data class Article (
    override val title: String,
    override val url: String,
    override val attribution: String,
    override val publisher: String,
    override val body: HtmlBody
): IArticle

data class HtmlBody(val html: String): IBody

// used for DB
internal data class FileArticle (
    override val title: String,
    override val url: String,
    override val attribution: String,
    override val publisher: String,
    override val body: FilePathBody
): IArticle

internal interface IArticle {
    val title: String
    val url: String
    val attribution: String
    val publisher: String
    val body: IBody
}

internal class FilePathBody(val path: String): IBody

interface IBody