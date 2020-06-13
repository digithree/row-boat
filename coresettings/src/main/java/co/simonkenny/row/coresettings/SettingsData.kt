package co.simonkenny.row.coresettings

enum class SizeData {
    SIZE_SMALL, SIZE_MEDIUM, SIZE_LARGE
}

fun Int.sizeDataFromOrdinal() = SizeData.values()[this]
