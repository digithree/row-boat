package co.simonkenny.row.collection.browse

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

    private val _articleList = MutableLiveData<UiState<List<Article>>>()
    val articleList: LiveData<UiState<List<Article>>> = _articleList

    fun fetchArticles(from: Article? = null) {
        viewModelScope.launch(dispatcher) {
            try {
                _articleList.postValue(UiState.Success(
                    articleRepo.getLocalArticleList(from).await()
                ))
            } catch (e: Exception) {
                _articleList.postValue(UiState.Error(e))
            }
        }
    }
}