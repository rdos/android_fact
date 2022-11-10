package ru.smartro.worknote.andPOintD

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import ru.smartro.worknote.LOG

class ConnectionLostLiveData(context: Context) : LiveData<Boolean>(), AndroidNet.CallBack {
    private var mNetworkCallback: ConnectivityManager.NetworkCallback? = null
    private val cm = context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager

    override fun onActive() {
        LOG.debug("before")
        mNetworkCallback = AndroidNet(cm, this)

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        if(mNetworkCallback == null) {
            LOG.warn(" if(mNetworkCallback == null) {")
            return
        }
        cm.registerNetworkCallback(networkRequest, mNetworkCallback!!)
    }

    override fun onInactive() {
        LOG.debug("before")
        if(mNetworkCallback != null) {
            LOG.info("if(mNetworkCallback != null)")
            cm.unregisterNetworkCallback(mNetworkCallback!!)
        }
    }

    override fun onLostInternet() {
        LOG.debug("before")
        postValue(true)
    }

}