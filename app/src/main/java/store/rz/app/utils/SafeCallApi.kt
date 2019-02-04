package store.rz.app.utils

import store.rz.app.domain.DataResource
import java.io.IOException

suspend fun <T : Any> safeApiCall(call: suspend () -> DataResource<T>, errorMessage: String): DataResource<T> {
    return try {
        call()
    } catch (e: Exception) {
        // An exception was thrown when calling the API so we're converting this to an IOException
        e.printStackTrace()
        DataResource.Error(IOException(errorMessage, e))
    }
}