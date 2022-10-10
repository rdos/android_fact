package ru.smartro.worknote.awORKOLDs.extensions

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import org.slf4j.LoggerFactory
import ru.smartro.worknote.App
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R
import ru.smartro.worknote.Snull
import ru.smartro.worknote.abs.AAct
import ru.smartro.worknote.andPOintD.ANOFragment
import ru.smartro.worknote.abs.AbstractDialog

private var loadingDialog: AlertDialog? = null
private var mCustomDialog: AlertDialog? = null
private val TAG = "Alert.kt"
private val log = LoggerFactory.getLogger("ALERT")


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
        mCustomDialog?.dismiss()
    } catch (ex: Exception) {
        // TODO: 02.11.2021
        log.error("hideCustomDialog", ex)
    }
}

fun showCustomDialog(builder: AlertDialog.Builder) {
    log.info( "before")
    try {
        mCustomDialog = builder.create()
        mCustomDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mCustomDialog?.show()
    }  catch (e: Exception) {
        // TODO: 02.11.2021
        log.error("showCustomDialog", e)
    }
    log.info("after")
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
fun AAct.showDlgLogout(): View {
    val builder = AlertDialog.Builder(this)
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.act_xchecklist__dialog_logout, null)
    builder.setView(view)
    showCustomDialog(builder)
    return view
}

fun AAct.showDlgWarning(warningType: WarningType): View {
    val builder = AlertDialog.Builder(this)
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.dialog_warning, null)
    var warningDialog: AlertDialog? = null
    view.findViewById<AppCompatTextView>(R.id.actv__dialog_warning__description).text = when(warningType) {
        WarningType.AIRPLANE_MODE -> getString(R.string.warning_airplane_mode)
        WarningType.CONNECTION_LOST -> getString(R.string.warning_connection_lost)
        WarningType.GPS_OFF -> getString(R.string.warning_gps_off)
        WarningType.CAR_LOCKED -> getString(R.string.car_locked)
        WarningType.FUEL_LOCKED -> getString(R.string.fuel_locked)
        WarningType.PHOTO_LOCKED -> getString(R.string.photo_locked)
    }
    view.findViewById<AppCompatButton>(R.id.acb__dialog_warning__ok)?.setOnClickListener {
        warningDialog?.dismiss()
    }
    builder.setView(view)
    warningDialog = builder.create()
    warningDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    warningDialog.show()
    return view
}

enum class WarningType(val type: Int) {
    AIRPLANE_MODE(413),
    CONNECTION_LOST(609),
    GPS_OFF(855)
}
=========
//showDlgPickup!r_dos
fun AAct.showDlgLogout(): View {
    val builder = AlertDialog.Builder(this)
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.act_xchecklist__dialog_logout, null)
    builder.setView(view)
    showCustomDialog(builder)
    return view
}
>>>>>>>>> Temporary merge branch 2
enum class WarningType(val type: Int) {
    AIRPLANE_MODE(413),
    CONNECTION_LOST(609),
    GPS_OFF(855)
}
enum class WarningType {
    AIRPLANE_MODE,
    CONNECTION_LOST,
    GPS_OFF,
    CAR_LOCKED,
    FUEL_LOCKED,
    PHOTO_LOCKED,
}

//showDlgPickup!r_dos
fun ANOFragment.showDlgPickup(): View {
    val context = requireActivity() as AAct
    val builder = AlertDialog.Builder(context)
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.act_platformserve__pickup__alert_dialog, null)
    builder.setView(view)
    showCustomDialog(builder)
    return view
}


private fun showLoadingDialog(builder: AlertDialog.Builder) {
    log.info( "before")
    try {
        loadingDialog = builder.create()
        loadingDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadingDialog?.show()
    }  catch (e: Exception) {
        // TODO: 02.11.2021
        log.error("showLoadingDialog", e)
    }
    log.info("after")
}


fun AppCompatActivity.hideProgress() {
    try {
        LOG.warn( "hideProgress")
        if (loadingDialog?.isShowing == true) {
            loadingDialog?.dismiss()
        }
    } catch (ex: Exception) {
        // TODO: 02.11.2021
        LOG.error("hideProgress", ex)
    }
}

fun AppCompatActivity.showingProgress(text: String?=null, isEmptyOldText: Boolean=false) {
    hideProgress()
    try {
        val builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val view = inflater.inflate(R.layout.alert_loading, null)
        text?.let {
            if (text != Snull) {
                val tv = view.findViewById<TextView>(R.id.tv_alert_loading)
                var oldText = tv.text
                if (isEmptyOldText) {
                    oldText = ""
                }

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



fun ANOFragment.showAlertPlatformByPoint(): View {
    val builder = AlertDialog.Builder(getAct())
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.act_map__dialog_platform_clicked_dtl__alert_by_point, null)
    builder.setView(view)
    showCustomDialog(builder)
    return view
}

fun ANOFragment.showDialogFillKgoVolume(): View {
    val context = requireActivity() as AAct
    val builder = AlertDialog.Builder(context)
    val inflater = context.layoutInflater
    val view = inflater.inflate(R.layout.dialog_fill_kgo, null)
    builder.setView(view)
    showCustomDialog(builder)
    return view
}



fun ANOFragment.warningClearNavigator(title: String): View {
    val builder = AlertDialog.Builder(getAct())
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.alert_clear_navigator, null)
    builder.setView(view)
    builder.setCancelable(false)
    mCustomDialog = builder.create()
    view.findViewById<TextView>(R.id.title_tv).text = title
    view.findViewById<Button>(R.id.dismiss_btn).setOnClickListener {
        mCustomDialog?.dismiss()
    }
    mCustomDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    mCustomDialog?.show()
    return view
}

//            fun Fragment.warningCameraShow(title: String): View {
//                val builder = AlertDialog.Builder(this.requireContext())
//                val inflater = this.layoutInflater
//                val view = inflater.inflate(R.layout.alert_warning_camera, null)
//                view.title_tv.text = title
//                builder.setView(view)
//                builder.setCancelable(false)
//                showCustomDiaLOG.debug(builder)
//                return view
//            }
//
//            fun AppCompatActivity.showSuccessComplete(): View {
//                val builder = AlertDialog.Builder(this)
//                val inflater = this.layoutInflater
//                val view = inflater.inflate(R.layout.alert_successful_complete, null)
//                builder.setView(view)
//                builder.setCancelable(false)
//                showCustomDiaLOG.debug(builder)
//                return view
//            }
//
//
//            fun AppCompatActivity.showDialogFillKgoVolume(): View {
//                val builder = AlertDialog.Builder(this)
//                val inflater = this.layoutInflater
//                val view = inflater.inflate(R.layout.dialog_fill_kgo, null)
//                builder.setView(view)
//                showCustomDiaLOG.debug(builder)
//                return view
//            }
//
//
//
//
//            fun AppCompatActivity.warningDelete(title: String): View {
//                val builder = AlertDialog.Builder(this)
//                val inflater = this.layoutInflater
//                val view = inflater.inflate(R.layout.alert_warning_delete, null)
//                view.title_tv.text = title
//                builder.setView(view)
//                builder.setCancelable(false)
//                showCustomDiaLOG.debug(builder)
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
//        customDiaLoG.dismiss()
//    }
//    view.point_detail_rv.adapter = ContainerDetailAdapter(point.containers)
//    customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//    customDialog.show()
//    return view
//}
