package ru.smartro.worknote.work.abs

import android.util.Log
import androidx.fragment.app.Fragment
import io.sentry.Sentry
import ru.smartro.worknote.App

abstract class AFragment : Fragment(){
    protected var TAG : String = "--Aaa${this::class.simpleName}"

    protected fun paramS() : App.SharedPref {
        return App.getAppParaMS()
    }

    protected fun logSentry(text: String) {
        Sentry.addBreadcrumb("${TAG} : $text")
        Log.i(TAG, "onCreate")
    }
                                    //    companion object {
                                    //        private const val TAG = "CameraXBasic"
                                    //        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
                                    //        private const val PHOTO_EXTENSION = ".jpg"
                                    //        private const val RATIO_4_3_VALUE = 4.0 / 3.0
                                    //        private const val RATIO_16_9_VALUE = 16.0 / 9.0
                                    //
                                    //        private fun createFile(baseFolder: File, format: String, extension: String) =
                                    //            File(baseFolder, SimpleDateFormat(format, Locale.US).format(System.currentTimeMillis()) + extension)
                                    //    }
}
