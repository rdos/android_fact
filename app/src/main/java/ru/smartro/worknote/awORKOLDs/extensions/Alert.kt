package ru.smartro.worknote.awORKOLDs.extensions

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.alert_clear_navigator.view.dismiss_btn
import kotlinx.android.synthetic.main.alert_finish_way.view.*
import kotlinx.android.synthetic.main.alert_warning_camera.view.title_tv
import kotlinx.android.synthetic.main.dialog_early_complete.view.*
import kotlinx.coroutines.*
import ru.smartro.worknote.A_SLEEP_TIME_1_83__MS
import ru.smartro.worknote.R
import ru.smartro.worknote.Snull
import ru.smartro.worknote.awORKOLDs.base.AbstractDialog
import ru.smartro.worknote.awORKOLDs.service.database.entity.problem.CancelWayReasonEntity
import ru.smartro.worknote.log.AAct

private lateinit var loadingDialog: AlertDialog
private lateinit var mCustomDialog: AlertDialog
private val TAG = "Alert--AAA"

private fun showCustomDialog(builder: AlertDialog.Builder) {
    Log.i(TAG, "showCustomDialog.before")
    try {
        mCustomDialog = builder.create()
        mCustomDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mCustomDialog.show()
    }  catch (e: Exception) {
        // TODO: 02.11.2021
        Log.e(TAG, "showCustomDialog", e)
    }
    Log.d(TAG, "showCustomDialog.after")
}


//showDlgPickup!r_dos
fun AAct.showDlgPickup(): View {
    val builder = AlertDialog.Builder(this)
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.act_platformserve__pickup__alert_dialog, null)
    builder.setView(view)
    showCustomDialog(builder)
    return view
}


private fun showLoadingDialog(builder: AlertDialog.Builder) {
    Log.i(TAG, "showLoadingDialog.before")
    try {
        loadingDialog = builder.create()
        loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadingDialog.show()
    }  catch (e: Exception) {
        // TODO: 02.11.2021
        Log.e(TAG, "showLoadingDialog", e)
    }
    Log.d(TAG, "showLoadingDialog.after")
}

fun AppCompatActivity.showingProgress(text: String? = null) {
    try {
        val builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val view = inflater.inflate(R.layout.alert_loading, null)
        text?.let {
            if (text != Snull) {
                val tv = view.findViewById<TextView>(R.id.tv_alert_loading)
                val oldText = tv.text
                tv.text = "${text} ${oldText}"
            }
        }

        builder.setView(view)
        builder.setCancelable(false)
        showLoadingDialog(builder)
        view.postDelayed({
            hideProgress()
        }, A_SLEEP_TIME_1_83__MS)
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
    showCustomDialog(builder)
    return view
}

fun AbstractDialog.showAlertPlatformByPoint(): View {
    val builder = AlertDialog.Builder(this.requireContext())
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.act_map__dialog_platform_clicked_dtl__alert_by_point, null)
    builder.setView(view)
    showCustomDialog(builder)
    return view
}

fun Fragment.warningCameraShow(title: String): View {
    val builder = AlertDialog.Builder(this.requireContext())
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.alert_warning_camera, null)
    view.title_tv.text = title
    builder.setView(view)
    builder.setCancelable(false)
    showCustomDialog(builder)
    return view
}

fun AppCompatActivity.showSuccessComplete(): View {
    val builder = AlertDialog.Builder(this)
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.alert_successful_complete, null)
    builder.setView(view)
    builder.setCancelable(false)
    showCustomDialog(builder)
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
    view.volume_tg.isChecked = true
    builder.setView(view)
    showCustomDialog(builder)
    return view
}


fun AppCompatActivity.showDialogFillKgoVolume(): View {
    val builder = AlertDialog.Builder(this)
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.dialog_fill_kgo, null)
    builder.setView(view)
    showCustomDialog(builder)
    return view
}


fun AppCompatActivity.warningDelete(title: String): View {
    val builder = AlertDialog.Builder(this)
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.alert_warning_delete, null)
    view.title_tv.text = title
    builder.setView(view)
    builder.setCancelable(false)
    showCustomDialog(builder)
    return view
}

//fun AppCompatActivity.showClickedPointDetail(point: PlatformEntity): View {
//    val customDialog: AlertDialog
//    val builder = AlertDialog.Builder(this)
//    val inflater = this.layoutInflater
//    val view = inflater.inflate(R.layout.alert_point_detail, null)
//    builder.setView(view)
//    customDialog = builder.create()
//    view.bottom_card.isVisible = point.status == StatusEnum.NEW
//    view.point_detail_address.text = "${point.address} \n ${point.srpId} ${point.containers.size} конт."
//    view.point_detail_close.setOnClickListener {
//        customDialog.dismiss()
//    }
//    view.point_detail_rv.adapter = ContainerDetailAdapter(point.containers)
//    customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//    customDialog.show()
//    return view
//}



fun AppCompatActivity.warningClearNavigator(title: String): View {
    val builder = AlertDialog.Builder(this)
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.alert_clear_navigator, null)
    builder.setView(view)
    builder.setCancelable(false)
    mCustomDialog = builder.create()
    view.title_tv.text = title
    view.dismiss_btn.setOnClickListener {
        mCustomDialog.dismiss()
    }
    mCustomDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    mCustomDialog.show()
    return view
}

fun AppCompatActivity.showDialogEarlyComplete(reasons: List<CancelWayReasonEntity>,
                                              workOrderId: Int, workOrderName: String): View {
    val builder = AlertDialog.Builder(this)
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.dialog_early_complete, null)
    val reasonsString = reasons.map { it.problem }
    view.reason_et.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, android.R.id.text1, reasonsString))
    view.reason_et.setOnClickListener {
        view.reason_et.showDropDown()
    }
    view.workorder_name.text = "Итоговые показатели рейса \n${workOrderId}($workOrderName)"
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

    //https://jira.smartro.ru/browse/SR-2625
    view.early_volume_tg.isChecked = true

    showCustomDialog(builder)
    return view
}

fun Fragment.warningDelete(title: String): View {
    val builder = AlertDialog.Builder(activity!!)
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.alert_warning_delete, null)
    view.title_tv.text = title
    builder.setView(view)
    builder.setCancelable(false)
    showCustomDialog(builder)
    return view
}

fun AppCompatActivity.hideDialog() {
    try {
        mCustomDialog.dismiss()
    } catch (e: Exception) {
        // TODO: 02.11.2021
        Log.e(TAG, "AppCompatActivity.hideDialog", e)
    }
}

fun AppCompatActivity.hideProgress() {
    try {
        if (loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
    } catch (e: Exception) {
        // TODO: 02.11.2021
        Log.e(TAG, "AppCompatActivity.loadingHide", e)
    }
}

fun Fragment.hideProgress() {
    try {
        loadingDialog.dismiss()
    } catch (e: Exception) {
        // TODO: 02.11.2021
        Log.e(TAG, "Fragment.loadingHide", e)
    }
}

fun Fragment.showingProgress() {
    try {
        val builder = AlertDialog.Builder(activity!!)
        val inflater = this.layoutInflater
        val view = inflater.inflate(R.layout.alert_loading, null)
        builder.setView(view)
        builder.setCancelable(false)
        showLoadingDialog(builder)
    } catch (e: Exception) {
        // TODO: 02.11.2021
        Log.e(TAG, "Fragment.loadingShow", e)
    }
}

fun Fragment.hideDialog(time: Long = 0) {
    try {
        println("rdos-A")
        CoroutineScope(Dispatchers.IO).launch {
            println("rdos-AB")
            delay(time)
            withContext(Dispatchers.Main) {
                mCustomDialog.dismiss()
            }
            println("rdos-ABC")
        }
        println("rdos-ABCD")


    } catch (e: Exception) {
        // TODO: 02.11.2021
        Log.e(TAG, "Fragment.hideDialog", e)
    }
}



