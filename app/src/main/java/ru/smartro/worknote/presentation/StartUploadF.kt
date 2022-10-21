package ru.smartro.worknote.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.activityViewModels
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.ADFragment
import ru.smartro.worknote.abs.AFragment
import ru.smartro.worknote.andPOintD.SmartROllc
import ru.smartro.worknote.presentation.platform_serve.ServePlatformVM
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.ConfigName

class StartUploadF: ADFragment() {
    private val viewModel: ServePlatformVM by activityViewModels()

    fun onGetLayout(): Int {
        return R.layout.f_upload_start
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.f_upload_start, container, false)
    }

    override fun onInitLayoutView(sview: SmartROllc): Boolean {
        val acbStart = sview.findViewById<AppCompatButton>(R.id.acb_f_upload_start__start)
        acbStart.setOnClickListener {

            val isModeUnload = viewModel.database.getConfigBool(ConfigName.AAPP__IS_MODE__UNLOAD)

            if (isModeUnload) {
                toast("выключился режим Выгрузки")
            } else {
                toast("включился режим Выгрузки")
            }
            viewModel.database.setConfig(ConfigName.AAPP__IS_MODE__UNLOAD, !isModeUnload)

            navigateBack(R.id.MapPlatformsF)
        }
        return true
    }

    override fun onNewLiveData() {
//        TODO("Not yet implemented")
    }

    override fun onBackPressed() {
        navigateBack()
    }
}