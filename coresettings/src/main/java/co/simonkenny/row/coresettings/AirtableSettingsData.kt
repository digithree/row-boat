package co.simonkenny.row.coresettings

const val PREF_KEY_AIRTABLE_TABLE_URL = "pref_key_airtable_table_url"
const val PREF_KEY_AIRTABLE_API_KEY = "pref_key_airtable_api_key"

data class AirtableSettingsData(
    val tableUrl: String? = null,
    val apiKey: String? = null
)