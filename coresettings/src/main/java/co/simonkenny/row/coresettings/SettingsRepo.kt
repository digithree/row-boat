package co.simonkenny.row.coresettings

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import co.simonkenny.row.util.RegisterableWithActivity

class SettingsRepo: RegisterableWithActivity() {

    private var sharedPreferences: SharedPreferences? = null

    override fun register(activity: AppCompatActivity) {
        super.register(activity)
        sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE)
    }

    override fun unregister(activity: AppCompatActivity) {
        super.unregister(activity)
        sharedPreferences = null
    }

    fun setPdfSettings(pdfSettingsData: PdfSettingsData) {
        checkPrefAccess()
        with (pdfSettingsData) {
            if (textSize == null && lineSpacingSize == null && marginVertSize == null && marginHorzSize == null) return
            with (requireNotNull(sharedPreferences).edit()) {
                textSize?.run { putInt(PREF_KEY_PDF_TEXT_SIZE, ordinal) }
                lineSpacingSize?.run { putInt(PREF_KEY_PDF_LINE_SPACING_SIZE, ordinal) }
                marginVertSize?.run { putInt(PREF_KEY_PDF_MARGIN_VERT_SIZE, ordinal) }
                marginHorzSize?.run { putInt(PREF_KEY_PDF_MARGIN_HORZ_SIZE, ordinal) }
                commit()
            }
        }
    }

    fun getPdfSettings(): PdfSettingsData {
        checkPrefAccess()
        return with (requireNotNull(sharedPreferences)) {
            PdfSettingsData(
                getInt(PREF_KEY_PDF_TEXT_SIZE, sizeDataDefaultOrdinal()).sizeDataFromOrdinal(),
                getInt(PREF_KEY_PDF_LINE_SPACING_SIZE, sizeDataDefaultOrdinal()).sizeDataFromOrdinal(),
                getInt(PREF_KEY_PDF_MARGIN_VERT_SIZE, sizeDataDefaultOrdinal()).sizeDataFromOrdinal(),
                getInt(PREF_KEY_PDF_MARGIN_HORZ_SIZE, sizeDataDefaultOrdinal()).sizeDataFromOrdinal()
            )
        }
    }

    fun setAirtableSettings(airtableSettingsData: AirtableSettingsData) {
        checkPrefAccess()
        with(airtableSettingsData) {
            if (tableUrl == null && apiKey == null) return
            with(requireNotNull(sharedPreferences).edit()) {
                tableUrl?.run { putString(PREF_KEY_AIRTABLE_TABLE_URL, this) }
                apiKey?.run { putString(PREF_KEY_AIRTABLE_API_KEY, this) }
                commit()
            }
        }
    }

    fun getAirtableSettings(): AirtableSettingsData {
        checkPrefAccess()
        return with(requireNotNull(sharedPreferences)) {
            AirtableSettingsData(
                getString(PREF_KEY_AIRTABLE_TABLE_URL, null),
                getString(PREF_KEY_AIRTABLE_API_KEY, null)
            )
        }
    }

    private fun checkPrefAccess() {
        if (!isRegistered) error { "Can't access SharedPreferences for settings, need to register Activity" }
        checkNotNull(sharedPreferences) { "SharedPreference creation error" }
    }
}