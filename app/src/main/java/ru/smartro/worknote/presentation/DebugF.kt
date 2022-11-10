package ru.smartro.worknote.presentation

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.offline_cache.*
import ru.smartro.worknote.*
import ru.smartro.worknote.abs.FragmentA
import ru.smartro.worknote.andPOintD.AViewModel
import ru.smartro.worknote.awORKOLDs.extensions.showDialogAction
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.utils.ZipManager
import ru.smartro.worknote.work.RegionEntity
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class DebugF : FragmentA(), MediaScannerConnection.OnScanCompletedListener, RegionListener, RegionListUpdatesListener {

    private var acbSendLogs: AppCompatButton? = null
    private val vm: DebugViewModel by viewModels()

    private var actvRegionCounter: AppCompatTextView? = null
    private var acibToggleRegionList: AppCompatImageButton? = null
    private var rvRegionList: RecyclerView? = null
    private var adapter: RegionListAdapter? = null
    private var llcRegionListWrapper: LinearLayoutCompat? = null
    private var acbClearMapCache: AppCompatButton? = null

    override fun onGetLayout(): Int {
        return R.layout.f_debug
    }

    fun initMapKitOfflineCacheManager() {
        MapKitFactory.getInstance().offlineCacheManager.allowUseCellularNetwork(true)
        MapKitFactory.getInstance().offlineCacheManager.addRegionListUpdatesListener(this)
        MapKitFactory.getInstance().offlineCacheManager.addRegionListener(this)
    }

    override fun onDetach() {
        super.onDetach()
        MapKitFactory.getInstance().offlineCacheManager.removeRegionListUpdatesListener(this)
        MapKitFactory.getInstance().offlineCacheManager.removeRegionListener(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getAct().supportActionBar?.hide()
        initMapKitOfflineCacheManager()

        initViews(view)

        vm.getRegions()

        vm.regionsList.observe(viewLifecycleOwner) { regionS ->
            if(regionS != null) {
                LOG.debug("Regions ::: ${regionS}")
                val mappedRegionS = mutableListOf<RegionDto>()
                var downloadedCounter = 0
                for(region in vm.regionsList.value!!) {
                    val _state = MapKitFactory.getInstance().offlineCacheManager.getState(region.id)
                    when(_state) {
                        RegionState.COMPLETED, RegionState.NEED_UPDATE, RegionState.OUTDATED -> {
                            LOG.debug("::: Region FOR 1 ${region.id} State: ${_state}")
                            ++downloadedCounter
                        }
                    }
                    mappedRegionS.add(RegionDto(region.id, region.showForUser(), region.size, _state))
                }
                setRegionCount(downloadedCounter)
                adapter?.regionS = mappedRegionS
            }
        }
    }

    private fun initViews(view: View) {
        llcRegionListWrapper = view.findViewById(R.id.llc__f_debug__regions_list_wrapper)
        llcRegionListWrapper?.visibility = View.GONE

        acbClearMapCache = view.findViewById(R.id.acb__f_debug__clear_map_cache)
        acbClearMapCache?.setOnClickListener {
            App.getAppliCation().startVibrateServiceHaptic()
            showDialogAction("Вы уверены, что хотите очистить ВСЕ скачанные регионы?", {
                MapKitFactory.getInstance().offlineCacheManager.clear {
                    toast("Регионы очищены")
                }
            })
        }

        actvRegionCounter = view.findViewById(R.id.actv__f_debug__cached_regions_count)
        setRegionCount()

        rvRegionList = view.findViewById(R.id.rv__f_debug__regions)
        val layoutManager = LinearLayoutManager(requireContext())
        rvRegionList?.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        rvRegionList?.addItemDecoration(dividerItemDecoration)

        acibToggleRegionList = view.findViewById(R.id.acib__f_debug__toggle_regions_list)
        acibToggleRegionList?.setOnClickListener {
            if(llcRegionListWrapper?.visibility == View.VISIBLE)
                llcRegionListWrapper?.visibility = View.GONE
            else
                llcRegionListWrapper?.visibility = View.VISIBLE
        }

        adapter = RegionListAdapter()
        rvRegionList?.adapter = adapter!!

        val workOrders = vm.database.findWorkOrders(false)
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
        actvUserName.text = paramS().userName

        val actvWaybillNumber = view.findViewById<AppCompatTextView>(R.id.actv_f_debug__waybill_number)
        actvWaybillNumber.text = paramS().wayBillNumber

        val actvPhone = view.findViewById<AppCompatTextView>(R.id.actv_f_debug__phone)
        actvPhone.text = "${MyUtil.getDeviceName()}, Android: ${android.os.Build.VERSION.SDK_INT}"

        val acibGotoBack =  view.findViewById<AppCompatImageButton>(R.id.acib_f_debug__gotoback)
        acibGotoBack.setOnClickListener {
            findNavController().popBackStack()
        }
        acbSendLogs =  view.findViewById(R.id.acb__f_debug__send_logs)
        acbSendLogs?.setOnClickListener {
//            SR-5236
//            Добавить на debug-экран возможность отправить "дебаг информацию для разработчиков"
            acbSendLogs?.isEnabled = false
            try {
                AppliCation().stopWorkERS()
                shareDevInformation()
            }
            catch (ex: Exception) {
                LOG.error("acbSendLogs", ex)
            }
            finally {

            }

        }
        val acbOpenLogs =  view.findViewById<AppCompatButton>(R.id.acb__f_debug__open_logs)
        var lastClickTimeSec = MyUtil.timeStampInSec()

        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            // Handle the returned Uri
            uri?.let {
                AppliCation().stopWorkERS()
                val zipFile = File(it.path!!)
                LOG.debug("registerForActivityResult= ${zipFile.absolutePath}")
//                //Media type da foto selecionada
//                val mediaType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(zipFile.extension)
////
//                MediaScannerConnection.scanFile(
//                    requireView().context,
//                    arrayOf(zipFile.absolutePath),
//                    arrayOf(mediaType), this@DebugF
//                )
                savefile(uri, D__R_DOS, "r.zip")
                val zipF = AppliCation().getF(D__R_DOS, "r.zip")
                ZipManager.unzip(zipF, AppliCation().getDPath(D__R_DOS))


                val realmFileNew = AppliCation().getF(D__R_DOS, FN__REALM)
                val realmFileOld = AppliCation().getF(D__FILES, FN__REALM)
                realmFileNew.copyTo(realmFileOld, overwrite = true)

                val sharedPrefsFileNew = AppliCation().getF(D__R_DOS, "AppParaMS.xml")
                val sharedPrefsFileOld = AppliCation().getF("shared_prefs", "AppParaMS.xml")
                sharedPrefsFileNew.copyTo(sharedPrefsFileOld, overwrite = true)
                this@DebugF.requireView().post{
                    AppliCation().restartApp()
                }
            }
        }

        actvAppVersionName.setOnClickListener{
            if (paramS().isDevModeEnableCounter <= 0) {
                if (lastClickTimeSec <= MyUtil.timeStampInSec() - 2) {
                    toast("похоже Вы разработчик")
                    lastClickTimeSec = MyUtil.timeStampInSec()
                }
                acbSendLogs?.visibility = View.VISIBLE
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

    private fun setRegionCount(count: Int = 0) {
        actvRegionCounter?.text = "${resources.getQuantityString(R.plurals.region_downloaded, count)} ${count} ${resources.getQuantityString(R.plurals.region_count, count)}"
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
//            zipFiles.addAll(saveJSONFiles)
        }

        val realmFile = AppliCation().getF(D__FILES, FN__REALM)
//        zipFiles.add(realmFile)

        val sharedPrefsFiles = AppliCation().getD("shared_prefs").listFiles()
        if (sharedPrefsFiles != null) {
            zipFiles.addAll(sharedPrefsFiles)
        }

        val logsFiles = AppliCation().getD(D__LOGS).listFiles()
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

    open class DebugViewModel(app: Application) : AViewModel(app) {

        private val mRegionsList: MutableLiveData<List<RegionEntity>> = MutableLiveData(listOf())
        val regionsList: LiveData<List<RegionEntity>>
            get() = mRegionsList

        fun getRegions() {
            val regions = database.getRegions()
            mRegionsList.postValue(regions)
        }
    }

    override fun onScanCompleted(path: String?, uri: Uri?) {
        LOG.debug("onScanCompleted.path=${path}")
        LOG.debug("onScanCompleted.uri=${uri}")
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
        acbSendLogs?.isEnabled = true
        AppliCation().startWorkER()
    }

    override fun onRegionStateChanged(regionId: Int) {
        val state = MapKitFactory.getInstance().offlineCacheManager.getState(regionId)
        LOG.debug("::: Region changed: ${regionId} State: ${state}")
        adapter?.updateItemState(regionId, state)

        if(vm.regionsList.value != null) {
            var downloadedCounter = 0
            for(region in vm.regionsList.value!!) {
                val _state = MapKitFactory.getInstance().offlineCacheManager.getState(region.id)
                when(_state) {
                    RegionState.COMPLETED, RegionState.NEED_UPDATE, RegionState.OUTDATED -> {
                        LOG.debug("::: Region FOR 2 ${region.id} State: ${_state}")
                        ++downloadedCounter
                    }
                }
            }
            setRegionCount(downloadedCounter)
        }
    }

    override fun onRegionProgress(regionId: Int) {
        val progress = MapKitFactory.getInstance().offlineCacheManager.getProgress(regionId)
        LOG.debug("::: Region ${regionId} progress: ${progress}")
        adapter?.updateItemProgress(regionId, progress)
    }

    inner class RegionListAdapter() :
        RecyclerView.Adapter<RegionListAdapter.RegionHolder>() {

        var regionS: List<RegionDto> = listOf()
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegionHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.f_debug__regions_item, parent, false)
            return RegionHolder(view)
        }

        override fun getItemCount(): Int {
            return regionS.size
        }

        private fun findItemIndex(regionId: Int): Int {
            var index = -1
            regionS.forEachIndexed { _index, region ->
                if(region.id == regionId) {
                    index = _index
                    return@forEachIndexed
                }
            }
            return index
        }

        fun updateItemState(regionId: Int, state: RegionState) {
            var index = findItemIndex(regionId)
            if(index == -1)
                return

            notifyItemChanged(index, state)
        }

        fun updateItemProgress(regionId: Int, progress: Float) {
            var index = findItemIndex(regionId)
            if(index == -1)
                return

            notifyItemChanged(index, progress)
        }

        override fun onBindViewHolder(holder: RegionHolder, position: Int, payloads: MutableList<Any>) {
            val region = regionS[position]
            LOG.debug("::: ${region.name} payload: ${payloads.size}")

            holder.setRegionName(region.name)

            holder.actvDownload.setOnClickListener {
                LOG.debug("Region Start Download Clicked: ${region.id}")
                App.getAppliCation().startVibrateServiceHaptic()
                MapKitFactory.getInstance().offlineCacheManager.startDownload(region.id)
            }

            holder.actvStopDownload.setOnClickListener {
                LOG.debug("Region Stop Download Clicked: ${region.id}")
                App.getAppliCation().startVibrateServiceHaptic()
                MapKitFactory.getInstance().offlineCacheManager.stopDownload(region.id)
            }

            holder.actvDropRegion.setOnClickListener {
                LOG.debug("Region Drop Clicked: ${region.id}")
                App.getAppliCation().startVibrateServiceHaptic()
                showDialogAction("Вы уверены, что хотите очистить скачанный регион?", {
                    MapKitFactory.getInstance().offlineCacheManager.drop(region.id)
                })
            }

            if(payloads.size > 0) {
                when(payloads[0]) {
                    is RegionState -> {
                        LOG.debug("Payload is State: ${payloads[0]}")
                        holder.setState(payloads[0] as RegionState)
                    }
                    is Float -> {
                        LOG.debug("Payload is Progress: ${payloads[0]}")
                        holder.setProgress(payloads[0] as Float)
                    }
                    else -> {
                        LOG.debug("ELSE !!!!!!!")
                    }
                }
            } else {
                LOG.debug("Payloads!!! size 0")
                LOG.debug("REGION STATE::: ${region.state}")
                holder.setState(region.state!!)
            }
        }

        inner class RegionHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            val actvRegionName: AppCompatTextView = itemView.findViewById(R.id.actv__f_debug__regions_item__name)
            val actvDownload: AppCompatImageButton = itemView.findViewById(R.id.acib__f_debug__regions_item__download_button)
            val actvDownloadProgress: AppCompatTextView = itemView.findViewById(R.id.actv__f_debug__regions_item__download_progress)
            val acivRegionCompleted: AppCompatImageView = itemView.findViewById(R.id.aciv__f_debug__regions_item__completed)
            val actvStopDownload: AppCompatImageButton = itemView.findViewById(R.id.acib__f_debug__regions_item__stop_download)
            val actvDropRegion: AppCompatImageButton = itemView.findViewById(R.id.acib__f_debug__regions_item__drop_region)

            fun setRegionName(name: String) {
                actvRegionName.text = name
            }

            fun setState(state: RegionState = RegionState.AVAILABLE) {
                LOG.debug("${state}")
                actvDownload.visibility = if(state == RegionState.AVAILABLE) View.VISIBLE else View.GONE
                actvDownloadProgress.visibility = if(state == RegionState.DOWNLOADING) View.VISIBLE else View.GONE
                actvStopDownload.visibility = if(state == RegionState.DOWNLOADING) View.VISIBLE else View.GONE
                acivRegionCompleted.visibility = if(state == RegionState.COMPLETED) View.VISIBLE else View.GONE
                actvDropRegion.visibility = if(state == RegionState.COMPLETED) View.VISIBLE else View.GONE
            }

            fun setProgress(rawProgress: Float) {
                val progress = (rawProgress * 100).toInt()
                actvDownloadProgress.text = "${progress} %"
            }
        }

        override fun onBindViewHolder(holder: RegionHolder, position: Int) {
//            TODO("Not yet implemented")
        }
    }

    override fun onListUpdated() {
        val regions = MapKitFactory.getInstance().offlineCacheManager.regions()
        LOG.debug("::: size: ${regions.size}, ids: [${regions.joinToString { it.id.toString() }}]")
    }
}

data class RegionDto(
    var id: Int = Inull,
    var name: String = Snull,
    var size: String = Snull,
    var state: RegionState? = null
)