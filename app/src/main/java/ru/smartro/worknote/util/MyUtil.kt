package ru.smartro.worknote.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ru.smartro.worknote.R
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.ui.auth.AuthActivity
import ru.smartro.worknote.ui.choose.owner_1.OrganisationActivity
import java.io.ByteArrayOutputStream
import java.io.File


object MyUtil {
    private const val PERMISSIONS_REQUEST_CODE = 10
    private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA)

    /** Convenience method used to check if all permissions required by this app are granted */
    fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    fun deleteFileFromStorage(filePath: String) {
        val file = File(filePath)
        if (file.delete())
            Log.d("Delete image after send", "DELETED $filePath ")
        else
            Log.d("Delete image after send", "NOT DELETED $filePath ")
    }

    fun createCardFolder(context: Context) {
        val folderPath = context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.path + "/images"
        val folder = File(folderPath)
        if (!folder.exists()) {
            val cardsDirectory = File(folderPath)
            cardsDirectory.mkdirs()
        }
    }

    fun timeStamp(): Long {
        return System.currentTimeMillis() / 1000L
    }

    fun askPermissionForCamera(context: Activity, code: Int) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(Manifest.permission.CAMERA),
                code
            )
        }
    }

    fun askPermissionForStorage(context: Activity, code: Int) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                code
            )
        }
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

    fun onMenuOptionClicked(context: Context, id: Int) {
        when (id) {
            R.id.change_organisation -> {
                context.startActivity(Intent(context, OrganisationActivity::class.java))
            }
            R.id.logout -> {
                logout(context)
            }
        }
    }

    fun logout(context: Context) {
        val intent = Intent(context, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
        AppPreferences.clear()
    }

    fun imageToBase64(filePath: String?): String {
        val bmp: Bitmap?
        val bos: ByteArrayOutputStream?
        val bt: ByteArray?
        var encodeString = ""
        val resizedBmp: Bitmap?
        try {
            bmp = BitmapFactory.decodeFile(filePath)
            bos = ByteArrayOutputStream()
            resizedBmp = Bitmap.createScaledBitmap(bmp, 360, 640, false)
            resizedBmp.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            bt = bos.toByteArray()
            encodeString = Base64.encodeToString(bt, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "data:image/png;base64,$encodeString"
    }

    private fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap? {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

        val resizedBitmap = Bitmap.createBitmap(
            bm, 0, 0, width, height, matrix, false
        )
        bm.recycle()
        return resizedBitmap
    }

}