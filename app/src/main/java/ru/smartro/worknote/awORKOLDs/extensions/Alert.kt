package ru.smartro.worknote.awORKOLDs.extensions

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.alert_clear_navigator.view.dismiss_btn
import kotlinx.android.synthetic.main.alert_warning_camera.view.title_tv
import ru.smartro.worknote.AFragment
import ru.smartro.worknote.R
import ru.smartro.worknote.Snull
import ru.smartro.worknote.abs.AbstractDialog
import ru.smartro.worknote.log.AAct

private lateinit var loadingDialog: AlertDialog
private lateinit var mCustomDialog: AlertDialog
private val TAG = "Alert.kt"

fun showCustomDialog(builder: AlertDialog.Builder) {
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


//showDlgPickup!r_dos
fun AFragment.showDlgPickup(): View {
    val context = requireActivity() as AAct
    val builder = AlertDialog.Builder(context)
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
    hideProgress()
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

fun AAct.showAlertPlatformByPoint(): View {
    val builder = AlertDialog.Builder(this)
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.act_map__dialog_platform_clicked_dtl__alert_by_point, null)
    builder.setView(view)
    showCustomDialog(builder)
    return view
}

//            fun Fragment.warningCameraShow(title: String): View {
//                val builder = AlertDialog.Builder(this.requireContext())
//                val inflater = this.layoutInflater
//                val view = inflater.inflate(R.layout.alert_warning_camera, null)
//                view.title_tv.text = title
//                builder.setView(view)
//                builder.setCancelable(false)
//                showCustomDialog(builder)
//                return view
//            }
//
//            fun AppCompatActivity.showSuccessComplete(): View {
//                val builder = AlertDialog.Builder(this)
//                val inflater = this.layoutInflater
//                val view = inflater.inflate(R.layout.alert_successful_complete, null)
//                builder.setView(view)
//                builder.setCancelable(false)
//                showCustomDialog(builder)
//                return view
//            }
//
//
//            fun AppCompatActivity.showDialogFillKgoVolume(): View {
//                val builder = AlertDialog.Builder(this)
//                val inflater = this.layoutInflater
//                val view = inflater.inflate(R.layout.dialog_fill_kgo, null)
//                builder.setView(view)
//                showCustomDialog(builder)
//                return view
//            }
//
//
fun AFragment.showDialogFillKgoVolume(): View {
    val context = requireActivity() as AAct
    val builder = AlertDialog.Builder(context)
    val inflater = context.layoutInflater
    val view = inflater.inflate(R.layout.dialog_fill_kgo, null)
    builder.setView(view)
    showCustomDialog(builder)
    return view
}
//
//
//            fun AppCompatActivity.warningDelete(title: String): View {
//                val builder = AlertDialog.Builder(this)
//                val inflater = this.layoutInflater
//                val view = inflater.inflate(R.layout.alert_warning_delete, null)
//                view.title_tv.text = title
//                builder.setView(view)
//                builder.setCancelable(false)
//                showCustomDialog(builder)
//                return view
//            }

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

fun Fragment.hideDialog() {
    hideCustomDialog()
}
fun AppCompatActivity.hideDialog() {
    hideCustomDialog()
}

fun AbstractDialog.hideDialog() {
    hideCustomDialog()
}

    private fun hideCustomDialog() {
        try {
            mCustomDialog.dismiss()
        } catch (e: Exception) {
            // TODO: 02.11.2021
            Log.e(TAG, "AppCompatActivity.hideDialog", e)
        }
    }

fun AppCompatActivity.hideProgress() {
    try {
        Log.w(TAG, "hideProgress")
        if (loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
    } catch (e: Exception) {
        // TODO: 02.11.2021
        Log.e(TAG, "AppCompatActivity.loadingHide", e)
    }
}