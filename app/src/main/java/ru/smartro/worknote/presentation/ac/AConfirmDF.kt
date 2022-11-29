package ru.smartro.worknote.presentation.ac

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R
import ru.smartro.worknote.andPOintD.SmartROllc
import ru.smartro.worknote.log.work.PlatformEntity
import ru.smartro.worknote.abs.ADF

//AInForMatDF with LiveData 
//TODO:: AinformFragmentDialog
// когда одна кнопка это либо вперёд либо остаться
abstract class AConfirmDF: ADF() {

    override fun onGetLayout(): Int {
        return R.layout.afd_inform
    }

    abstract fun onGetNextText() : String?
    abstract fun onGetBackText() : String?

    abstract fun onLiveData(entity: PlatformEntity)
    abstract fun onStyle(sview: SmartROllc, acbGotoBack: AppCompatButton)
    abstract fun onNextFragment(entity: PlatformEntity)
    abstract fun onBackFragment(entity: PlatformEntity)


    final override fun onLiveData() {
        LOG.error("DONT USE!!!!!!!!!!")
    }

    private var mAcbGotoNext: AppCompatButton? = null
    private fun acbGotoNext(): AppCompatButton {
        if (mAcbGotoNext == null) {
            return AppCompatButton(this.requireContext())
        }
        return mAcbGotoNext!!
    }

    private var mActvTitle: AppCompatTextView? = null
    fun actvTitle(): AppCompatTextView {
        if (mActvTitle == null) {
            return AppCompatTextView(this.requireContext())
        }
        return mActvTitle!!
    }

    private var mActvContent: AppCompatTextView? = null
    private fun actvContent(): AppCompatTextView {
        if (mActvContent == null) {
            return AppCompatTextView(this.requireContext())
        }
        return mActvContent!!
    }

    private var mAcbGotoBack: AppCompatButton? = null
    private fun acbGotoBack(): AppCompatButton {
        if (mAcbGotoBack == null) {
            return AppCompatButton(this.requireContext())
        }
        return mAcbGotoBack!!
    }

    private var mTietAdditional: TextInputEditText? = null
    fun tietAdditional(): TextInputEditText {
        if (mTietAdditional == null) {
            return TextInputEditText(this.requireContext())
        }
        return mTietAdditional!!
    }

    private var mTilAdditional: TextInputLayout? = null
    fun tilAdditional(): TextInputLayout {
        if (mTilAdditional == null) {
            return TextInputLayout(this.requireContext())
        }
        return mTilAdditional!!
    }


    final override fun onInitLayoutView(sview: SmartROllc): Boolean {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        mActvContent = sview.findViewById(R.id.actv__adf_inform__content)
        mActvTitle = sview.findViewById(R.id.actv__adf_inform__title)

        mAcbGotoNext = sview.findViewById(R.id.acb__adf_inform__gotonext)
        mAcbGotoBack = sview.findViewById(R.id.acb__adf_inform__gotoback)
        mTietAdditional = sview.findViewById(R.id.tiet_alert_additional)
        mTilAdditional = sview.findViewById(R.id.til_alert_additional)

//        todo:R_dos!Up!!

        val entity = getFragEntity()
        onLiveData(entity)

        onStyle(sview, acbGotoBack())
        val contextText = onGetContentText()
        if (contextText == null) {
            LOG.error(" if (contextText == null) {")
            return false
        }
        actvContent().text = contextText

        val nextText = onGetNextText()
        if(nextText != null)
            acbGotoNext().text = nextText

        acbGotoNext().setOnClickListener {
            onNextFragment(entity)
        }

        val backText = onGetBackText()
        if(backText == null) {
            acbGotoBack().visibility = View.GONE
        } else {
            acbGotoBack().text = backText

        }

        acbGotoBack().setOnClickListener {
            onBackFragment(entity)
        }

        return true
    }

    fun getFragEntity(): PlatformEntity {
        var result =  onGetEntity()
        if (result == null) {
            LOG.error("if (result == null) {")
            result = PlatformEntity.createEmpty()
        }
        return result
    }


    final override fun onBackPressed() {
        navigateBack()
    }
    
    abstract fun onGetContentText(): String?
    abstract fun onGetNavId(): Int
    abstract fun onGetEntity(): PlatformEntity?

}