package co.simonkenny.row.settings

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class SettingsViewModel(
    private val sharedPreferences: SharedPreferences
): ViewModel() {

    private val _pdfSettingsData = MutableLiveData<PdfSettingsData>()
    val pdfSettingsData: LiveData<PdfSettingsData> = _pdfSettingsData

    fun init() {
        postPdfSettings()
    }

    fun updatePdfSettings(pdfSettingsData: PdfSettingsData) {
        with (pdfSettingsData) {
            if (textSize == null && lineSpacingSize == null && marginVertSize == null && marginHorzSize == null) return
            with (sharedPreferences.edit()) {
                textSize?.run { putInt(PREF_KEY_PDF_TEXT_SIZE, ordinal) }
                lineSpacingSize?.run { putInt(PREF_KEY_PDF_LINE_SPACING_SIZE, ordinal) }
                marginVertSize?.run { putInt(PREF_KEY_PDF_MARGIN_VERT_SIZE, ordinal) }
                marginHorzSize?.run { putInt(PREF_KEY_PDF_MARGIN_HORZ_SIZE, ordinal) }
                commit()
            }
        }
        postPdfSettings()
    }

    private fun postPdfSettings() {
        with (sharedPreferences) {
            _pdfSettingsData.postValue(
                PdfSettingsData(
                    getInt(PREF_KEY_PDF_TEXT_SIZE, SizeData.SIZE_MEDIUM.ordinal).sizeDataFromOrdinal(),
                    getInt(PREF_KEY_PDF_LINE_SPACING_SIZE, SizeData.SIZE_MEDIUM.ordinal).sizeDataFromOrdinal(),
                    getInt(PREF_KEY_PDF_MARGIN_VERT_SIZE, SizeData.SIZE_MEDIUM.ordinal).sizeDataFromOrdinal(),
                    getInt(PREF_KEY_PDF_MARGIN_HORZ_SIZE, SizeData.SIZE_MEDIUM.ordinal).sizeDataFromOrdinal()
                )
            )
        }
    }
}