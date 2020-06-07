package co.simonkenny.row.collection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.simonkenny.row.core.UiState
import co.simonkenny.row.core.article.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await

class AddToCollectionViewModel(
    private val articleRepo: ArticleRepo,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {

    private val _addUiState = MutableLiveData<UiState<Any>>()
    val addUiState: LiveData<UiState<Any>> = _addUiState

    fun add(url: String, title: String? = null, tagsList: List<String>? = null) {
        viewModelScope.launch(dispatcher) {
            try {
                articleRepo.addLocalArticle(
                    articleRepo.getArticle(url, RepoFetchOptions(network = false, errorOnFail = false)).await()
                        .run {
                            title?.run { replaceTitle(this) } ?: this
                        }.run {
                            tagsList?.run { replaceTags(joinToString(",")) } ?: this
                        }
                ).await()
                _addUiState.postValue(UiState.Success(Any()))
            } catch (e: Exception) {
                _addUiState.postValue(UiState.Error(e))
            }
        }
    }
}