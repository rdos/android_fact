package ru.smartro.worknote.awORKOLDs.service

import io.sentry.android.okhttp.SentryOkHttpInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import ru.smartro.worknote.App
import ru.smartro.worknote.TIME_OUT
import ru.smartro.worknote.abs.AbsObject
import ru.smartro.worknote.awORKOLDs.AbsRequest
import ru.smartro.worknote.awORKOLDs.RequestAI
import java.util.PriorityQueue
import java.util.concurrent.TimeUnit


//class OkRESTman: AbsObject(), Queue<AbsRequest> {
class OkRESTman: AbsObject() {
//    private var mList: LinkedList<AbsRequest>? = null
    private lateinit var mPriorityQueue: PriorityQueue<RequestAI>
    init {
//        mList = LinkedList()
        val comparator = FistCharComparator()
        mPriorityQueue = PriorityQueue(10, comparator)
    }



    private val authInterceptor = Interceptor { chain ->
        val newUrl = chain.request().url
            .newBuilder()
            .build()

        val newRequest = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer " + App.getAppParaMS().token)
            .url(newUrl)
            .build()
        chain.proceed(newRequest)
    }
    private val client =
        OkHttpClient().newBuilder()
//            .addInterceptor(authInterceptor)
//            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(SentryOkHttpInterceptor())
//            .authenticator(TokenAuthenticator(context))
            .connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
            .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
            .writeTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
            .build()


    fun add(request: RequestAI) {
        mPriorityQueue.add(request)
    }

    fun send() {
        val request = mPriorityQueue.remove()
        val aRequest = (request as AbsRequest<*, *>)
        aRequest.onBefore()
        client.newCall(request.getOKHTTPRequest()).enqueue(request)
        // TODO:  : r_dos!!!//        request.onAfterSend()
    }

    /**
    data class Resource<out T>(val status: Status, val data: T?, val msg: String?) {
        companion object {
            fun <T> success(data: T?, msg: String = ""): Resource<T> {
                return Resource(Status.SUCCESS, data, msg)
            }
            fun <T> error(msg: String = "", data: T? = null): Resource<T> {
                return Resource(Status.ERROR, data, msg)
            }
            fun <T> network(msg: String = "", data: T? = null): Resource<T> {
                return Resource(Status.NETWORK, data, msg)
            }
        }
    }
*/
    inner class FistCharComparator : Comparator<RequestAI> {

        override fun compare(o1: RequestAI, o2: RequestAI): Int {
            val oneString = o1.getTAGObject()
            val twoString = o2.getTAGObject()

            val firstSymbolX = oneString[0]
            val firstSymbolY = twoString[0]
            if (firstSymbolX > firstSymbolY) {
                return 1
            }
            if (firstSymbolX < firstSymbolY) {
                return -1
            }
            return 0
        }
    }
}