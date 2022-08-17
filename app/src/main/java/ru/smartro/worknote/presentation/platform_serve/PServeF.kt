package ru.smartro.worknote.presentation.platform_serve

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import ru.smartro.worknote.*
import ru.smartro.worknote.abs.AFragment
import ru.smartro.worknote.awORKOLDs.extensions.hideDialog
import ru.smartro.worknote.awORKOLDs.extensions.showDialogFillKgoVolume
import ru.smartro.worknote.awORKOLDs.extensions.showDlgPickup
import ru.smartro.worknote.awORKOLDs.util.MyUtil.toStr
import ru.smartro.worknote.work.ConfigName
import ru.smartro.worknote.work.ContainerEntity


class PServeF :
    AFragment(){

    private var mBackPressedCnt: Int = 2

    private val THUMB_INACTIVE = "Inactive"
    private val THUMB_ACTIVE = "Active"
    private var mVolumePickup: Double? = null
    private var tvVolumePickup: TextView? = null
    private var acbKGORemaining: AppCompatButton? = null
    private var mAcbKGOServed: AppCompatButton? = null
    private var acbProblem: AppCompatButton? = null
    private var rvContainers: RecyclerView? = null
    private var acsbVolumePickup: SeekBar? = null

    private var btnCompleteTask: AppCompatButton? = null
    private var tvPlatformSrpId: TextView? = null
    private var actvAddress: AppCompatTextView? = null
    private var scScreenMode: SwitchCompat? = null
    private var actvScreenLabel: AppCompatTextView? = null

    private var plId: Int? = null
    private val vm: ServePlatformVM by activityViewModels()

    override fun onGetLayout(): Int {
        return R.layout.f_pserve
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        plId = getArgumentID()
        if (savedInstanceState == null) {
            log("savedInstanceState == null")
        } else {
            log("savedInstanceState HE null")
        }

        tvPlatformSrpId = view.findViewById(R.id.tv_f_pserve__sprid)

        btnCompleteTask = view.findViewById(R.id.acb_activity_platform_serve__complete)
        actvAddress = view.findViewById(R.id.tv_platform_serve__address)
        scScreenMode = view.findViewById(R.id.sc_screen_mode)
        actvScreenLabel = view.findViewById(R.id.screen_mode_label)

        tvVolumePickup = view.findViewById(R.id.et_act_platformserve__volumepickup)
        rvContainers = view.findViewById<RecyclerView?>(R.id.rv_f_pserve__containers).apply {
            recycledViewPool.setMaxRecycledViews(0, 0)
        }
        acbProblem = view.findViewById(R.id.acb_activity_platform_serve__problem)
        mAcbKGOServed = view.findViewById(R.id.acb_activity_platform_serve__kgo_served)
        acbKGORemaining = view.findViewById(R.id.apb_activity_platform_serve__kgo_remaining)
        acsbVolumePickup = view.findViewById(R.id.acsb_activity_platform_serve__seekbar)

        actvScreenLabel?.text = "Списком"

        val platformEntity = vm.getPlatformEntity()
        /////////////////////////////////////////////
        if(platformEntity.serveModeCodeName == App.ServeMode.PServeF)
            scScreenMode?.visibility = View.GONE
        else
            initSwitch()
        ////////////////////////////////////////////
        tvPlatformSrpId?.text =
            "№${platformEntity.srpId} / ${platformEntity.containers.size} конт."

        btnCompleteTask?.setOnClickListener {
            navigateMain(R.id.PhotoAfterMediaF, platformEntity.platformId)
        }

        actvAddress?.text = "${platformEntity.address}"
        if (platformEntity.containers.size >= 7 ) {
            actvAddress?.apply {
                setOnClickListener { view ->
                    maxLines = if (maxLines < 3) {
                        3
                    } else {
                        1
                    }
                }
            }
        } else {
            actvAddress?.maxLines = 3
        }

        val containers = vm.getContainerS()
        LoG.debug("CONTAINERS::: ${containers.joinToString { it.volume.toString() }}")
        val containersAdapter = PServeContainersAdapter(containers)
        rvContainers?.adapter = containersAdapter

        if (platformEntity.failureMedia.size > 0) {
            acbProblem?.let { setUseButtonStyleBackgroundRed(it) }
        }
        acbProblem?.setOnClickListener {
            navigateMain(R.id.PhotoFailureMediaF, platformEntity.platformId)
        }
        if (platformEntity.isServedKGONotEmpty()) {
            mAcbKGOServed?.let { setUseButtonStyleBackgroundGreen(it) }
        }
        mAcbKGOServed?.setOnClickListener {
            showDialogFillKgoVolume().let { view ->
                val tietKGOVolumeIn = view.findViewById<TextInputEditText>(R.id.kgo_volume_in)
                tietKGOVolumeIn.setText(platformEntity.getServedKGOVolume())
                val btnSave = view.findViewById<Button>(R.id.btn_alert_kgo__save)
                btnSave.setOnClickListener {
                    val servedKGOVolumeText = tietKGOVolumeIn.text.toString()
                    if (servedKGOVolumeText.isNullOrBlank()) {
                        return@setOnClickListener
                    }
                    navigateMain(R.id.PhotoKgoServedF, platformEntity.platformId, servedKGOVolumeText)
                    hideDialog()
                }
            }
        }
        if (platformEntity.isRemainingKGONotEmpty()) {
            acbKGORemaining?.let { setUseButtonStyleBackgroundGreen(it) }
        }

        acbKGORemaining?.setOnClickListener {
            showDialogFillKgoVolume().let { view ->
                val tietKGOVolumeIn = view.findViewById<TextInputEditText>(R.id.kgo_volume_in)
                tietKGOVolumeIn.setText(platformEntity.getRemainingKGOVolume())
                val btnSave = view.findViewById<Button>(R.id.btn_alert_kgo__save)
                btnSave.setOnClickListener{
                    val remainingKGOVolumeText = tietKGOVolumeIn.text.toString()
                    if (remainingKGOVolumeText.isNullOrBlank()) {
                        return@setOnClickListener
                    }
                    navigateMain(R.id.PhotoKgoRemainingF, platformEntity.platformId, remainingKGOVolumeText)
                    hideDialog()
                }
            }
        }
        if (platformEntity.isPickupNotEmpty()) {
            acsbVolumePickup?.progress = platformEntity.volumePickup!!.toInt()
            tvVolumePickuptext(platformEntity.volumePickup)
            acsbVolumePickup?.thumb = getThumb(R.drawable.bg_button_green__usebutton)
        } else {
            acsbVolumePickup?.thumb = getThumb(null)
        }
        acsbVolumePickup?.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            private var mProgressAtStartTracking = 0
            private val SENSITIVITY = 1
            override fun onProgressChanged(s: SeekBar?, progress: Int, fromUser: Boolean) {
                tvVolumePickuptext(progress)
                if(progress > 0 && tag != THUMB_ACTIVE){
                    acsbVolumePickup?.thumb = getThumb(R.drawable.bg_button_green__usebutton)
                    acsbVolumePickup?.tag = THUMB_ACTIVE
                } else if(progress <= 0 && tag != THUMB_INACTIVE) {
                    acsbVolumePickup?.thumb = getThumb(null)
                    acsbVolumePickup?.tag = THUMB_INACTIVE
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                mProgressAtStartTracking = seekBar!!.progress
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if(Math.abs(mProgressAtStartTracking - seekBar!!.progress) <= SENSITIVITY){
                    acsbVolumePickup?.apply {
                        onClickPickup(this)
                    }
                    return
                }

                acsbVolumePickup?.apply {
                    if (progress > 0 ) {
                        gotoMakePhotoForPickup(progress.toDouble())
                    } else {
                        vm.updateVolumePickup(platformEntity.platformId, null)
                    }
                }

            }
        })
    }

    private fun initSwitch() {
        // DISABLING SWIPE MOTION ON SWITCH
        scScreenMode?.setOnTouchListener { v, event -> event.actionMasked == MotionEvent.ACTION_MOVE }
        scScreenMode?.setOnClickListener {
            val configEntity = vm.database.loadConfig(ConfigName.USER_WORK_SERVE_MODE)
            configEntity.value = App.ServeMode.PServeGroupByContainersF
            vm.database.saveConfig(configEntity)

            navigateMain(R.id.PServeByTypesF, plId!!)
        }
    }


    private fun onClickPickup(acsbVolumePickup: SeekBar) {
        acsbVolumePickup.isEnabled = false
        try {
            showDlgPickup().let{ dialogView ->
                val tietAdditionalVolumeInM3 = dialogView.findViewById<TextInputEditText>(R.id.tiet_alert_additional_volume_container)
                vm.getPlatformEntity().volumePickup?.let{
                    tietAdditionalVolumeInM3.setText(vm.getPlatformEntity().volumePickup.toString())
                }

                val btnOk = dialogView.findViewById<Button>(R.id.btn_alert_additional_volume_container__ok)
                btnOk.setOnClickListener {
                    hideDialog()
                    val volume = tietAdditionalVolumeInM3.text.toString().toDoubleOrNull()
                    if (volume == null) {
                        acsbVolumePickup.progress = 0
                    } else {
                        gotoMakePhotoForPickup(volume)
                    }
                }
            }
        } finally {
            acsbVolumePickup.isEnabled = true
        }
    }

    private fun gotoMakePhotoForPickup(newVolume: Double) {
        navigateMain(R.id.PhotoPickupMediaF, vm.getPlatformId(), newVolume.toString())
    }

    private fun tvVolumePickuptext(progressDouble: Double?) {
        val progressText = if (progressDouble != null)
            String.format("%.1f", progressDouble)
        else
            String.format("%.1f", 0.0)

        tvVolumePickup?.text = "$progressText м³"
        mVolumePickup = progressDouble
    }

    private fun tvVolumePickuptext(progress: Int) {
        if (progress < 0) {
            tvVolumePickuptext(0.0)
            return
        }
        tvVolumePickuptext(progress.toDouble())
    }

    private fun getThumb(background: Int? = null): Drawable {
        val thumbView: View = LayoutInflater.from(getAct())
            .inflate(R.layout.act_platformserve__pickup_seekbarthumb, null, false)
        if(background != null)
            thumbView
                .findViewById<AppCompatButton>(R.id.acb_act_platformserve__pickup_seekbarthumb)
                .setBackgroundDrawable(ContextCompat.getDrawable(getAct(), R.drawable.bg_button_green__usebutton))
        thumbView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap = Bitmap.createBitmap(thumbView.measuredWidth, thumbView.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        thumbView.layout(0, 0, thumbView.measuredWidth, thumbView.measuredHeight)
        thumbView.draw(canvas)
        return BitmapDrawable(resources, bitmap)
    }

    private fun setUseButtonStyleBackgroundGreen(appCompatButton: AppCompatButton) {
        appCompatButton.setBackgroundDrawable(ContextCompat.getDrawable(getAct(), R.drawable.bg_button_green__usebutton))
    }

    private fun setUseButtonStyleBackgroundRed(appCompatButton: AppCompatButton) {
        appCompatButton.setBackgroundDrawable(ContextCompat.getDrawable(getAct(), R.drawable.bg_button_red__usebutton))
    }


    override fun onBackPressed() {
        mBackPressedCnt--
        if (mBackPressedCnt <= 0) {
            vm.updatePlatformStatusUnfinished()
            navigateBack(R.id.MapF)
            toast("Вы не завершили обслуживание КП.")
        } else {
            toast("Вы не завершили обслуживание КП. Нажмите ещё раз, чтобы выйти")
        }
    }


    /********************************************************************************************************************
     ********************************************************************************************************************
     ********************************************************************************************************************
     * **************************************VIEW MODEL
     */
//
   inner class PServeContainersAdapter(
        private val containers: List<ContainerEntity>,
    ) : RecyclerView.Adapter<PServeContainersAdapter.OwnerViewHolder>() {
        // TODO: 22.10.2021  item_container_adapter !!!
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnerViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_container_adapter, parent, false)
            return OwnerViewHolder(view)
        }


        override fun getItemCount(): Int {
            LoG.debug("${containers.size}")
            return containers.size
        }

        override fun onBindViewHolder(holder: OwnerViewHolder, position: Int) {
            val container = containers[position]

            holder.itemView.findViewById<TextView>(R.id.choose_title).text = container.number
            holder.itemView.findViewById<TextView>(R.id.tv_item_container_adapter__type_name).text = container.typeName
            // TODO: 25.10.2021 add getString() + format
            holder.itemView.findViewById<TextView>(R.id.tv_item_container_adapter__constructiveVolume).text = container.constructiveVolume.toStr("м³")
            holder.itemView.setOnClickListener {
                LoG.debug("CONTAINER::: id: ${container.containerId}, isActive: ${container.isActiveToday}, volume: ${container.volume}")
                if(container.isActiveToday || container.volume != null) {
                    navigateMain(R.id.ContainerServeBottomDialog, container.containerId, vm.getPlatformId().toString())
//                    return@setOnClickListener // не работал
                } else {
                    showTakeInactiveContainerAlert(getAct()) {
                        navigateMain(R.id.ContainerServeBottomDialog, container.containerId, vm.getPlatformId().toString())
                    }
                }
                LoG.info("onBindViewHolder: true")
            }
            val tvVolume = holder.itemView.findViewById<TextView>(R.id.tv_item_container_adapter__volume)
            tvVolume.text =  "${container.getVolumeInPercent()}%"
            //2&
            tvVolume.setTextColor(container.getVolumePercentColor(holder.itemView.context))

            if(!container.isActiveToday && container.volume == null) {
                holder.itemView.findViewById<TextView>(R.id.choose_title).setTextColor(ContextCompat.getColor(getAct(), R.color.light_gray))
                holder.itemView.findViewById<TextView>(R.id.tv_item_container_adapter__type_name).setTextColor(ContextCompat.getColor(getAct(), R.color.light_gray))
                holder.itemView.findViewById<TextView>(R.id.tv_item_container_adapter__constructiveVolume).setTextColor(ContextCompat.getColor(getAct(), R.color.light_gray))
                tvVolume.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.light_gray))
            }

            if (container.isFailureNotEmpty()) {
                holder.itemView.findViewById<CardView>(R.id.choose_cardview).setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.red_cool))
            }

            if(container.volume != null && !container.isFailureNotEmpty()) {
                holder.itemView.findViewById<CardView>(R.id.choose_cardview).setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.green_cool))
            }
        }

        private fun showTakeInactiveContainerAlert(context: Context, next: () -> Any) {
            try {
                lateinit var alertDiaLoG: AlertDialog
                val builder = AlertDialog.Builder(context)
                val view = (context as Activity).layoutInflater.inflate(R.layout.alert_take_inactive_container, null)
                view.findViewById<AppCompatButton>(R.id.acb_alert_inactive_container___accept).setOnClickListener {
                    next()
                    alertDiaLoG.dismiss()
                }
                view.findViewById<AppCompatButton>(R.id.acb_alert_inactive_container___decline).setOnClickListener {
                    alertDiaLoG.dismiss()
                }
                builder.setView(view)
                alertDiaLoG = builder.create()
                alertDiaLoG.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                alertDiaLoG.show()
            } catch (e: Exception) {
                LoG.error( e.stackTraceToString())
            }
        }

       inner class OwnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    }
    interface ContainerPointClickListener {
        fun startContainerService(item: ContainerEntity)
    }

    /********************************************************************************************************************
     ********************************************************************************************************************
     ********************************************************************************************************************
     * **************************************VIEW MODEL
     */
//
//    class PServeFVM(app: Application) : AViewModel(app) {
//
//        private val _platformEntity: MutableLiveData<PlatformEntity> = MutableLiveData(null)
//        val mPlatformEntity: LiveData<PlatformEntity>
//            get() = _platformEntity
//
//
//
//
//    }
}