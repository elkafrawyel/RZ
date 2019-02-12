package store.rz.app.domain

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


    data class MyOrdersState(
        val loadingVisibility: Int = View.GONE,
        val isRefreshing: Boolean = false,
        val errorVisibility: Int = View.GONE,
        val errorProgress: Float = 0F,
        val errorPlayAnimation: Boolean = false,
        val emptyVisibility: Int = View.GONE,
        val noConnectionVisibility: Int = View.GONE,
        val dataVisibility: Int = View.GONE,
        val payNowBtnVisibility: Int = View.GONE,
        val selectedPayment: Payment = Payment.CASH,
        val myCashOrders: List<MiniOrder> = emptyList(),
        val myPaypalOrders: List<MiniOrder> = emptyList()
    ) : State()

    data class OrderState(
        val loadingVisibility: Int = View.GONE,
        val isRefreshing: Boolean = false,
        val errorVisibility: Int = View.GONE,
        val errorProgress: Float = 0F,
        val errorPlayAnimation: Boolean = false,
        val emptyVisibility: Int = View.GONE,
        val noConnectionVisibility: Int = View.GONE,
        val dataVisibility: Int = View.GONE,
        val order: List<Order> = emptyList()
    ) : State()

    data class ReviewsState(
        val loadingVisibility: Int = View.GONE,
        val isRefreshing: Boolean = false,
        val errorVisibility: Int = View.GONE,
        val errorProgress: Float = 0F,
        val errorPlayAnimation: Boolean = false,
        val emptyVisibility: Int = View.GONE,
        val noConnectionVisibility: Int = View.GONE,
        val dataVisibility: Int = View.GONE,
        val reviews: List<Review> = emptyList()
    ) : State()

    data class WriteReviewState(
        val onViewLoadingVisibility: Int = View.GONE,
        val goBack: Boolean = false
    ) : State()

    data class SearchState(
        val loadingVisibility: Int = View.GONE,
        val errorVisibility: Int = View.GONE,
        val errorProgress: Float = 0F,
        val errorPlayAnimation: Boolean = false,
        val subCategoriesLoading: Int = View.GONE,
        val dataVisibility: Int = View.GONE,
        val selectedCategory: Int = 0,
        val selectedSubCategory: Int = 0,
        val categories: List<Category> = emptyList(),
        val subCategories: List<SubCategory> = emptyList()
    ) : State()

    object EditAdState : State()
    object CreateOrderState : State()
    object ForgetPassword : State()
    object Login : State()
    object Register : State()
    object Verification : State()
    object Main : State()

}

sealed class Action {
    object Started : Action()
    object Refresh : Action()
    object UpgradeRequest : Action()
    data class DeleteAd(val position: Int) : Action()
    data class SelectAttribute(val mainAttributePosition: Int, val subAttributePosition: Int) : Action()
    data class PaymentTabSelected(val payment: Payment) : Action()
    object AcceptOrder : Action()
    data class RefuseOrder(val note: String) : Action()
    data class PaymentReceived(val amount: String) : Action()
    object CompleteOrder : Action()
    data class WriteReview(val rate: Int, val review: String) : Action()
    data class CategorySelected(val position: Int) : Action()
    data class SubCategorySelected(val position: Int) : Action()
}