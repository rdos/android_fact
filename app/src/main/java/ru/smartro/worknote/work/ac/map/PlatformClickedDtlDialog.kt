package ru.smartro.worknote.work.ac.map

import android.content.Intent
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
import kotlinx.android.synthetic.main.dialog_platform_clicked_dtl.*
import kotlinx.android.synthetic.main.alert_failure_finish_way.view.*
import kotlinx.android.synthetic.main.alert_finish_way.view.accept_btn
import ru.smartro.worknote.R
import ru.smartro.worknote.base.AbstractDialog
import ru.smartro.worknote.extensions.*
import ru.smartro.worknote.isShowForUser
import ru.smartro.worknote.work.PlatformEntity
import ru.smartro.worknote.work.platform_serve.PlatformServeActivity
import ru.smartro.worknote.ui.problem.ExtremeProblemActivity
import ru.smartro.worknote.util.StatusEnum
import kotlin.math.min

class PlatformClickedDtlDialog(private val _platform: PlatformEntity, private val _point: Point) : AbstractDialog(), View.OnClickListener {
    private lateinit var mCurrentActivity: AppCompatActivity
    private var mFirstTime = true
    private var mIsServeAgain = false
    private val mOnClickListener = this as View.OnClickListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_platform_clicked_dtl, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //onBindViewHolder
        super.onViewCreated(view, savedInstanceState)
        mCurrentActivity = requireActivity() as MapAct

        val spanCount = min(_platform.containers.size, 10)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_dialog_platform_clicked_dtl)
        recyclerView.layoutManager = GridLayoutManager(context, spanCount)
        recyclerView.adapter = PlatformClickedDtlAdapter(_platform)

        val tvContainersCnt = view.findViewById<TextView>(R.id.tv_dialog_platform_clicked_dtl__containers_cnt)
        tvContainersCnt.text = String.format(getString(R.string.dialog_platform_clicked_dtl__containers_cnt), _platform.containers.size)


        mIsServeAgain = _platform.status != StatusEnum.NEW

        val cvStartServe = view.findViewById<CardView>(R.id.cv_dialog_platform_clicked_dtl__start_serve)
        cvStartServe.isVisible = !mIsServeAgain

        val cvServeAgain = view.findViewById<CardView>(R.id.cv_dialog_platform_clicked_dtl__serve_again)
        cvServeAgain.isVisible = mIsServeAgain
        val tvAddress = view.findViewById<TextView>(R.id.tv_dialog_platform_clicked_dtl__address)
        tvAddress.text = String.format(getString(R.string.dialog_platform_clicked_dtl__address), _platform.address, _platform.srpId)

        val tvPlatformContact = view.findViewById<TextView>(R.id.tv_dialog_platform_clicked_dtl__platform_contact)
        val contactsInfo = _platform.getContactsInfo()
        tvPlatformContact.text = contactsInfo
        tvPlatformContact.isVisible = contactsInfo.isNotEmpty()

        // TODO: 27.10.2021 !! !?
        initButtonsViews()
        view.findViewById<ImageButton>(R.id.ibtn_dialog_platform_clicked_dtl__close).setOnClickListener {
            dismiss()
        }

        view.findViewById<Button>(R.id.btn_dialog_platform_clicked_dtl__serve_again).setOnClickListener(mOnClickListener)
        val btnStartServe = view.findViewById<Button>(R.id.btn_dialog_platform_clicked_dtl__start_serve)
        btnStartServe.setOnClickListener(mOnClickListener)
        if (_platform.isStartServe()) {
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
            R.id.btn_dialog_platform_clicked_dtl__serve_again ,
            R.id.btn_dialog_platform_clicked_dtl__start_serve -> {
                val intent = Intent(requireActivity(), PlatformServeActivity::class.java)
                intent.putExtra("platform_id", _platform.platformId)
                intent.putExtra("mIsServeAgain", mIsServeAgain)
                dismiss()
                startActivityForResult(intent, 88)
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

        // TODO: 22.10.2021 mFirstTime ??!
        if (mFirstTime) {
            mFirstTime = false
        } else {
            dismiss()
        }
    }

    private fun initButtonsViews() {
        platform_detail_fire.setOnClickListener {
            warningCameraShow("Сделайте фото проблемы").let {
                it.accept_btn.setOnClickListener {
                    hideDialog()
                    val intent = Intent(requireActivity(), ExtremeProblemActivity::class.java)
                    intent.putExtra("platform_id", _platform.platformId)
                    dismiss()
                    startActivityForResult(intent, 88)
                }

                it.dismiss_btn.setOnClickListener {
                    hideDialog()
                }
            }
        }

        //коммент инициализации
        platform_location.setOnClickListener {
            val currentActivity = requireActivity() as MapAct
            val drivingModeState = currentActivity.drivingModeState
            if (drivingModeState) {
                currentActivity.warningClearNavigator("У вас уже есть построенный маршрут. Отменить старый и построить новый?")
                    .let {
                        it.accept_btn.setOnClickListener {
                            currentActivity.buildNavigator(_point)
                            dismiss()
                        }
                    }
            } else {
                currentActivity.buildNavigator(_point)
                dismiss()
            }
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
            holder.platformImageView.setImageResource(_platform.getIconDrawableResId())

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
            when (container?.status) {
                StatusEnum.SUCCESS -> {
                    holder.statusImageView.isVisible = true
                    holder.statusImageView.setImageResource(R.drawable.ic_check)
                }
                StatusEnum.ERROR -> {
                    holder.statusImageView.isVisible = true
                    holder.statusImageView.setImageResource(R.drawable.ic_red_check)
                }
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