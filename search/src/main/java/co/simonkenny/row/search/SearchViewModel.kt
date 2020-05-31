package co.simonkenny.row.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.simonkenny.row.core.ROW_BASE_URL
import co.simonkenny.row.core.UiState
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

internal class SearchViewModel(
    private val retrofit: Retrofit,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {

    // TODO : wrap this in Repo pattern
    private val searchApi = retrofit.create(SearchApi::class.java)

    private val _searchResultList = MutableLiveData<UiState<List<SearchResultItem>>>()
    val searchResultList: LiveData<UiState<List<SearchResultItem>>> = _searchResultList

    fun search(query: String) {
        _searchResultList.postValue(UiState.Loading)
        viewModelScope.launch(dispatcher) {
            try {
                _searchResultList.postValue(UiState.Success(searchApi.search(query).results))
            } catch (e: Exception) {
                _searchResultList.postValue(UiState.Error(e))
            }
        }
    }
}