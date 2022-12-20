package ru.smartro.worknote.log

sealed class RestConnectionResource<out T: Any> {
//    data class Success<out T: Any>(val data: T): RestConnectionResource<T>()
    data class SuccessData<out T: Any>(val data: T): RestConnectionResource<T>()
    data class Error(val codeMessage: Pair<Int, String>): RestConnectionResource<Nothing>()
    object Loading: RestConnectionResource<Nothing>()
}