package co.simonkenny.row.util

sealed class UiState<out T> {
    object None: UiState<Nothing>()
    object Loading: UiState<Nothing>()
    data class Success<T>(val data: T): UiState<T>()
    class Error(val exception: Throwable): UiState<Nothing>()
}