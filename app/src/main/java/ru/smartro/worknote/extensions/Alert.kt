package ru.smartro.worknote.extensions

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.alert_accept_task.view.title_tv
import kotlinx.android.synthetic.main.alert_failure_finish_way.view.*
import kotlinx.android.synthetic.main.alert_finish_way.view.*
import kotlinx.android.synthetic.main.alert_point_detail.view.*
import kotlinx.coroutines.*
import ru.smartro.worknote.R
import ru.smartro.worknote.adapter.container_service.ContainerDetailAdapter
import ru.smartro.worknote.service.database.entity.problem.CancelWayReasonEntity
import ru.smartro.worknote.service.database.entity.work_order.PlatformEntity
import ru.smartro.worknote.util.StatusEnum

private lateinit var loadingDialog: AlertDialog
private lateinit var customDialog: AlertDialog

fun AppCompatActivity.loadingShow() {
    try {
        val builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val view = inflater.inflate(R.layout.alert_loading, null)
        builder.setView(view)
        builder.setCancelable(false)
        loadingDialog = builder.create()
        loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadingDialog.show()
    } catch (e: Exception) {
        println()
    }
}

fun AppCompatActivity.warningCameraShow(title: String): View {
    val builder = AlertDialog.Builder(this)
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.alert_warning_camera, null)
    view.title_tv.text = title
    builder.setView(view)
    builder.setCancelable(false)
    customDialog = builder.create()
    customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    customDialog.show()
    return view
}

fun AppCompatActivity.warningNavigatePlatform(): View {
    val builder = AlertDialog.Builder(this)
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.alert_navigate_platform, null)
    builder.setView(view)
    builder.setCancelable(false)
    customDialog = builder.create()
    view.dismiss_btn.setOnClickListener {
        customDialog.dismiss()
    }
    customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    customDialog.show()
    return view
}

fun AppCompatActivity.warningClearNavigator(title: String): View {
    val builder = AlertDialog.Builder(this)
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.alert_clear_navigator, null)
    builder.setView(view)
    builder.setCancelable(false)
    customDialog = builder.create()
    view.title_tv.text = title
    view.dismiss_btn.setOnClickListener {
        customDialog.dismiss()
    }
    customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    customDialog.show()
    return view
}

fun AppCompatActivity.alertOnPoint(): View {
    val builder = AlertDialog.Builder(this)
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.alert_on_point, null)
    builder.setView(view)
    customDialog = builder.create()
    customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    customDialog.show()
    return view
}

fun Fragment.warningCameraShow(title: String): View {
    val builder = AlertDialog.Builder(this.requireContext())
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.alert_warning_camera, null)
    view.title_tv.text = title
    builder.setView(view)
    builder.setCancelable(false)
    customDialog = builder.create()
    customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    customDialog.show()
    return view
}

fun AppCompatActivity.showSuccessComplete(): View {
    val builder = AlertDialog.Builder(this)
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.alert_successful_complete, null)
    builder.setView(view)
    builder.setCancelable(false)
    customDialog = builder.create()
    customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    customDialog.show()
    return view
}

fun AppCompatActivity.showCompleteWaybill(): View {
    val builder = AlertDialog.Builder(this)
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.alert_finish_way, null)
    view.weight_tg.setOnCheckedChangeListener { compoundButton, b ->
        if (b) {
            view.volume_tg.isChecked = !b
            view.weight_tg.setTextColor(Color.WHITE)
            view.comment_et_out.hint = (getString(R.string.enter_weight_hint))
        } else {
            view.weight_tg.setTextColor(Color.BLACK)
        }
    }
    view.volume_tg.setOnCheckedChangeListener { compoundButton, b ->
        if (b) {
            view.weight_tg.isChecked = !b
            view.volume_tg.setTextColor(Color.WHITE)
            view.comment_et_out.hint = getString(R.string.enter_volume_hint)
        } else {
            view.volume_tg.setTextColor(Color.BLACK)
        }
    }
    builder.setView(view)
    customDialog = builder.create()
    customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    customDialog.show()
    return view
}


fun AppCompatActivity.fillKgoVolume(): View {
    val builder = AlertDialog.Builder(this)
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.alert_fill_kgo, null)
    builder.setView(view)
    customDialog = builder.create()
    customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    customDialog.show()
    return view
}

fun Fragment.fillKgoVolume(): View {
    val builder = AlertDialog.Builder(requireContext())
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.alert_fill_kgo, null)
    builder.setView(view)
    customDialog = builder.create()
    customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    customDialog.show()
    return view
}

fun AppCompatActivity.warningDelete(title: String): View {
    val builder = AlertDialog.Builder(this)
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.alert_warning_delete, null)
    view.title_tv.text = title
    builder.setView(view)
    builder.setCancelable(false)
    customDialog = builder.create()
    customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    customDialog.show()
    return view
}

fun AppCompatActivity.showClickedPointDetail(point: PlatformEntity): View {
    val customDialog: AlertDialog
    val builder = AlertDialog.Builder(this)
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.alert_point_detail, null)
    builder.setView(view)
    customDialog = builder.create()
    view.bottom_card.isVisible = point.status == StatusEnum.NEW
    view.point_detail_address.text = "${point.address} \n ${point.srpId} ${point.containers.size} конт."
    view.point_detail_close.setOnClickListener {
        customDialog.dismiss()
    }
    view.point_detail_rv.adapter = ContainerDetailAdapter(point.containers)
    customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    customDialog.show()
    return view
}

fun AppCompatActivity.warningContainerFailure(title: String): View {
    val builder = AlertDialog.Builder(this)
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.alert_warning_failure, null)
    view.title_tv.text = title
    builder.setView(view)
    customDialog = builder.create()
    customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    customDialog.show()
    return view
}

fun AppCompatActivity.warningAlert(title: String): View {
    val builder = AlertDialog.Builder(this)
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.alert_warning, null)
    builder.setView(view)
    customDialog = builder.create()
    view.title_tv.text = title
    view.dismiss_btn.setOnClickListener {
        customDialog.dismiss()
    }
    customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    customDialog.show()
    return view
}

fun AppCompatActivity.showEarlyComplete(reasons: List<CancelWayReasonEntity>): View {
    val builder = AlertDialog.Builder(this)
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.alert_failure_finish_way, null)
    val reasonsString = reasons.map { it.problem }
    view.reason_et.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, android.R.id.text1, reasonsString))
    view.reason_et.setOnClickListener {
        view.reason_et.showDropDown()
    }
    view.early_weight_tg.setOnCheckedChangeListener { _, b ->
        if (b) {
            view.early_volume_tg.isChecked = !b
            view.early_weight_tg.setTextColor(Color.WHITE)
            view.unload_value_et_out.hint = (getString(R.string.enter_weight_hint))
        } else {
            view.early_weight_tg.setTextColor(Color.BLACK)
        }
    }
    view.early_volume_tg.setOnCheckedChangeListener { _, b ->
        if (b) {
            view.early_weight_tg.isChecked = !b
            view.early_volume_tg.setTextColor(Color.WHITE)
            view.unload_value_et_out.hint = (getString(R.string.enter_volume_hint))
        } else {
            view.early_volume_tg.setTextColor(Color.BLACK)
        }
    }
    view.reason_et.setOnFocusChangeListener { _, _ ->
        view.reason_et.showDropDown()
    }
    builder.setView(view)
    customDialog = builder.create()
    customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    customDialog.show()
    return view
}

fun Fragment.warningDelete(title: String): View {
    val builder = AlertDialog.Builder(activity!!)
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.alert_warning_delete, null)
    view.title_tv.text = title
    builder.setView(view)
    builder.setCancelable(false)
    customDialog = builder.create()
    customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    customDialog.show()
    return view
}

fun AppCompatActivity.hideDialog() {
    try {
        customDialog.dismiss()
    } catch (e: java.lang.Exception) {

    }
}

fun AppCompatActivity.loadingHide() {
    try {
        loadingDialog.dismiss()
    } catch (e: java.lang.Exception) {

    }
}

fun Fragment.loadingHide() {
    try {
        loadingDialog.dismiss()
    } catch (e: java.lang.Exception) {

    }
}

fun Fragment.loadingShow() {
    try {
        val builder = AlertDialog.Builder(activity!!)
        val inflater = this.layoutInflater
        val view = inflater.inflate(R.layout.alert_loading, null)
        builder.setView(view)
        builder.setCancelable(false)
        loadingDialog = builder.create()
        loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadingDialog.show()
    } catch (e: Exception) {
        println()
    }
}

fun Fragment.hideDialog(time: Long = 0) {
    try {
        println()
        CoroutineScope(Dispatchers.IO).launch {
            println()
            delay(time)
            withContext(Dispatchers.Main) {
                customDialog.dismiss()
            }
            println()
        }
        println()


    } catch (e: Exception) {
        println()
    }
}



