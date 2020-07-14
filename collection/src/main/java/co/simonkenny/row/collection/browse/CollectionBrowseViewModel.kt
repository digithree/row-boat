package co.simonkenny.row.collection.browse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.simonkenny.row.base.SingleLiveEvent
import co.simonkenny.row.util.UiState
import co.simonkenny.row.core.article.Article
import co.simonkenny.row.core.article.ArticleRepo
import co.simonkenny.row.core.article.RepoFetchOptions
import co.simonkenny.row.core.article.replaceRead
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

    private val _deleteArticlesEvent = SingleLiveEvent<Pair<Any?, Throwable?>>()
    val deleteArticlesEvent: LiveData<Pair<Any?, Throwable?>> = _deleteArticlesEvent

    fun fetchArticles(from: Article? = null, unreadOnly: Boolean = false) {
        _articleList.postValue(UiState.Loading)
        viewModelScope.launch(dispatcher) {
            try {
                _articleList.postValue(UiState.Success(
                    articleRepo.getLocalArticleList(from, unreadOnly).await()
                ))
            } catch (e: Exception) {
                _articleList.postValue(UiState.Error(e))
            }
        }
    }

    fun deleteArticles(urls: List<String>) {
        viewModelScope.launch(dispatcher) {
            try {
                _deleteArticlesEvent.postValue(Pair(
                    articleRepo.deleteLocalArticles(urls).await(),
                    null))
                fetchArticles()
            } catch (e: Exception) {
                _deleteArticlesEvent.postValue(Pair(null, e))
            }
        }
    }

    fun updateArticleReadState(url: String, read: Boolean) {
        viewModelScope.launch(dispatcher) {
            try {
                articleRepo.updateLocalArticle(
                    articleRepo.getArticle(url, RepoFetchOptions(network = false))
                        .await()
                        .replaceRead(read)
                ).await()
                // no need to update state now, may need to when filtering implemented
            } catch (e: Exception) {
                _articleList.postValue(UiState.Error(e))
            }
        }
    }
}