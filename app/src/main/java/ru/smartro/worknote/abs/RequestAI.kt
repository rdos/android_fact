package ru.smartro.worknote.abs

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request

interface RequestAI {
    fun getTAGObject(): String
    fun getOKHTTPRequest(): Request
}