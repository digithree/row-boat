package co.simonkenny.row.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.simonkenny.row.coresettings.*
import co.simonkenny.row.util.UiState


class SettingsViewModel(
    private val settingsRepo: SettingsRepo
): ViewModel() {

    private val _pdfSettingsData = MutableLiveData<UiState<PdfSettingsData>>()
    val pdfSettingsData: LiveData<UiState<PdfSettingsData>> = _pdfSettingsData

    private val _airtableSettingsData = MutableLiveData<UiState<AirtableSettingsData>>()
    val airtableSettingsData: LiveData<UiState<AirtableSettingsData>> = _airtableSettingsData

    fun init() {
        postPdfSettings()
        postAirtableSettings()
    }

    fun updatePdfSettings(pdfSettingsData: PdfSettingsData) {
        settingsRepo.setPdfSettings(pdfSettingsData)
        postPdfSettings()
    }

    fun updateAirtableSettings(airtableSettingsData: AirtableSettingsData) {
        settingsRepo.setAirtableSettings(airtableSettingsData)
        // don't post changes, does not work with EditText
    }

    private fun postPdfSettings() {
        try {
            _pdfSettingsData.postValue(UiState.Success(settingsRepo.getPdfSettings()))
        } catch (e: Exception) {
            _pdfSettingsData.postValue(UiState.Error(e))
        }
    }

    private fun postAirtableSettings() {
        try {
            _airtableSettingsData.postValue(UiState.Success(settingsRepo.getAirtableSettings()))
        } catch (e: Exception) {
            _airtableSettingsData.postValue(UiState.Error(e))
        }
    }
}