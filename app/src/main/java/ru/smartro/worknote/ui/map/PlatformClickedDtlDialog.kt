package ru.smartro.worknote.ui.map

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yandex.mapkit.geometry.Point
import kotlinx.android.synthetic.main.dialog_platform_clicked_dtl.*
import kotlinx.android.synthetic.main.alert_failure_finish_way.view.*
import kotlinx.android.synthetic.main.alert_finish_way.view.accept_btn
import ru.smartro.worknote.R
import ru.smartro.worknote.extensions.hideDialog
import ru.smartro.worknote.extensions.warningCameraShow
import ru.smartro.worknote.extensions.warningClearNavigator
import ru.smartro.worknote.service.database.entity.work_order.PlatformEntity
import ru.smartro.worknote.ui.platform_service.PlatformServeActivity
import ru.smartro.worknote.ui.problem.ExtremeProblemActivity
import ru.smartro.worknote.util.StatusEnum
import kotlin.math.min

class PlatformClickedDtlDialog(private val _platform: PlatformEntity, private val _point: Point) : DialogFragment() {
    private lateinit var mCurrentActivity: AppCompatActivity
    private var mFirstTime = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_platform_clicked_dtl, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mCurrentActivity = requireActivity() as MapActivity

        view.findViewById<Button>(R.id.btn_dialog_platform_clicked_dtl__start_serve).setOnClickListener {
            val intent = Intent(requireActivity(), PlatformServeActivity::class.java)
            intent.putExtra("platform_id", _platform.platformId)
            dismiss()
            startActivityForResult(intent, 88)
        }



        val spanCount = min(_platform.containers.size, 10)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_dialog_platform_clicked_dtl)
        recyclerView.layoutManager = GridLayoutManager(context, spanCount)
        recyclerView.adapter = PlatformClickedDtlAdapter(_platform)

        val tvContainersCnt = view.findViewById<TextView>(R.id.tv_dialog_platform_clicked_dtl__containers_cnt)
        tvContainersCnt.text = String.format(getString(R.string.dialog_platform_clicked_dtl__containers_cnt), _platform.containers.size)

        initViews()

    }

    override fun onResume() {
        super.onResume()
        val params: WindowManager.LayoutParams = dialog!!.window!!.attributes
        params.height = FrameLayout.LayoutParams.WRAP_CONTENT
        params.width = FrameLayout.LayoutParams.MATCH_PARENT
        params.horizontalMargin = 56f
        dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.window!!.attributes = params

        // TODO: 22.10.2021 mFirstTime ??!
        if (mFirstTime) {
            mFirstTime = false
        } else {
            dismiss()
        }
    }

    private fun initViews() {

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
            val currentActivity = requireActivity() as MapActivity
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
        bottom_card.isVisible = _platform.status == StatusEnum.NEW
        point_detail_address.text = "${_platform.address} \n ${_platform.srpId}"

        point_detail_close.setOnClickListener {
            dismiss()
        }

    }

    class PlatformClickedDtlAdapter(private val _platform: PlatformEntity) :
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
        class PlatformClickedDtlHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//            val tv_title = itemView.findViewById<TextView>(R.id.tv_item_dialog_platform_clicked_dtl)
            val platformImageView = itemView.findViewById<ImageView>(R.id.iv_item_dialog_platform_clicked_dtl__platform)
            val statusImageView = itemView.findViewById<ImageView>(R.id.iv_item_dialog_platform_clicked_dtl__status)

        }
    }


}