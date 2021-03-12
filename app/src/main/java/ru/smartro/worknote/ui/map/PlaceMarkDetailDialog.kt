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
import com.google.gson.Gson
import kotlinx.android.synthetic.main.alert_point_detail.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.adapter.container_service.ContainerPointDetailAdapter
import ru.smartro.worknote.service.database.entity.way_task.WayPointEntity
import ru.smartro.worknote.ui.point_service.PointServiceActivity
import ru.smartro.worknote.ui.problem.ContainerProblemActivity
import ru.smartro.worknote.util.StatusEnum


class PlaceMarkDetailDialog(val wayPoint: WayPointEntity) : DialogFragment() {
    private val viewModel: MapViewModel by viewModel()
    private val POINT_SERVICE_CODE = 10
    private val REQUEST_EXIT = 41

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
            val intent = Intent(requireActivity(), PointServiceActivity::class.java)
            val itemJson = Gson().toJson(wayPoint)
            intent.putExtra("container", itemJson)
            dismiss()
            startActivityForResult(intent, POINT_SERVICE_CODE)
        }

        point_detail_fire.setOnClickListener {
            val intent = Intent(requireActivity(), ContainerProblemActivity::class.java)
            intent.putExtra("wayPoint", Gson().toJson(wayPoint))
            intent.putExtra("isContainerProblem", false)
            viewModel.createServedPointEntityIfNull(wayPoint)
            dismiss()
            startActivityForResult(intent, REQUEST_EXIT)
        }
        bottom_card.isVisible = wayPoint.status == StatusEnum.empty
        point_detail_address.text = "${wayPoint.address} \n ${wayPoint.srp_id} ${wayPoint.cs!!.size} конт."

        point_detail_rv.adapter = ContainerPointDetailAdapter(wayPoint.cs!!)
        point_detail_close.setOnClickListener {
            dismiss()
        }
    }
}