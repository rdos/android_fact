package ru.smartro.worknote.awORKOLDs.service.network.interceptor

import android.content.Context
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import ru.smartro.worknote.LOG


class TokenAuthenticator(val context: Context) : Authenticator {
    
    override fun authenticate(route: Route?, response: Response): Request? {
        LOG.warn( "authenticate")
        //todo: add senrty exception
//        MyUtil.logout(context)
//        val realm = Realm.getDefaultInstance()
//        realm.executeTransaction {
//            realm.deleteAll()
//        }
        return null
    }
}