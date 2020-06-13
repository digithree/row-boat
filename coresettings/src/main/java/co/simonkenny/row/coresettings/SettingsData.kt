package co.simonkenny.row.coresettings

enum class SizeData {
    SIZE_SMALL, SIZE_MEDIUM, SIZE_LARGE
}

fun Int.sizeDataFromOrdinal() = SizeData.values()[this]

fun sizeDataDefault() = SizeData.SIZE_MEDIUM

fun sizeDataDefaultOrdinal(): Int = SizeData.SIZE_MEDIUM.ordinal