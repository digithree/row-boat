package co.simonkenny.row.core.article

data class RepoFetchOptions(
    val network: Boolean = true,
    val db: Boolean = true,
    val mem: Boolean = true,
    val errorOnFail: Boolean = true
)