package ru.smartro.worknote.presentation

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.smartro.worknote.*
import ru.smartro.worknote.abs.AFragment
import ru.smartro.worknote.andPOintD.AViewModel
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.utils.ZipManager
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


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
            AppliCation().stopWorkERS()
            shareDevInformation()
            AppliCation().startWorkER()
        }
        val acbOpenLogs =  view.findViewById<AppCompatButton>(R.id.acb__f_debug__open_logs)


        val actvDevMode = view.findViewById<AppCompatTextView>(R.id.actv__f_debug__dev_mode)
        var lastClickTimeSec = MyUtil.timeStampInSec()

        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            // Handle the returned Uri
            uri?.let {
                val zipFile = File(it.path!!)
                log("registerForActivityResult= ${zipFile.absolutePath}")
//                //Media type da foto selecionada
//                val mediaType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(zipFile.extension)
////
//                MediaScannerConnection.scanFile(
//                    requireView().context,
//                    arrayOf(zipFile.absolutePath),
//                    arrayOf(mediaType), this@DebugF
//                )
                savefile(uri, "r_dos", "r.zip")
                val zipF = AppliCation().getF("r_dos", "r.zip")
                ZipManager.unzip(zipF, AppliCation().getDPath("r_dos"))
                AppliCation().stopWorkERS()

                val realmFileNew = AppliCation().getF("r_dos", "FACT.realm")
                val realmFileOld = AppliCation().getF("files", "FACT.realm")
                realmFileNew.copyTo(realmFileOld, overwrite = true)

                val sharedPrefsFileNew = AppliCation().getF("r_dos", "AppParaMS.xml")
                val sharedPrefsFileOld = AppliCation().getF("shared_prefs", "AppParaMS.xml")
                sharedPrefsFileNew.copyTo(realmFileOld, overwrite = true)

            }
        }

        actvDevMode.setOnClickListener{
            if (paramS().isDevModeEnableCounter <= 0) {
                if (lastClickTimeSec <= MyUtil.timeStampInSec() - 2) {
                    toast("похоже Вы разработчик")
                    lastClickTimeSec = MyUtil.timeStampInSec()
                }
                acbOpenLogs.visibility = View.VISIBLE
                acbOpenLogs.setOnClickListener {
//                    openZipFiles()

//                    val intent = Intent(Intent.ACTION_GET_CONTENT);
//                    intent.setType("*/*");
//                    startActivityForResult(intent, 7);

                    getContent.launch("*/*")
                }
                return@setOnClickListener
            }
            paramS().isDevModeEnableCounter -= 1
            if (paramS().isDevModeEnableCounter <= 1) {
                toast("остался ${paramS().isDevModeEnableCounter } шаг)")
                return@setOnClickListener
            }
            if (paramS().isDevModeEnableCounter <= 3) {
                if (lastClickTimeSec <= MyUtil.timeStampInSec() - 2) {
                    toast("осталось ${paramS().isDevModeEnableCounter } шага)))")
                    lastClickTimeSec = MyUtil.timeStampInSec()
                }

            }

        }



    }

    fun savefile(sourceuri: Uri, dirName: String, fileName: String) {
        val file = AppliCation().getF(dirName, fileName)
        val inputStream: InputStream? = requireContext().contentResolver.openInputStream(sourceuri)
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)

        inputStream?.close()
        outputStream.close()
    }

//
//    fun openFolder() {
//        val file = File(
//            Environment.getExternalStorageDirectory(),
//            "myFolder"
//        )
//        Log.d("path", file.toString())
//        val intent = Intent(Intent.ACTION_GET_CONTENT)
//        intent.setDataAndType(Uri.fromFile(file), "*/*")
//        startActivity(intent)
//    }
    private fun getzipFiles(): Array<File> {
        val zipFiles = mutableListOf<File>()
        val saveJSONFiles = AppliCation().getD("saveJSON").listFiles()
        if (saveJSONFiles != null) {
            zipFiles.addAll(saveJSONFiles)
        }

        val realmFile = AppliCation().getF("files", "FACT.realm")
        zipFiles.add(realmFile)

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
        val zipFile = File(downloadD,  "${BuildConfig.BUILD_TYPE}.${BuildConfig.VERSION_NAME}.zip")
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

    open class DebugViewModel(app: Application) : AViewModel(app)

    override fun onScanCompleted(path: String?, uri: Uri?) {
        log("onScanCompleted.path=${path}")
        log("onScanCompleted.uri=${uri}")
        val mediaType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("zip")

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