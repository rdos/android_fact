package ru.smartro.worknote.presentation

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import kotlinx.android.synthetic.main.activity_debug.*
import ru.smartro.worknote.abs.AFragment
import ru.smartro.worknote.BuildConfig
import ru.smartro.worknote.R
import ru.smartro.worknote.andPOintD.BaseViewModel
import ru.smartro.worknote.awORKOLDs.util.MyUtil


class DebugF : AFragment() {
    private lateinit var mMapView: MapView
    private val vs: DebugViewModel by viewModels()
    override fun onGetLayout(): Int {
        return R.layout.activity_debug
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getAct().supportActionBar?.hide()
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
        (getAct().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getMemoryInfo(memoryInfo)
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

        mMapView = view!!.findViewById(R.id.debug_mapview)
        mMapView.map.move(
            CameraPosition(AppliCation().gps(), 12.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1F), null
        )
        mMapView.map.isScrollGesturesEnabled = false
        mMapView.map.isZoomGesturesEnabled = false
        mMapView.map.isRotateGesturesEnabled = false

        view?.findViewById<AppCompatImageButton>(R.id.acib__act_journalchat__gotoback)?.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        mMapView.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onDetach() {
        super.onDetach()
        Log.e(TAG, "r_dos/DebugFrag.onDetach.before")
        mMapView.onStop()
        MapKitFactory.getInstance().onStop()
        Log.e(TAG, "r_dos/DebugFrag.onDetach.after")
    }

    override fun onDestroyView() {
        Log.w(TAG, "r_dos/DebugFrag.onDetach.before")
        super.onDestroyView()
        Log.w(TAG, "r_dos/DebugFrag.onDetach.after")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        findNavController().popBackStack()
    }

    open class DebugViewModel(application: Application) : BaseViewModel(application)
}