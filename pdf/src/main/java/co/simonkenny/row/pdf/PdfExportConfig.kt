package co.simonkenny.row.pdf

import co.simonkenny.row.coresettings.PdfSettingsData
import co.simonkenny.row.coresettings.sizeDataDefaultOrdinal


data class PdfExportConfig(
    val fontSizeTitle: Float,
    val fontSizeInfo: Float,
    val fontSizeBody: Float,
    val lineSpacingTitle: Float,
    val lineSpacingInfo: Float,
    val lineSpacingBody: Float,
    val vertMargins: Float,
    val horzMargins: Float
)

internal fun PdfSettingsData.toExportConfig() =
    PdfExportConfig(
        FONT_SIZE_TITLE_LIST[textSize?.ordinal ?: sizeDataDefaultOrdinal()],
        FONT_SIZE_INFO_LIST[textSize?.ordinal ?: sizeDataDefaultOrdinal()],
        FONT_SIZE_BODY_LIST[textSize?.ordinal ?: sizeDataDefaultOrdinal()],
        LINE_SPACING_TITLE_LIST[textSize?.ordinal ?: sizeDataDefaultOrdinal()],
        LINE_SPACING_INFO_LIST[textSize?.ordinal ?: sizeDataDefaultOrdinal()],
        LINE_SPACING_BODY_LIST[lineSpacingSize?.ordinal ?: sizeDataDefaultOrdinal()]
            * LINE_SPACING_BODY_ADJUST_MUL_LIST[textSize?.ordinal ?: sizeDataDefaultOrdinal()],
        VERT_MARGINS_LIST[marginVertSize?.ordinal ?: sizeDataDefaultOrdinal()],
        HORZ_MARGINS_LIST[marginHorzSize?.ordinal ?: sizeDataDefaultOrdinal()]
    )