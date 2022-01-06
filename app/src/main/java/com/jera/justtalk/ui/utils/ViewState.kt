package com.jera.justtalk.ui.utils

sealed class ViewState {
    object Success : ViewState()
    object Error : ViewState()
    object Loading : ViewState()
    object Initial : ViewState()
}
