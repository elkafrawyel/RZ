package com.hmaserv.rz.domain

sealed class DataResource<out T : Any> {

    data class Success<out T : Any>(val data: T) : DataResource<T>()
    data class Error(val exception: Exception) : DataResource<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
        }
    }
}