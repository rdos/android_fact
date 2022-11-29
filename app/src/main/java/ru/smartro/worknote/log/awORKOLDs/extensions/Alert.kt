package ru.smartro.worknote.log.awORKOLDs.extensions

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import org.slf4j.LoggerFactory
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R
import ru.smartro.worknote.Snull
import ru.smartro.worknote.abs.AAct
import ru.smartro.worknote.abs.AF
import ru.smartro.worknote.abs.ADF

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

fun ADF.hideDialog() {
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

fun AF.showDialogAction(description: String, onAccept: () -> Unit, onDecline: (() -> Unit)? = null) {
    val builder = AlertDialog.Builder(getAct())
    val inflater = this.layoutInflater
    var actionDialog: AlertDialog? = null

    val view = inflater.inflate(R.layout.dialog_action, null)
    view.findViewById<AppCompatTextView>(R.id.actv__dialog_action__description).text = description
    view.findViewById<AppCompatButton>(R.id.acb__dialog_action__accept).setOnClickListener {
        onAccept()
        actionDialog?.dismiss()
    }
    view.findViewById<AppCompatButton>(R.id.acb__dialog_action__decline).setOnClickListener {
        onDecline?.invoke()
        actionDialog?.dismiss()
    }
    builder.setView(view)
    actionDialog = builder.create()
    actionDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    actionDialog.show()
}

//todo:!r_dos!Ж)
fun AAct.showDlgLogout(): View {
    val builder = AlertDialog.Builder(this)
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.act_xchecklist__dialog_logout, null)
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


//fun FragmentA.warningClearNavigator(title: String): View {
//    val builder = AlertDialog.Builder(getAct())
//    val inflater = this.layoutInflater
//    val view = inflater.inflate(R.layout.alert_clear_navigator, null)
//    builder.setView(view)
//    builder.setCancelable(false)
//    mCustomDialog = builder.create()
//    view.findViewById<TextView>(R.id.title_tv).text = title
//    view.findViewById<Button>(R.id.dismiss_btn).setOnClickListener {
//        mCustomDialog?.dismiss()
//    }
//    mCustomDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//    mCustomDialog?.show()
//    return view
//}

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

