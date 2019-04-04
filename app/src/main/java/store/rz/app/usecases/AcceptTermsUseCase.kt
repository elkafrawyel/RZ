package store.rz.app.usecases

import kotlinx.coroutines.Deferred
import store.rz.app.data.apiService.RetrofitAuthApiService
import store.rz.app.data.logedInUser.ILoggedInUserRepo
import store.rz.app.domain.ApiResponse
import store.rz.app.domain.DataResource
import store.rz.app.utils.Constants

class AcceptTermsUseCase(
    private val loggedInUserRepo: ILoggedInUserRepo,
    private val authApiService: RetrofitAuthApiService
) {

    suspend fun acceptTerms(): Deferred<ApiResponse<Boolean>> {
        return authApiService.acceptTerms(
            "${Constants.AUTHORIZATION_START} ${loggedInUserRepo.getLoggedInUser().token}"
        )
    }
}