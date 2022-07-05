package ru.smartro.worknote

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavArgument
import io.sentry.Sentry
import ru.smartro.worknote.App
import ru.smartro.worknote.Inull
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.ActAbstract
import ru.smartro.worknote.awORKOLDs.extensions.hideProgress
import ru.smartro.worknote.awORKOLDs.extensions.showingProgress
import ru.smartro.worknote.awORKOLDs.service.network.NetworkRepository
import ru.smartro.worknote.log.AAct

const val ARGUMENT_NAME___PARAM_ID = "ARGUMENT_NAME__PUT_EXTRA_PARAM_ID"
const val ARGUMENT_NAME___PARAM_NAME = "ARGUMENT_NAME__PUT_EXTRA_PARAM_NAME"
abstract class AFragment : Fragment(){
    protected var TAG : String = "--Aaa${this::class.simpleName}"

    protected fun paramS() = App.getAppParaMS()
    protected fun getAct() = requireActivity() as AAct
    protected fun showingProgress(){
        //todo:ActAbstract
        (requireActivity() as AAct).showingProgress()
    }

    protected fun hideProgress(){
        (requireActivity() as AAct).hideProgress()
    }

    protected fun logSentry(text: String) {
        Sentry.addBreadcrumb("${TAG} : $text")
        // TODO: )))
        Log.i(TAG, "onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        try {
//          это провал!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//        }
        val view = inflater.inflate(onGetLayout(), container, false)
        return view
    }

    abstract fun onGetLayout(): Int

    fun addArgument(argumentId: Int, containerUuid: String? = null) {
        val bundle = Bundle(2)
        bundle.putInt(ARGUMENT_NAME___PARAM_ID, argumentId)
        // TODO: 10.12.2021 let на всякий П???
        containerUuid?.let {
            bundle.putString(ARGUMENT_NAME___PARAM_ID, containerUuid)
        }
        this.arguments = bundle
    }

    protected fun getArgumentID(): Int {
        val result = requireArguments().getInt(ARGUMENT_NAME___PARAM_NAME, Inull)
        return result
    }

    protected fun getArgumentText(): String? {
        val result = requireArguments().getString(ARGUMENT_NAME___PARAM_NAME)
        return result
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        onCreate()
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
