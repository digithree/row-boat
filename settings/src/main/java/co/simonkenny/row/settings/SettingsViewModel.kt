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

    fun init() {
        postPdfSettings()
    }

    fun updatePdfSettings(pdfSettingsData: PdfSettingsData) {
        settingsRepo.setPdfSettings(pdfSettingsData)
        postPdfSettings()
    }

    private fun postPdfSettings() {
        try {
            _pdfSettingsData.postValue(UiState.Success(settingsRepo.getPdfSettings()))
        } catch (e: Exception) {
            _pdfSettingsData.postValue(UiState.Error(e))
        }
    }
}