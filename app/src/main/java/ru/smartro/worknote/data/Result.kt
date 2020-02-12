package ru.smartro.worknote.data

import retrofit2.HttpException
import java.io.IOException

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class Result<out T : Any> {

    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val exception: Throwable, val message: String? = null) : Result<Nothing>() {
        val isAuthError: Boolean
            get() {
                return when (exception) {
                    is HttpException -> {
                        exception.code() == 403 || exception.code() == 401
                    }
                    else -> {
                        false
                    }
                }
            }
        val isIOError: Boolean
            get() {
                return when (exception) {
                    is IOException -> true
                    else -> false
                }
            }
    }

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
        }
    }
}
