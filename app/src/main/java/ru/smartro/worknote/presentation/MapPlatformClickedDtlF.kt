package ru.smartro.worknote.presentation

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yandex.mapkit.geometry.Point
import ru.smartro.worknote.*
import ru.smartro.worknote.abs.AbstractDialog
import ru.smartro.worknote.presentation.ac.MainAct
import ru.smartro.worknote.awORKOLDs.util.StatusEnum
import ru.smartro.worknote.presentation.platform_serve.PServeF
import ru.smartro.worknote.presentation.platform_serve.PServeGroupByContainersF
import ru.smartro.worknote.work.PlatformEntity
import kotlin.math.min

class MapPlatformClickedDtlF(private val _platform: PlatformEntity, private val _point: Point,
                             private val listener: MapPlatformSBehaviorAdapter.PlatformClickListener
) : AbstractDialog(), View.OnClickListener {
    private lateinit var mCurrentActivity: AppCompatActivity
    private val mOnClickListener = this as View.OnClickListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.act_map__dialog_platform_clicked_dtl, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //onBindViewHolder
        super.onViewCreated(view, savedInstanceState)
        LoG.warn( "R_dos")
        mCurrentActivity = requireActivity() as MainAct

        val spanCount = min(_platform.containers.size, 10)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_dialog_platform_clicked_dtl)
        recyclerView.layoutManager = GridLayoutManager(context, spanCount)
        recyclerView.adapter = PlatformClickedDtlAdapter(_platform)

        val tvContainersCnt = view.findViewById<TextView>(R.id.tv_dialog_platform_clicked_dtl__containers_cnt)
        tvContainersCnt.text = String.format(getString(R.string.dialog_platform_clicked_dtl__containers_cnt), _platform.containers.size)


        val isServeAgain = _platform.getStatusPlatform() != StatusEnum.NEW

        val cvStartServe = view.findViewById<CardView>(R.id.cv_dialog_platform_clicked_dtl__start_serve)
        cvStartServe.isVisible = !isServeAgain

        val cvServeAgain = view.findViewById<CardView>(R.id.cv_dialog_platform_clicked_dtl__serve_again)
        cvServeAgain.isVisible = isServeAgain
        val tvAddress = view.findViewById<TextView>(R.id.tv_dialog_platform_clicked_dtl__address)
        tvAddress.text = String.format(getString(R.string.dialog_platform_clicked_dtl__address), _platform.address, _platform.srpId)

        val tvPlatformContact = view.findViewById<TextView>(R.id.tv_dialog_platform_clicked_dtl__platform_contact)
        val contactsInfo = _platform.getContactsInfo()
        tvPlatformContact.text = contactsInfo
        tvPlatformContact.isVisible = contactsInfo.isNotEmpty()

        // TODO: 27.10.2021 !! !?
        initButtonsViews(view)
        view.findViewById<ImageButton>(R.id.ibtn_dialog_platform_clicked_dtl__close).setOnClickListener {
            dismiss()
        }

        view.findViewById<Button>(R.id.btn_dialog_platform_clicked_dtl__serve_again).setOnClickListener(mOnClickListener)
        val btnStartServe = view.findViewById<Button>(R.id.btn_dialog_platform_clicked_dtl__start_serve)
        btnStartServe.setOnClickListener(mOnClickListener)
        if (_platform.getStatusPlatform() == StatusEnum.UNFINISHED) {
            btnStartServe.setText(R.string.start_serve_again)
        }
        val tvName = view.findViewById<TextView>(R.id.tv_dialog_platform_clicked_dtl__name)
        tvName.text = _platform.name
        val tvOrderTime = view.findViewById<TextView>(R.id.tv_dialog_platform_clicked_dtl__order_time)
        val orderTime = _platform.getOrderTime()
        if (orderTime.isShowForUser()) {
            tvOrderTime.text = orderTime
            tvOrderTime.setTextColor(_platform.getOrderTimeColor(requireContext()))
            tvOrderTime.isVisible = true
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_dialog_platform_clicked_dtl__serve_again  -> {
                dismiss()
                listener.startPlatformService(_platform)
            }
            R.id.btn_dialog_platform_clicked_dtl__start_serve -> {
                dismiss()
                listener.startPlatformService(_platform)
            }
        }

    }


    override fun onResume() {
        super.onResume()
        val params: WindowManager.LayoutParams? = dialog?.window?.attributes
        params?.height = FrameLayout.LayoutParams.WRAP_CONTENT
        params?.width = FrameLayout.LayoutParams.MATCH_PARENT
        params?.horizontalMargin = 56f
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.attributes = params
    }

    private fun initButtonsViews(view: View) {
        view.findViewById<ImageButton>(R.id.platform_detail_fire).setOnClickListener {
            dismiss()
            listener.openFailureFire(_platform)
        }

        //коммент инициализации
        view.findViewById<ImageButton>(R.id.platform_location).setOnClickListener {
            dismiss()
            listener.navigatePlatform(_point)
        }
    }
    

    inner class PlatformClickedDtlAdapter(private val _platform: PlatformEntity) :
        RecyclerView.Adapter<PlatformClickedDtlAdapter.PlatformClickedDtlHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlatformClickedDtlHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dialog_platform_clicked_dtl, parent, false)
            return PlatformClickedDtlHolder(view)
        }

        override fun getItemCount(): Int {
            return _platform.containers.size
        }

        override fun onBindViewHolder(holder: PlatformClickedDtlHolder, position: Int) {
            val container = _platform.containers[position]
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




}