package ru.smartro.worknote.work.ui

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import kotlinx.android.synthetic.main.activity_debug.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.BuildConfig
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.ActNOAbst
import ru.smartro.worknote.awORKOLDs.base.BaseViewModel
import ru.smartro.worknote.awORKOLDs.util.MyUtil


class DebugAct : ActNOAbst() {
    private lateinit var mMapView: MapView
    private val vs: DebugViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)
        supportActionBar!!.title = "Дебаг экран"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initViews()
    }

    private fun initViews() {
        val workOrders = vs.baseDat.findWorkOrders(false)
//        val containerProgress = workOrders.
//        val platformProgress = vs.findPlatformProgress()
        var platformCnt = 0
        var platformProgress = 0
        var containerCnt = 0
        var containerProgress = 0
        for(workOrder in workOrders) {
            platformCnt += workOrder.cnt_platform
            platformProgress += workOrder.cntPlatformProgress()
            containerCnt += workOrder.cnt_container
            containerProgress += workOrder.cntContainerProgress()
        }
        val memoryInfo = ActivityManager.MemoryInfo()
        (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getMemoryInfo(memoryInfo)
        val nativeHeapSize = memoryInfo.totalMem
        val nativeHeapFreeSize = memoryInfo.availMem
        val usedMemInBytes = nativeHeapSize - nativeHeapFreeSize
        val usedMemInPercentage = usedMemInBytes * 100 / nativeHeapSize

        val appVersion = BuildConfig.VERSION_NAME

        debug_app.text = "Версия приложения: $appVersion"
        debug_ram_count.text = "ОЗУ используется: $usedMemInPercentage%"

        debug_platform_count.text = "Кол-во обслуженных платформ: ${platformProgress}/${platformCnt}"
        debug_platform_progress.max = platformCnt
        debug_platform_progress.progress = platformProgress
        debug_container_count.text = "Кол-во обслуженных контейнеров: ${containerProgress}/${containerCnt}"
        debug_container_progress.max = containerCnt
        debug_container_progress.progress = containerProgress

        debug_ram_progress.max = 100
        debug_ram_progress.progress = usedMemInPercentage.toInt()

        debug_organisation.text = "Организация: ${paramS().ownerId}"
        debug_user.text = "Пользователь: ${paramS().userName}"
        debug_waybill.text = "Путевой лист: ${paramS().wayBillNumber}"
        debug_coordinate.text = "Координаты: ${AppliCation().gps().showForUser()}"
        debug_phone.text = "Устройство: ${MyUtil.getDeviceName()}, Android: ${android.os.Build.VERSION.SDK_INT}"

        mMapView = findViewById(R.id.debug_mapview)
        mMapView.map.move(
            CameraPosition(AppliCation().gps(), 12.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1F), null
        )
        mMapView.map.isScrollGesturesEnabled = false
        mMapView.map.isZoomGesturesEnabled = false
        mMapView.map.isRotateGesturesEnabled = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        mMapView.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        super.onStop()
        Log.e(TAG, "r_dos/DebugAct.onStop.before")
        mMapView.onStop()
        MapKitFactory.getInstance().onStop()
        Log.e(TAG, "r_dos/DebugAct.onStop.after")
    }

    override fun onPause() {
        Log.w(TAG, "r_dos/DebugAct.onPause.before")
        super.onPause()
        Log.w(TAG, "r_dos/DebugAct.onPause.after")
    }

    open class DebugViewModel(application: Application) : BaseViewModel(application)
}