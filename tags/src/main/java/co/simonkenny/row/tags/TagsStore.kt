package co.simonkenny.row.tags

/**
 * In future this will be editable, for now it just provides a static list.
 */
class TagsStore private constructor() {

    companion object {
        val instance: TagsStore = TagsStore()
    }

    private val basicTags = listOf(
        "news",
        "art",
        "culture",
        "design",
        "economics",
        "education",
        "food",
        "future",
        "gaming",
        "health",
        "history",
        "ideas",
        "funny",
        "inspiration",
        "literature",
        "parenting",
        "politics",
        "privacy",
        "productivity",
        "psychology",
        "satire",
        "science",
        "socialmedia",
        "space",
        "technology",
        "music",
        "future",
        "gender",
        "motivation",
        "medicine",
        "advice",
        "dev",
        "fun",
        "food",
        "fitness"
    ).sorted()

    fun getAllTags() = basicTags
}