package ru.smartro.worknote.awORKOLDs.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.util.Base64
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.yandex.mapkit.geometry.Point
import ru.smartro.worknote.App
import ru.smartro.worknote.Inull
import ru.smartro.worknote.R
import ru.smartro.worknote.log.AAct
import java.text.SimpleDateFormat
import java.util.*


object MyUtil {
    private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA)

    /** Convenience method used to check if all permissions required by this app are granted */
    fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    fun timeStampInSec(): Long {
        return System.currentTimeMillis() / 1000L
    }

    fun timeStampInMSec(): Long {
        return System.currentTimeMillis()
    }

    fun currentTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ", Locale.getDefault())
        return sdf.format(Date())
    }

    // TODO: 26.10.2021 !!! см. MapActivity.getBitmapFromVectorDrawable
    fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap? {
        var drawable = ContextCompat.getDrawable(context, drawableId) ?: return null

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = DrawableCompat.wrap(drawable).mutate()
        }

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        ) ?: return null
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    fun hasPermissions(context: Context, vararg permissions: Array<String>): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(context, it.toString()) == PackageManager.PERMISSION_GRANTED
        }

    fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view: View? = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    fun base64ToImage(encodedImage: String?): Bitmap {
        val decodedString: ByteArray =
            Base64.decode(encodedImage?.replace("data:image/png;base64,", ""), Base64.DEFAULT)

        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    fun getDeviceName(): String? {
        fun capitalize(s: String?): String? {
            if (s == null || s.isEmpty()) {
                return ""
            }
            val first = s[0]
            return if (Character.isUpperCase(first)) {
                s
            } else {
                Character.toUpperCase(first).toString() + s.substring(1)
            }
        }

        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            capitalize(model)
        } else {
            capitalize(manufacturer).toString() + " " + model
        }
    }

    fun calculateDistance(
        currentLocation: Point,
        finishLocation: Point
    ): Int {
        val userLocation = Location(LocationManager.GPS_PROVIDER)
        userLocation.latitude = currentLocation.latitude
        userLocation.longitude = currentLocation.longitude

        val checkPointLocation = Location(LocationManager.GPS_PROVIDER)
        checkPointLocation.latitude = finishLocation.latitude
        checkPointLocation.longitude = finishLocation.longitude
        return userLocation.distanceTo(checkPointLocation).toInt()
    }

    fun CharSequence?.isNotNull(): Boolean {
        return !this.isNullOrBlank()
    }

    fun Any?.toStr(s: String): String {
        return if (this == null) {
            ""
        } else {
            "$this $s"
        }
    }

    fun Any?.toStr() = this?.toString() ?: ""
    //        MyUtil(эх молодость)

}