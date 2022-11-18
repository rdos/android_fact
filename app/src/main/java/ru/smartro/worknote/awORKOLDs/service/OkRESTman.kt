package ru.smartro.worknote.awORKOLDs.service

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import ru.smartro.worknote.abs.AbsObject
import ru.smartro.worknote.awORKOLDs.AbsRequest
import java.util.PriorityQueue


//class OkRESTman: AbsObject(), Queue<AbsRequest> {
class OkRESTman: AbsObject() {
//    private var mList: LinkedList<AbsRequest>? = null
    private lateinit var mPriorityQueue: PriorityQueue<AbsRequest>
    init {
//        mList = LinkedList()
        val comparator = FistCharComparator()
        mPriorityQueue = PriorityQueue(10, comparator)
    }

    private var client = OkHttpClient()


    fun add(request: AbsRequest) {
        mPriorityQueue.add(request)
    }

    fun send() {
        val request = mPriorityQueue.remove()
        request.onBeforeSend()
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
    inner class FistCharComparator : Comparator<AbsRequest> {

        override fun compare(o1: AbsRequest, o2: AbsRequest): Int {
            val oneString = o1.TAGObject
            val twoString = o2.TAGObject

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