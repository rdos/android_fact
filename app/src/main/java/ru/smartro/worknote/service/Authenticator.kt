package ru.smartro.worknote.service

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class Authenticator : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        return if (isRequestWithAccessToken(response))
            null
        else
            newRequestWithAccessToken(response.request, AppPreferences.accessToken!!)
    }

    private fun isRequestWithAccessToken(response: Response): Boolean {
        val header = response.request.header("Authorization")
        return header != null
    }

    private fun newRequestWithAccessToken(request: Request, accessToken: String): Request {
        return request.newBuilder().header("Authorization", "Bearer $accessToken").build()
    }
}