package ru.smartro.worknote.presentation

import android.graphics.drawable.TransitionDrawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.smartro.worknote.*
import ru.smartro.worknote.abs.ADF
import ru.smartro.worknote.ac.SmartROllc
import ru.smartro.worknote.log.todo.ConfigName
import ru.smartro.worknote.log.todo.PlatformEntity
import ru.smartro.worknote.log.todo.StatusEnum
import kotlin.math.min

//todo: MapPlatformsDF MapPlatformsDF MapPlatformsDF???ИЛИ MapPlatforms(on)MapObjectTap(DF)=
class DFPMap : ADF(), View.OnClickListener {
    private lateinit var platformTODO: PlatformEntity
    private val vm: VMPserve by activityViewModels()
    private val mOnClickListener = this as View.OnClickListener
    private val isModeUnload by lazy {
        vm.database.getConfigBool(ConfigName.AAPP__IS_MODE__UNLOAD)
    }

    override fun onGetLayout(): Int {
        return R.layout.df_map_platforms__map_object_tap
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_dialog_platform_clicked_dtl__serve_again,
            R.id.btn_dialog_platform_clicked_dtl__start_serve  -> {
                if (isModeUnload) {
                    toast("В режиме выгрузка нельзя обслуживать КП")
                    return
                }

                if(platformTODO.needCleanup == true) {
                    navigateNext(R.id.PMapNeedCleanupDF, platformTODO.platformId)
                } else {
                    // TODO ::: CHECK NEEDED !!!
                    navigateNext(R.id.PMapWarnDF, platformTODO.platformId, getString(R.string.warning_gps_exception))
                }
            }

            R.id.platform_detail_fire -> {
                if (isModeUnload) {
                    toast("В режиме выгрузка нельзя обслуживать КП")
                    return
                }
                vm.setPlatformEntity(platformTODO)
                navigateNext(R.id.PhotoFailureMediaF, platformTODO.platformId)}
            R.id.ibtn_dialog_platform_clicked_dtl__close -> {
                navigate(R.id.MapPlatformsF)
            }
            R.id.platform_location -> {
                findNavController().previousBackStackEntry?.savedStateHandle?.set("navigatePlatform", true)
                navigate(R.id.MapPlatformsF)
            }
        }

    }


    override fun onInitLayoutView(sview: SmartROllc): Boolean {
        platformTODO = vm.getPlatformEntity()
        //onBindViewHolder
//        super.onViewCreated(view, savedInstanceState)

        val spanCount = min(platformTODO.containerS.size, 10)
        val recyclerView = sview.findViewById<RecyclerView>(R.id.rv_dialog_platform_clicked_dtl)
        recyclerView.layoutManager = GridLayoutManager(context, spanCount)
        recyclerView.adapter = PlatformClickedDtlAdapter(platformTODO)

        val tvContainersCnt = sview.findViewById<TextView>(R.id.tv_dialog_platform_clicked_dtl__containers_cnt)
        tvContainersCnt.text = String.format(getString(R.string.dialog_platform_clicked_dtl__containers_cnt), platformTODO.containerS.size)


        val isServeAgain = platformTODO.getStatusPlatform() != StatusEnum.NEW


        val tvAddress = sview.findViewById<TextView>(R.id.tv_dialog_platform_clicked_dtl__address)
        tvAddress.text = String.format(getString(R.string.dialog_platform_clicked_dtl__address), platformTODO.address, platformTODO.srpId)

        val tvPlatformContact = sview.findViewById<TextView>(R.id.tv_dialog_platform_clicked_dtl__platform_contact)
        val contactsInfo = platformTODO.getContactsInfo()
        tvPlatformContact.text = contactsInfo
        // TODO::: СВЕЖО 
        tvPlatformContact.isVisible = contactsInfo.trim().isNotEmpty()

        // TODO: 27.10.2021 !! R_DOS! = СПРОСИть ОН знает
        LOG.warn("R_DOS sview.findViewById<ImageButton>(R.id.platform_detail_fire).setOnClickListener(mOnClickListener)")
        sview.findViewById<ImageButton>(R.id.platform_detail_fire).setOnClickListener(mOnClickListener)

        //коммент инициализации
        sview.findViewById<ImageButton>(R.id.platform_location).setOnClickListener(mOnClickListener)
        sview.findViewById<ImageButton>(R.id.ibtn_dialog_platform_clicked_dtl__close).setOnClickListener(mOnClickListener)

        sview.findViewById<AppCompatButton>(R.id.btn_dialog_platform_clicked_dtl__serve_again).apply {
            isVisible = isServeAgain
            setOnClickListener(mOnClickListener)
        }


        val btnStartServe = sview.findViewById<LinearLayoutCompat>(R.id.btn_dialog_platform_clicked_dtl__start_serve)
        btnStartServe.isVisible = !isServeAgain
        btnStartServe.setOnClickListener(mOnClickListener)
        if (platformTODO.getStatusPlatform() == StatusEnum.UNFINISHED) {
            sview.findViewById<AppCompatTextView>(R.id.actv__df_map_object_tap__serve_text).setText(R.string.start_serve_again)
        }
        val tvName = sview.findViewById<TextView>(R.id.tv_dialog_platform_clicked_dtl__name)
        tvName.text = platformTODO.name
        val tvOrderTime = sview.findViewById<TextView>(R.id.tv_dialog_platform_clicked_dtl__order_time)
        val orderTime = platformTODO.getOrderTime()
        if (orderTime.isShowForUser()) {
            tvOrderTime.text = orderTime
            tvOrderTime.setTextColor(platformTODO.getOrderTimeColor(requireContext()))
            tvOrderTime.isVisible = true
        }


//        ключ toggle
        bottomWrapperInit(sview)

        val acivCleanup = sview.findViewById<AppCompatImageView>(R.id.aciv__df_map_object_tap__cleanup)
        if(platformTODO.needCleanup == false) {
            acivCleanup.visibility = View.GONE
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
               val transitionBackground = btnStartServe.background as TransitionDrawable
                transitionBackground.startTransition(500)
            }, 500)
        }

        return false
    }

    private fun bottomWrapperInit(sview: SmartROllc) {
        //   переключатель! toggle
        val llcInfoUnload = sview.findViewById<LinearLayoutCompat>(R.id.llc_info_unload)
        val llcButtonWrapper = sview.findViewById<LinearLayoutCompat>(R.id.llc_button_wrapper)
        llcButtonWrapper.visibility = View.VISIBLE
        llcInfoUnload.visibility = View.GONE
        if (isModeUnload) {
            llcButtonWrapper.visibility = View.GONE
            llcInfoUnload.visibility = View.VISIBLE
        }
    }


    private fun setUseButtonStyleBackgroundGreen(appCompatButton: AppCompatButton) {
        appCompatButton.setBackgroundDrawable(ContextCompat.getDrawable(getAct(), R.drawable.bg_button_green__usebutton))
    }


    override fun onLiveData() {
//        TODO("Not yet implemented")
    }

    override fun onBindLayoutState(): Boolean {
        return false
    }

    override fun onBackPressed() {
        navigateBack()
    }
    

    inner class PlatformClickedDtlAdapter(private val _platform: PlatformEntity) :
        RecyclerView.Adapter<PlatformClickedDtlAdapter.PlatformClickedDtlHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlatformClickedDtlHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dialog_platform_clicked_dtl, parent, false)
            return PlatformClickedDtlHolder(view)
        }

        override fun getItemCount(): Int {
            return _platform.containerS.size
        }

        override fun onBindViewHolder(holder: PlatformClickedDtlHolder, position: Int) {
            val container = _platform.containerS[position]

//            holder.tv_title.text = container!!.number

            if(container != null)
                holder.containerIcon.setImageResource(container.getIconFromStatus())

            holder.clParent.setOnClickListener{
                var toastText = ""
                if (!_platform.name.isNullOrEmpty()) {
                    toastText += "Имя = ${_platform.name} \n"
                }
                toastText += "${container?.typeName} \n"
                toastText += "Объем = ${container?.constructiveVolume} \n"
                if (!container?.client.isNullOrEmpty()) {
                    toastText += "Клиент = ${container?.client} \n"
                }
                if (!container?.client.isNullOrEmpty()) {
                    toastText += "Контакт = ${container?.contacts} \n"
                }
                toast(toastText)
            }
            holder.statusImageView.isVisible = false

            val containerStatus = container?.getStatusContainer()

            if(containerStatus == StatusEnum.SUCCESS) {
                holder.statusImageView.isVisible = true
                holder.statusImageView.setImageResource(R.drawable.ic_check)
            } else if(containerStatus == StatusEnum.ERROR && container.isActiveToday) {
                holder.statusImageView.isVisible = true
                holder.statusImageView.setImageResource(R.drawable.ic_red_check)
            }
        }

        inner class PlatformClickedDtlHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//            val tv_title = itemView.findViewById<TextView>(R.id.tv_item_dialog_platform_clicked_dtl)
            val containerIcon = itemView.findViewById<AppCompatImageView>(R.id.ib_item_dialog_platform_clicked_dtl__container)
            val statusImageView = itemView.findViewById<AppCompatImageView>(R.id.iv_item_dialog_platform_clicked_dtl__status)
            val clParent = itemView.findViewById<ConstraintLayout>(R.id.cl_item_dialog_platform_clicked_dtl)
        }
    }


    override fun onResume() {
        super.onResume()
        //todo:r_dos:: сделать прозрачными
        val params: WindowManager.LayoutParams? = dialog?.window?.attributes
        params?.height = FrameLayout.LayoutParams.WRAP_CONTENT
        params?.width = FrameLayout.LayoutParams.MATCH_PARENT
        params?.horizontalMargin = 56f
//        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.attributes = params
    }

}