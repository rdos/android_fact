package ru.smartro.worknote.presentation

import androidx.appcompat.widget.AppCompatButton
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.AFragment
import ru.smartro.worknote.andPOintD.ANOFragment
import ru.smartro.worknote.andPOintD.SmartROllc
import ru.smartro.worknote.toast

class StartUploadF: AFragment() {
    
    override fun onGetLayout(): Int {
        return R.layout.f_upload_start
    }

    override fun onInitLayoutView(sview: SmartROllc): Boolean {
        val acbStart = sview.findViewById<AppCompatButton>(R.id.acb_f_upload_start__start)
        acbStart.setOnClickListener {
            toast("включить невидимку")
            navigateBack(R.id.MapPlatformsF)
        }
        return super.onInitLayoutView(sview)
    }
}