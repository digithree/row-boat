package co.simonkenny.row.collection

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

class CollectionBrowseViewModel(
    private val articleRepo: ArticleRepo,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {

    private val _articlesList = MutableLiveData<UiState<List<Article>>>()
    val articlesList: LiveData<UiState<List<Article>>> = _articlesList

    fun fetchArticles(from: Article? = null) {
        viewModelScope.launch(dispatcher) {
            try {
                _articlesList.postValue(UiState.Success(
                    articleRepo.getArticleList(from).await()
                ))
            } catch (e: Exception) {
                _articlesList.postValue(UiState.Error(e))
            }
        }
    }
}