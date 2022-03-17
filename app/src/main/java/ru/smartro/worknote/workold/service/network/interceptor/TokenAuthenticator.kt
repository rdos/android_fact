package ru.smartro.worknote.workold.service.network.interceptor

import android.content.Context
import io.realm.Realm
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import ru.smartro.worknote.workold.util.MyUtil

class TokenAuthenticator(val context: Context) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        MyUtil.logout(context)
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            realm.deleteAll()
        }
        return null
    }
}