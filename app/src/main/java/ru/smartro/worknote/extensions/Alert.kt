package ru.smartro.worknote.extensions

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.alert_accept_task.view.*
import kotlinx.coroutines.*
import ru.smartro.worknote.R

private lateinit var dialog: AlertDialog

fun AppCompatActivity.loadingShow() {
    try {
        val builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val view = inflater.inflate(R.layout.alert_loading, null)
        builder.setView(view)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
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
    dialog = builder.create()
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.show()
    return view
}

fun AppCompatActivity.warningEndTask(title: String): View {
    val builder = AlertDialog.Builder(this)
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.alert_warning_camera, null)
    view.title_tv.text = title
    builder.setView(view)
    builder.setCancelable(false)
    dialog = builder.create()
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.show()
    return view
}

fun AppCompatActivity.warningDelete(title: String): View {
    val builder = AlertDialog.Builder(this)
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.alert_warning_delete, null)
    view.title_tv.text = title
    builder.setView(view)
    builder.setCancelable(false)
    dialog = builder.create()
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.show()
    return view
}

fun Fragment.warningDelete(title: String): View {
    val builder = AlertDialog.Builder(activity!!)
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.alert_warning_delete, null)
    view.title_tv.text = title
    builder.setView(view)
    builder.setCancelable(false)
    dialog = builder.create()
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.show()
    return view
}

fun AppCompatActivity.loadingHide(time: Long = 0) {
    try {
        println()
        CoroutineScope(Dispatchers.IO).launch {
            println()
            delay(time)
            withContext(Dispatchers.Main) {
                dialog.dismiss()
            }
            println()
        }
        println()
    } catch (e: Exception) {
        println()
    }

}


fun Fragment.loadingShow() {
    try {
        val builder = AlertDialog.Builder(activity!!)
        val inflater = this.layoutInflater
        val view = inflater.inflate(R.layout.alert_loading, null)
        builder.setView(view)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    } catch (e: Exception) {
        println()
    }
}

fun Fragment.loadingHide(time: Long = 0) {
    try {
        println()
        CoroutineScope(Dispatchers.IO).launch {
            println()
            delay(time)
            withContext(Dispatchers.Main) {
                dialog.dismiss()
            }
            println()
        }
        println()


    } catch (e: Exception) {
        println()
    }
}



