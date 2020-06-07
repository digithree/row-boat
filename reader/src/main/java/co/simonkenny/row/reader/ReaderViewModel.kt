package co.simonkenny.row.reader

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.simonkenny.row.core.UiState
import co.simonkenny.row.core.article.Article
import co.simonkenny.row.core.article.ArticleRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import retrofit2.Retrofit

class ReaderViewModel(
    private val articleRepo: ArticleRepo,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {

    private val _article = MutableLiveData<UiState<Article>>()
    val article: LiveData<UiState<Article>> = _article

    fun fetchArticle(url: String) {
        _article.postValue(UiState.Loading)
        viewModelScope.launch(dispatcher) {
            try {
                _article.postValue(UiState.Success(articleRepo.getArticle(url).await()))
            } catch (e: Exception) {
                _article.postValue(UiState.Error(e))
            }
        }
    }
}