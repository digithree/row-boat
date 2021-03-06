package co.simonkenny.row.reader

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.simonkenny.row.base.SingleLiveEvent
import co.simonkenny.row.core.article.ArticleRepo
import co.simonkenny.row.core.article.RepoFetchOptions
import co.simonkenny.row.core.article.replaceRead
import co.simonkenny.row.core.article.replaceScroll
import co.simonkenny.row.readersupport.ReaderDoc
import co.simonkenny.row.readersupport.toReaderDoc
import co.simonkenny.row.util.UiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await

class ReaderViewModel(
    private val resources: Resources,
    private val articleRepo: ArticleRepo,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {

    private val _readerDoc = MutableLiveData<UiState<ReaderDoc>>()
    val readerDoc: LiveData<UiState<ReaderDoc>> = _readerDoc

    private val _readerTransientError = SingleLiveEvent<Exception>()
    val readerTransientError: LiveData<Exception> = _readerTransientError

    fun fetchArticle(url: String) {
        _readerDoc.postValue(UiState.Loading)
        viewModelScope.launch(dispatcher) {
            try {
                val article = articleRepo.getArticle(url).await().takeIf { it.body != null }
                    ?: articleRepo.getArticle(url, RepoFetchOptions(db = false, mem = false)).await()
                _readerDoc.postValue(UiState.Success(article.toReaderDoc(resources)))
            } catch (e: Exception) {
                _readerDoc.postValue(UiState.Error(e))
                _readerTransientError.postValue(e)
            }
        }
    }

    fun updateArticleScroll(url: String, scroll: Int) {
        viewModelScope.launch(dispatcher) {
            try {
                articleRepo.updateLocalArticle(
                    articleRepo.getArticle(url, RepoFetchOptions(network = false))
                        .await()
                        .replaceScroll(scroll)
                ).await()
            } catch (e: Exception) {
                e.printStackTrace()
                // silent
            }
            fetchArticle(url)
        }
    }
}