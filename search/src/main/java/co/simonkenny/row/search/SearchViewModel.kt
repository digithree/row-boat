package co.simonkenny.row.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.simonkenny.row.core.UiState
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

    private val _searchResultList = MutableLiveData<UiState<List<SearchResultItem>>>()
    val searchResultList: LiveData<UiState<List<SearchResultItem>>> = _searchResultList

    private val _errorEvent = MutableLiveData<Throwable>()
    val errorEvent: LiveData<Throwable> = _errorEvent

    fun search(query: String) {
        _searchResultList.postValue(UiState.Loading)
        viewModelScope.launch(dispatcher) {
            try {
                _searchResultList.postValue(UiState.Success(searchApi.search(query).results))
            } catch (e: Exception) {
                _errorEvent.postValue(e)
            }
        }
    }
}