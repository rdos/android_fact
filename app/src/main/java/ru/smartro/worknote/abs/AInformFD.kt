package ru.smartro.worknote.abs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R
import ru.smartro.worknote.andPOintD.SmartROllc
import ru.smartro.worknote.work.PlatformEntity

//AInForMatDF with LiveData 
//TODO:: AinformFragmentDialog
abstract class AInformFD: FragmentDialogA() {


    override fun onGetLayout(): Int {
        return R.layout.afd_inform
    }


    abstract fun onLiveData(entity: PlatformEntity)
    abstract fun onStyle(sview: SmartROllc)
    abstract fun onNextFragment(entity: PlatformEntity)
    

    final override fun onLiveData() {
        LOG.error("DONT USE!!!!!!!!!!")
    }

    private var mAcbGotoNext: AppCompatButton? = null
    private fun acbAccept(): AppCompatButton {
        if (mAcbGotoNext == null) {
            return AppCompatButton(this.requireContext())
        }
        return mAcbGotoNext!!
    }

    private var mActvContent: AppCompatTextView? = null
    private fun actvContent(): AppCompatTextView {
        if (mActvContent == null) {
            return AppCompatTextView(this.requireContext())
        }
        return mActvContent!!
    }


    final override fun onInitLayoutView(sview: SmartROllc): Boolean {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        mActvContent = sview.findViewById(R.id.actv__adf_inform__content)
        mAcbGotoNext = sview.findViewById(R.id.acb__adf_inform__gotonext)
        onStyle(sview)
        val contextText = onGetContentText()
        if (contextText == null) {
            LOG.error(" if (contextText == null) {")
            return false
        }
        actvContent().text = contextText
        val entity = onGetEntity()
        if (entity == null) {
            LOG.error("if (entity == null) {")
            return false
        }

        acbAccept().setOnClickListener {
            onNextFragment(entity)
        }

        onLiveData(entity)
        
        return true
    }

    

    final override fun onBackPressed() {
        navigateBack()
    }
    
    abstract fun onGetContentText(): String?
    abstract fun onGetNavId(): Int
    abstract fun onGetEntity(): PlatformEntity?

}