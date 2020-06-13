package co.simonkenny.row.settings

internal const val PREF_KEY_PDF_TEXT_SIZE = "pref_key_pdf_text_size"
internal const val PREF_KEY_PDF_LINE_SPACING_SIZE = "pref_key_pdf_line_spacing_size"
internal const val PREF_KEY_PDF_MARGIN_VERT_SIZE = "pref_key_pdf_margin_vert_size"
internal const val PREF_KEY_PDF_MARGIN_HORZ_SIZE = "pref_key_pdf_margin_horz_size"

data class PdfSettingsData(
    val textSize: SizeData? = null,
    val lineSpacingSize: SizeData? = null,
    val marginVertSize: SizeData? = null,
    val marginHorzSize: SizeData? = null
)