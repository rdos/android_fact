package ru.smartro.worknote.awORKOLDs.service.network.interceptor

import android.content.Context
import android.util.Log
import io.realm.Realm
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import org.slf4j.LoggerFactory
import ru.smartro.worknote.awORKOLDs.util.MyUtil


class TokenAuthenticator(val context: Context) : Authenticator {
    protected val log = LoggerFactory.getLogger("${this::class.simpleName}")
    override fun authenticate(route: Route?, response: Response): Request? {
        log.warn( "authenticate")
        //todo: add senrty exception
//        MyUtil.logout(context)
//        val realm = Realm.getDefaultInstance()
//        realm.executeTransaction {
//            realm.deleteAll()
//        }
        return null
    }
}