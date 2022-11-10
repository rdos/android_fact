package ru.smartro.worknote.presentation

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.ADFragment
import ru.smartro.worknote.andPOintD.ARGUMENT_NAME___PARAM_NAME
import ru.smartro.worknote.andPOintD.SmartROllc

open class InfoDialogF: ADFragment() {

//    private val vm: ServePlatformVM by activityViewModels()

    override fun onGetLayout(): Int {
        return R.layout.df_info
    }


    override fun onInitLayoutView(sview: SmartROllc): Boolean {

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        val acbAccept = sview.findViewById<AppCompatButton>(R.id.acb__df_info__ok)
        val actvContent = sview.findViewById<AppCompatTextView>(R.id.actv__df_info__content)

        acbAccept.setOnClickListener {
            navigate(R.id.MapPlatformsF)
        }

        actvContent.text = requireArguments().getString(ARGUMENT_NAME___PARAM_NAME)
        onNewLiveData()
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