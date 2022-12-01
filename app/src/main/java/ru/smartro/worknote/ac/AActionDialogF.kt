package ru.smartro.worknote.ac

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.ADF
import ru.smartro.worknote.abs.DFAI

abstract class AActionDialogF: ADF(), DFAI {

    var actvTitle: AppCompatTextView? = null
    var actvContent: AppCompatTextView? = null
    var acbAccept: AppCompatButton? = null
    var acbDecline: AppCompatButton? = null

    override fun onGetLayout(): Int {
        return R.layout.df_action
    }

    override fun onInitLayoutView(sview: ru.smartro.worknote.ac.SmartROllc): Boolean {

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        actvTitle = sview.findViewById(R.id.actv__df_action__title)
        actvContent = sview.findViewById(R.id.actv__df_action__content)
        acbAccept = sview.findViewById(R.id.acb__df_action__accept)
        acbDecline = sview.findViewById(R.id.acb__df_action__decline)

        onLayoutInitialized()

        return true
    }

    override fun onLiveData() {

    }

    override fun onBindLayoutState(): Boolean{
        return false
    }

    override fun onBackPressed() {
        navigateBack()
    }
}