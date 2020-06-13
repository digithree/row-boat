package co.simonkenny.row.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.simonkenny.row.base.SingleLiveEvent
import co.simonkenny.row.util.UiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit

internal class SearchViewModel(
    retrofit: Retrofit,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {

    // TODO : wrap this in Repo pattern
    private val searchApi = retrofit.create(SearchApi::class.java)

    private val _searchResults = MutableLiveData<UiState<SearchResultsWrapper>>()
    val searchResults: LiveData<UiState<SearchResultsWrapper>> = _searchResults

    private val _errorEvent = SingleLiveEvent<Throwable>()
    val errorEvent: LiveData<Throwable> = _errorEvent

    fun search(query: String) {
        _searchResults.postValue(UiState.Loading)
        viewModelScope.launch(dispatcher) {
            try {
                _searchResults.postValue(UiState.Success(
                    SearchResultsWrapper(
                        query,
                        searchApi.search(query).results
                    )
                ))
            } catch (e: Exception) {
                _errorEvent.postValue(e)
            }
        }
    }
}