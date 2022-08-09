package ru.smartro.worknote.presentation

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.smartro.worknote.BuildConfig
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.AFragment
import ru.smartro.worknote.andPOintD.BaseViewModel
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.toast
import ru.smartro.worknote.utils.ZipManager
import java.io.*


class DebugF : AFragment(), MediaScannerConnection.OnScanCompletedListener {
    private val vs: DebugViewModel by viewModels()
    override fun onGetLayout(): Int {
        return R.layout.f_debug
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getAct().supportActionBar?.hide()
        initViews(view)
    }

    private fun initViews(view: View) {
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

        val actvAppVersionName = view.findViewById<AppCompatTextView>(R.id.actv__f_debug__app_version_name)
        actvAppVersionName.text = "Версия приложения: $appVersion"

        val actvRamCount = view.findViewById<AppCompatTextView>(R.id.actv_f_debug__ram_count)
        actvRamCount.text = "ОЗУ используется: $usedMemInPercentage%"

        val actvPlatformCount = view.findViewById<AppCompatTextView>(R.id.actv_f_debug__platform_count)
        actvPlatformCount.text = "Кол-во обслуженных платформ: ${platformProgress}/${platformCnt}"
        val pbPlatformProgress = view.findViewById<ProgressBar>(R.id.pb_f_debug__platform_progress)
        pbPlatformProgress.max = platformCnt
        pbPlatformProgress.progress = platformProgress

        val actvContainerCount = view.findViewById<AppCompatTextView>(R.id.actv_f_debug__container_count)
        actvContainerCount.text = "Кол-во обслуженных контейнеров: ${containerProgress}/${containerCnt}"

        val pbContainerProgress = view.findViewById<ProgressBar>(R.id.pb_f_debug__container_progress)
        pbContainerProgress.max = containerCnt
        pbContainerProgress.progress = containerProgress

        val pbRamProgress = view.findViewById<ProgressBar>(R.id.pb_f_debug__ram_progress)
        pbRamProgress.max = 100
        pbRamProgress.progress = usedMemInPercentage.toInt()

        val actvOrganization = view.findViewById<AppCompatTextView>(R.id.actv_f_debug__organisation)
        actvOrganization.text = "Организация: ${paramS().ownerId}"

        val actvUserName = view.findViewById<AppCompatTextView>(R.id.actv_f_debug__username)
        actvUserName.text = "Пользователь: ${paramS().userName}"

        val actvWaybillNumber = view.findViewById<AppCompatTextView>(R.id.actv_f_debug__waybill_number)
        actvWaybillNumber.text = "Путевой лист: ${paramS().wayBillNumber}"

        val actvCoordinate = view.findViewById<AppCompatTextView>(R.id.actv_f_debug__coordinate)
        actvCoordinate.text = "Координаты: ${AppliCation().gps().showForUser()}"

        val actvPhone = view.findViewById<AppCompatTextView>(R.id.actv_f_debug__phone)
        actvPhone.text = "Устройство: ${MyUtil.getDeviceName()}, Android: ${android.os.Build.VERSION.SDK_INT}"

        val acibGotoBack =  view.findViewById<AppCompatImageButton>(R.id.acib_f_debug__gotoback)
        acibGotoBack.setOnClickListener {
            findNavController().popBackStack()
        }
        val acbSendLogs =  view.findViewById<AppCompatButton>(R.id.acb__f_debug__send_logs)
        acbSendLogs.setOnClickListener {
            shareDevInformation()
        }
        val acbOpenLogs =  view.findViewById<AppCompatButton>(R.id.acb__f_debug__open_logs)


        val actvDevMode = view.findViewById<AppCompatTextView>(R.id.actv__f_debug__dev_mode)
        actvDevMode.setOnClickListener{
            if (paramS().isDevModeEnableCounter <= 0) {
                toast("похоже Вы разработчик")
                acbOpenLogs.setOnClickListener {

                }
                return@setOnClickListener
            }
            paramS().isDevModeEnableCounter -= 1
            if (paramS().isDevModeEnableCounter <= 1) {
                toast("остался ${paramS().isDevModeEnableCounter } шаг.(:")
                return@setOnClickListener
            }
            if (paramS().isDevModeEnableCounter <= 3) {
                toast("осталось ${paramS().isDevModeEnableCounter } шага)))")
            }

        }

    }

    private fun getzipFiles(): Array<File> {
        val zipFiles = mutableListOf<File>()
        val saveJSONFiles = AppliCation().getD("saveJSON").listFiles()
        if (saveJSONFiles != null) {
            zipFiles.addAll(saveJSONFiles)
        }

        val realmFiles = AppliCation().getF("files", "FACT.realm")
        zipFiles.add(realmFiles)

        val sharedPrefsFiles = AppliCation().getD("shared_prefs").listFiles()
        if (sharedPrefsFiles != null) {
            zipFiles.addAll(sharedPrefsFiles)
        }

        val logsFiles = AppliCation().getD("logs").listFiles()
        if (logsFiles != null) {
            zipFiles.addAll(logsFiles)
        }

        return zipFiles.toTypedArray()
    }
    private fun shareDevInformation() {


        val downloadD = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val zipFile = File(downloadD,  "${BuildConfig.APPLICATION_ID}.${BuildConfig.VERSION_NAME}")
        //        val zipFile = AppliCation().getF("toSend", "ttest.zip")
        ZipManager.zip(this.getzipFiles(), zipFile)

        //Media type da foto selecionada
        val mediaType = MimeTypeMap.getSingleton()
            .getMimeTypeFromExtension(zipFile.extension)

        MediaScannerConnection.scanFile(
            requireView().context,
            arrayOf(zipFile.absolutePath),
            arrayOf(mediaType), this@DebugF
        )

    }



    override fun onBackPressed() {
        super.onBackPressed()
        findNavController().popBackStack()
    }

    open class DebugViewModel(application: Application) : BaseViewModel(application)

    override fun onScanCompleted(path: String?, uri: Uri?) {

        val mediaType = MimeTypeMap.getSingleton()
            .getMimeTypeFromExtension("zip")

        val intent = Intent()
//

//        val uri = FileProvider.getUriForFile(
//            requireView().context, BuildConfig.APPLICATION_ID + ".provider", zipFile
//        )

            //Setando actions e flags para compartilhamento
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.type = mediaType
        intent.action = Intent.ACTION_SEND
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION


        //Abrindo modal de opções de compartilhamento

        startActivity(Intent.createChooser(intent, "Помогите нам стать лучше"))
    }
}