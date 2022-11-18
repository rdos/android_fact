package ru.smartro.worknote.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.smartro.worknote.*
import ru.smartro.worknote.abs.FragmentDialogA
import ru.smartro.worknote.andPOintD.SmartROllc
import ru.smartro.worknote.awORKOLDs.util.StatusEnum
import ru.smartro.worknote.presentation.work.ServePlatformVM
import ru.smartro.worknote.presentation.work.ConfigName
import ru.smartro.worknote.presentation.work.PlatformEntity
import kotlin.math.min

//todo: MapPlatformsDF MapPlatformsDF MapPlatformsDF???ИЛИ MapPlatforms(on)MapObjectTap(DF)=
class PMapFragmentDialog : FragmentDialogA(), View.OnClickListener {
    private lateinit var TbIboy__item: PlatformEntity
    private val vm: ServePlatformVM by activityViewModels()
    private val mOnClickListener = this as View.OnClickListener

    override fun onGetLayout(): Int {
        return R.layout.df_map_platforms__map_object_tap
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_dialog_platform_clicked_dtl__serve_again,
            R.id.btn_dialog_platform_clicked_dtl__start_serve  -> {
                val isModeUnload = vm.database.getConfigBool(ConfigName.AAPP__IS_MODE__UNLOAD)
                if (isModeUnload) {
                    toast("В режиме выгрузка нельзя обслуживать КП")
                    return
                }
                navigateNext(R.id.WarnDF, TbIboy__item.platformId, getString(R.string.warning_gps_exception))
            }

            R.id.platform_detail_fire -> {
                val isModeUnload = vm.database.getConfigBool(ConfigName.AAPP__IS_MODE__UNLOAD)
                if (isModeUnload) {
                    toast("В режиме выгрузка нельзя обслуживать КП")
                    return
                }
                vm.setPlatformEntity(TbIboy__item)
                navigateNext(R.id.PhotoFailureMediaF, TbIboy__item.platformId)}
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
        TbIboy__item = vm.getPlatformEntity()
        //onBindViewHolder
//        super.onViewCreated(view, savedInstanceState)

        val spanCount = min(TbIboy__item.containerS.size, 10)
        val recyclerView = sview.findViewById<RecyclerView>(R.id.rv_dialog_platform_clicked_dtl)
        recyclerView.layoutManager = GridLayoutManager(context, spanCount)
        recyclerView.adapter = PlatformClickedDtlAdapter(TbIboy__item)

        val tvContainersCnt = sview.findViewById<TextView>(R.id.tv_dialog_platform_clicked_dtl__containers_cnt)
        tvContainersCnt.text = String.format(getString(R.string.dialog_platform_clicked_dtl__containers_cnt), TbIboy__item.containerS.size)


        val isServeAgain = TbIboy__item.getStatusPlatform() != StatusEnum.NEW


        val tvAddress = sview.findViewById<TextView>(R.id.tv_dialog_platform_clicked_dtl__address)
        tvAddress.text = String.format(getString(R.string.dialog_platform_clicked_dtl__address), TbIboy__item.address, TbIboy__item.srpId)

        val tvPlatformContact = sview.findViewById<TextView>(R.id.tv_dialog_platform_clicked_dtl__platform_contact)
        val contactsInfo = TbIboy__item.getContactsInfo()
        tvPlatformContact.text = contactsInfo
        // TODO::: СВЕЖО 
        tvPlatformContact.isVisible = contactsInfo.trim().isNotEmpty()

        // TODO: 27.10.2021 !! R_DOS! = СПРОСИть ОН знает
        LOG.warn("R_DOS sview.findViewById<ImageButton>(R.id.platform_detail_fire).setOnClickListener(mOnClickListener)")
        val isModeUnload = vm.database.getConfigBool(ConfigName.AAPP__IS_MODE__UNLOAD)
        sview.findViewById<ImageButton>(R.id.platform_detail_fire).setOnClickListener(mOnClickListener)

        //коммент инициализации
        sview.findViewById<ImageButton>(R.id.platform_location).setOnClickListener(mOnClickListener)
        sview.findViewById<ImageButton>(R.id.ibtn_dialog_platform_clicked_dtl__close).setOnClickListener(mOnClickListener)

        sview.findViewById<AppCompatButton>(R.id.btn_dialog_platform_clicked_dtl__serve_again).apply {
            isVisible = isServeAgain
            setOnClickListener(mOnClickListener)
        }


        val btnStartServe = sview.findViewById<Button>(R.id.btn_dialog_platform_clicked_dtl__start_serve)
        btnStartServe.isVisible = !isServeAgain
        btnStartServe.setOnClickListener(mOnClickListener)
        if (TbIboy__item.getStatusPlatform() == StatusEnum.UNFINISHED) {
            btnStartServe.setText(R.string.start_serve_again)
        }
        val tvName = sview.findViewById<TextView>(R.id.tv_dialog_platform_clicked_dtl__name)
        tvName.text = TbIboy__item.name
        val tvOrderTime = sview.findViewById<TextView>(R.id.tv_dialog_platform_clicked_dtl__order_time)
        val orderTime = TbIboy__item.getOrderTime()
        if (orderTime.isShowForUser()) {
            tvOrderTime.text = orderTime
            tvOrderTime.setTextColor(TbIboy__item.getOrderTimeColor(requireContext()))
            tvOrderTime.isVisible = true
        }


//        ключ toggle
        bottomWrapperInit(sview, isModeUnload)
        return false
    }

    private fun bottomWrapperInit(sview: SmartROllc, isModeUnload: Boolean) {
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
            if(container?.isActiveToday == true)
                holder.platformImageView.setImageResource(_platform.getIconFromStatus())
            else
                holder.platformImageView.setImageResource(_platform.getInactiveIcon())

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
            val platformImageView = itemView.findViewById<ImageView>(R.id.ib_item_dialog_platform_clicked_dtl__platform)
            val statusImageView = itemView.findViewById<ImageView>(R.id.iv_item_dialog_platform_clicked_dtl__status)
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