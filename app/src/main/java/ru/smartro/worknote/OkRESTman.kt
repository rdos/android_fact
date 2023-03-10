package ru.smartro.worknote

import io.sentry.android.okhttp.SentryOkHttpInterceptor
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import ru.smartro.worknote.abs.AbsObject
import ru.smartro.worknote.presentation.ac.AbsRequest
import ru.smartro.worknote.presentation.RPOSTAuth
import ru.smartro.worknote.abs.RequestAI
import java.io.IOException
import java.util.PriorityQueue
import java.util.concurrent.TimeUnit


//class OkRESTman: AbsObject(), Queue<AbsRequest> {
class OkRESTman: AbsObject(), Callback {
    private var mRequestInProgress: Boolean = false
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



    fun put(request: RequestAI) {
        LOG.debug("mPriorityQueue.size=${mPriorityQueue.size}")
        mPriorityQueue.add(request)
        if(mRequestInProgress == true) {
            return
        }

        this.send()
        LOG.info("mPriorityQueue.size=${mPriorityQueue.size}")
    }

    private fun send() {
        LOG.trace("before")
        if (mPriorityQueue.size <= 0 ) {
            LOG.trace("if (mPriorityQueue.size <= 0 ) {")
            mRequestInProgress = false
            return
        }

        mRequestInProgress = true

        val request = mPriorityQueue.remove()
        mLastARequest = (request as AbsRequest<*, *>)
        mLastARequest!!.onBefore()
        LOG.trace("newCall ${mLastARequest!!::class.java}")

        getClient().newCall(mLastARequest!!.getOKHTTPRequest()).enqueue(this)
        // TODO:  : r_dos!!!//        request.onAfterSend()
        LOG.trace("after")
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
        LOG.debug("before")
        val messageShowForUser = "????????????, ?????????????????????? ????????????????..."
        mLastARequest?.onFailure(call, e, messageShowForUser)

        this.send()
        LOG.debug("after")
    }

    override fun onResponse(call: Call, response: Response) {
        LOG.debug("onResponse")
        val isAuthRequest = mLastARequest is RPOSTAuth
        if(
            response.code == 401 &&
            !isAuthRequest
        ) {
            LOG.todo("????????????_????????????-production")

            if (mCOunter <= 0) {
                mCOunter++
                val rauth = RPOSTAuth()
                App.oKRESTman().put(rauth)
                App.oKRESTman().put(mLastARequest!!)
                
                return
            }
        }
        mLastARequest?.onResponse(call, response)

        this.send()
    }
}