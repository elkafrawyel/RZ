package com.hmaserv.rz.ui

import androidx.fragment.app.Fragment
import com.hmaserv.rz.domain.UiState

abstract class BaseFragment : Fragment() {
    fun onUiStateChanged(state: UiState?) {
        when(state) {
            UiState.Loading -> showLoading()
            is UiState.Success -> showSuccess(state.dataMap)
            is UiState.Error -> showError(state.message)
            UiState.NoInternetConnection -> showNoInternetConnection()
            null -> {}
        }
    }
    abstract fun showLoading()
    abstract fun showSuccess(dataMap: Map<String, Any>)
    abstract fun showError(message: String)
    abstract fun showNoInternetConnection()
}