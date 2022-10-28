package ru.smartro.worknote.presentation

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.activityViewModels
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.ADFragment
import ru.smartro.worknote.andPOintD.ARGUMENT_NAME___PARAM_NAME
import ru.smartro.worknote.andPOintD.SmartROllc
import ru.smartro.worknote.presentation.platform_serve.ServePlatformVM

class InfoDialogF: ADFragment() {

//    private val vm: ServePlatformVM by activityViewModels()

    override fun onGetLayout(): Int {
        return R.layout.d_info
    }

    override fun onInitLayoutView(sview: SmartROllc): Boolean {

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        val acbAccept = sview.findViewById<AppCompatButton>(R.id.acb__d_info__accept)
        val actvContent = sview.findViewById<AppCompatTextView>(R.id.actv__d_info__content)

        acbAccept.setOnClickListener {
            navigateBack(R.id.MapPlatformsF)
        }

        actvContent.text = requireArguments().getString(ARGUMENT_NAME___PARAM_NAME)

        return true
    }

    override fun onNewLiveData() {

    }

    override fun onBindLayoutState(): Boolean{
        return false
    }

    override fun onBackPressed() {
        navigateBack()
    }
}