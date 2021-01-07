package ru.smartro.worknote.extensions

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun Fragment.toast(text: String? = "") {
    try {
        Toast.makeText(this.context, text, Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
    }

}

fun AppCompatActivity.toast(text: String? = "") {
    try {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
    }

}