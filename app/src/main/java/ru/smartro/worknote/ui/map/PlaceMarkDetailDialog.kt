package ru.smartro.worknote.ui.map

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.alert_failure_finish_way.view.*
import kotlinx.android.synthetic.main.alert_finish_way.view.accept_btn
import kotlinx.android.synthetic.main.alert_point_detail.*
import ru.smartro.worknote.R
import ru.smartro.worknote.adapter.container_service.ContainerDetailAdapter
import ru.smartro.worknote.extensions.hideDialog
import ru.smartro.worknote.extensions.warningCameraShow
import ru.smartro.worknote.service.database.entity.work_order.PlatformEntity
import ru.smartro.worknote.ui.platform_service.PlatformServiceActivity
import ru.smartro.worknote.ui.problem.ExtremeProblemActivity
import ru.smartro.worknote.util.StatusEnum


class PlaceMarkDetailDialog(private val platform: PlatformEntity) : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.alert_point_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
    }
    private fun initViews() {
        point_detail_start_service.setOnClickListener {
            val intent = Intent(requireActivity(), PlatformServiceActivity::class.java)
            intent.putExtra("platform_id", platform.platformId)
            dismiss()
            startActivity(intent)
        }

        point_detail_fire.setOnClickListener {
            warningCameraShow("Сделайте фото проблемы").let {
                it.accept_btn.setOnClickListener {
                    hideDialog()
                    val intent = Intent(requireActivity(), ExtremeProblemActivity::class.java)
                    intent.putExtra("platform_id", platform.platformId)
                    dismiss()
                    startActivity(intent)
                }

                it.dismiss_btn.setOnClickListener {
                    hideDialog()
                }
            }

        }
        bottom_card.isVisible = platform.status == StatusEnum.NEW
        point_detail_address.text = "${platform.address} \n ${platform.srpId} ${platform.containers!!.size} конт."

        point_detail_rv.adapter = ContainerDetailAdapter(platform.containers!!)
        point_detail_close.setOnClickListener {
            dismiss()
        }
    }
}