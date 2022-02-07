package ru.smartro.worknote.base

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.sentry.Sentry
import ru.smartro.worknote.Inull
import ru.smartro.worknote.Snull
import java.lang.Exception

//        try {
//            throw Exception("This is a devel.")
//        } catch (e: Exception) {
//            Sentry.captureException(e)
//        }
//todo:r_dos::_know1=знаешь точно, что ОСТАВИТЬ И НУЖНО ВСЕМУ коГду(а))
//       ::_know0=а это не ОСТАВЛЯТЬ
abstract class AbstractAct : AppCompatActivity() {

    protected var TAG : String = "${this::class.simpleName}"

    protected fun logSentry(text: String) {
        Sentry.addBreadcrumb("${TAG} : $text")
        Log.i(TAG + "Sent", text)
    }
    protected fun logSentry(data: Int) {
        logSentry(data.toString())
    }

    protected fun setAntiErrorClick(itemView: View) {
        itemView.isEnabled = false
        itemView.postDelayed({
            try {
                itemView.isEnabled = true
            } catch (ex: Exception) {
                // TODO: r_dos 
            }
        }, 333)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
    }


    protected val PUT_EXTRA_PARAM_ID: String = "PUT_EXTRA_PARAM_ID"
    fun getPutExtraParam_ID(): Int {
        val res = intent.getIntExtra(PUT_EXTRA_PARAM_ID, Inull)
        if (res == Inull) {
            logSentry("res == Inull")
        }
        return res
    }

    protected val PUT_EXTRA_PARAM_NAME: String = "PUT_EXTRA_PARAM_TEXT"
    fun getPutExtraParam_NAME(): String {
        var res = intent.getStringExtra(PUT_EXTRA_PARAM_NAME)
        if (res.isNullOrEmpty()) {
            logSentry("resres.isNullOrEmpty()")
            res = Snull
        }
        return res
    }


    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")

    }


}

//TODO: find next шагай)_/gotoAdd = Add -> create->ПОТОМ->Save->ПОТОМ Show
/** :r_dos::_1n0w0=переделать, удалить=не то 100% внимание /
:yo-11now0 пудов, но форма странная.
        */