package com.hmaserv.rz.domain

sealed class UiState {
    object Loading : UiState()
    data class Success(val dataMap: Map<String, Any>) : UiState()
    data class Error(val message: String) : UiState()
    object NoInternetConnection : UiState()
}