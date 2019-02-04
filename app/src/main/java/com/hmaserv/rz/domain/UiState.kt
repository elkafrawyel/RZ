package com.hmaserv.rz.domain

import android.view.View

sealed class UiState {
    object Loading : UiState()
    data class Success(val dataMap: Map<String, Any>) : UiState()
    data class Error(val message: String) : UiState()
    object NoInternetConnection : UiState()
}

sealed class State {
    data class HomeState(
        val loadingVisibility: Int = View.GONE,
        val errorVisibility: Int = View.GONE,
        val errorProgress: Float = 0F,
        val errorPlayAnimation: Boolean = false,
        val emptyVisibility: Int = View.GONE,
        val noConnectionVisibility: Int = View.GONE,
        val dataVisibility: Int = View.GONE,
        val bannersVisibility: Int = View.GONE,
        val banners: List<String> = emptyList(),
        val promotions: List<MiniAd> = emptyList()
    ) : State()

    data class CategoriesState(
        val loadingVisibility: Int = View.GONE,
        val isRefreshing: Boolean = false,
        val errorVisibility: Int = View.GONE,
        val errorProgress: Float = 0F,
        val errorPlayAnimation: Boolean = false,
        val emptyVisibility: Int = View.GONE,
        val noConnectionVisibility: Int = View.GONE,
        val dataVisibility: Int = View.GONE,
        val categories: List<Category> = emptyList()
    ) : State()

    data class SubCategoriesState(
        val loadingVisibility: Int = View.GONE,
        val isRefreshing: Boolean = false,
        val errorVisibility: Int = View.GONE,
        val errorProgress: Float = 0F,
        val errorPlayAnimation: Boolean = false,
        val emptyVisibility: Int = View.GONE,
        val noConnectionVisibility: Int = View.GONE,
        val dataVisibility: Int = View.GONE,
        val subCategories: List<SubCategory> = emptyList()
    ) : State()

    data class AdsState(
        val loadingVisibility: Int = View.GONE,
        val isRefreshing: Boolean = false,
        val errorVisibility: Int = View.GONE,
        val errorProgress: Float = 0F,
        val errorPlayAnimation: Boolean = false,
        val emptyVisibility: Int = View.GONE,
        val noConnectionVisibility: Int = View.GONE,
        val dataVisibility: Int = View.GONE,
        val ads: List<MiniAd> = emptyList()
    ) : State()

    data class AdState(
        val loadingVisibility: Int = View.GONE,
        val isRefreshing: Boolean = false,
        val errorVisibility: Int = View.GONE,
        val errorProgress: Float = 0F,
        val errorPlayAnimation: Boolean = false,
        val emptyVisibility: Int = View.GONE,
        val noConnectionVisibility: Int = View.GONE,
        val dataVisibility: Int = View.GONE,
        val updateAttribute: Boolean = false,
        val ad: Ad? = null,
        val totalPrice: Int = 0,
        val attributes: List<Attribute.MainAttribute> = emptyList()
    ) : State()

    data class MyAdsState(
        val loadingVisibility: Int = View.GONE,
        val isRefreshing: Boolean = false,
        val errorVisibility: Int = View.GONE,
        val errorProgress: Float = 0F,
        val errorPlayAnimation: Boolean = false,
        val emptyVisibility: Int = View.GONE,
        val noConnectionVisibility: Int = View.GONE,
        val dataVisibility: Int = View.GONE,
        val myAds: List<MiniAd> = emptyList()
    ) : State()

    data class CreateAdState(
        val loadingVisibility: Int = View.GONE,
        val isRefreshing: Boolean = false,
        val errorVisibility: Int = View.GONE,
        val errorProgress: Float = 0F,
        val errorPlayAnimation: Boolean = false,
        val emptyVisibility: Int = View.GONE,
        val noConnectionVisibility: Int = View.GONE,
        val dataVisibility: Int = View.GONE,
        val categories: List<Category> = emptyList(),
        val subCategoriesMap: Map<String, List<SubCategory>> = emptyMap(),
        val attributesMap: Map<String, List<AttributeSection>> = emptyMap()
    ) : State()

    object EditAdState : State()
}

sealed class Action {
    object Started : Action()
    object Refresh : Action()
    object UpgradeRequest : Action()
    data class DeleteAd(val position: Int) : Action()
    data class SelectAttribute(val mainAttributePosition: Int, val subAttributePosition: Int) : Action()
}