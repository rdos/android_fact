package ru.smartro.worknote.ac

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.smartro.worknote.App
import ru.smartro.worknote.LOG

class AndroidNet(val p_cm: ConnectivityManager, val p_callback: ru.smartro.worknote.ac.AndroidNet.CallBack) : ConnectivityManager.NetworkCallback() {
    private val validNetworks: MutableSet<Network> = HashSet()
    private var mOnLostInternetJob: Job? =  null

    override fun onAvailable(network: Network) {
        LOG.debug("before: network=${network}, validNetworks size=${validNetworks.size}")
        val networkCapabilities = p_cm.getNetworkCapabilities(network)
        val isInternet = networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        if(isInternet == true) {
            LOG.info("if(isInternet == true)")
            validNetworks.add(network)
            blockOnLostInternet()
        }
        checkValidNetworks()
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        LOG.debug("before::: validNetworks size=${validNetworks.size}")
        validNetworks.remove(network)
        checkValidNetworks()
    }

    private fun checkValidNetworks() {
        LOG.debug("before::: validNetworks size=${validNetworks.size}")
        if (validNetworks.size <= 0) {
            LOG.trace("if (validNetworks.size <= 0) {")
            runOnLostInternet()
        }
    }

    private fun runOnLostInternet() {
        blockOnLostInternet()
        mOnLostInternetJob = App.getAppliCation().applicationScope.launch(Dispatchers.Main) {
            delay(3000L)
            LOG.info("onLostInternet")
            p_callback.onLostInternet()
            LOG.debug("onLostInternet")
        }
    }

    private fun blockOnLostInternet() {
        if (mOnLostInternetJob != null) {
            mOnLostInternetJob?.cancel()
        }
    }
    interface  CallBack {
        fun onLostInternet()
    }


}
