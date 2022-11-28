package ru.smartro.worknote.awORKOLDs.service

import io.sentry.android.okhttp.SentryOkHttpInterceptor
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import ru.smartro.worknote.App
import ru.smartro.worknote.LOG
import ru.smartro.worknote.TIME_OUT
import ru.smartro.worknote.abs.AbsObject
import ru.smartro.worknote.awORKOLDs.AbsRequest
import ru.smartro.worknote.awORKOLDs.AuthRequest
import ru.smartro.worknote.awORKOLDs.RequestAI
import ru.smartro.worknote.todo
import java.io.IOException
import java.util.PriorityQueue
import java.util.concurrent.TimeUnit


//class OkRESTman: AbsObject(), Queue<AbsRequest> {
class OkRESTman: AbsObject(), Callback {
    private var mCOunter: Int = 0
    private var mLastARequest: AbsRequest<*, *>? = null

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

    private var httpLoggingInterceptor = run {
        val httpLoggingInterceptor1 = HttpLoggingInterceptor { message ->
            LOG.warn(message)
        }
        httpLoggingInterceptor1.apply {
            httpLoggingInterceptor1.level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private var mOkHttpClient: OkHttpClient? = null
    private fun getClient(): OkHttpClient {
        if (mOkHttpClient == null) {
            val builder = OkHttpClient().newBuilder()
//            .addInterceptor(authInterceptor)
            if (App.getAppliCation().isDevelMode()) {
                builder.addInterceptor(httpLoggingInterceptor)
            }
            builder.apply {
                addInterceptor(SentryOkHttpInterceptor())
                connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                writeTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
            }
            mOkHttpClient = builder.build()
//            .authenticator(TokenAuthenticator(context))

        }
        return mOkHttpClient!!
    }



    fun add(request: RequestAI) {
        LOG.debug("mPriorityQueue.size=${mPriorityQueue.size}")
        mPriorityQueue.add(request)
        LOG.info("mPriorityQueue.size=${mPriorityQueue.size}")
    }

    fun send() {
        if (mPriorityQueue.size <= 0 ) {
            return
        }
        val request = mPriorityQueue.remove()
        mLastARequest = (request as AbsRequest<*, *>)
        mLastARequest?.onBefore()
        getClient().newCall(request.getOKHTTPRequest()).enqueue(this)
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

    override fun onFailure(call: Call, e: IOException) {
        mLastARequest?.onFailure(call, e)
    }

    override fun onResponse(call: Call, response: Response) {
        if (response.code == 401) {
            LOG.todo("спустя_рукова-production")
            if (mCOunter <= 0) {
                mCOunter++
                val authRequest = AuthRequest()
                App.oKRESTman().add(authRequest)
                App.oKRESTman().add(mLastARequest!!)
                App.oKRESTman().send()
                return
            }
        }
        mLastARequest?.onResponse(call, response)
        this.send()
    }
}