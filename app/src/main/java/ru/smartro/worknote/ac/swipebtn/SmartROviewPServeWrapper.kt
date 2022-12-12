package ru.smartro.worknote.ac.swipebtn

import android.content.Context
import android.graphics.drawable.TransitionDrawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import ru.smartro.worknote.R
import ru.smartro.worknote.ac.SmartROllc
import ru.smartro.worknote.ac.SmartROsc
import ru.smartro.worknote.log.todo.PlatformEntity


//todo: ::: https://github.com/ebanx/swipe-button.git
class SmartROviewPServeWrapper @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
) : SmartROllc(context, attrs, defStyleAttrs) {

    private var mOnCompleteServe: (() -> Unit)? = null
    private var mOnSwitchMode: (() -> Unit)? = null

    private var parent: ConstraintLayout? = null
    private var clHeader: ConstraintLayout? = null
    private var actvTitle: AppCompatTextView? = null
    private var actvScreenModeLabel: AppCompatTextView? = null
    private var actvCleanupLabel: AppCompatTextView? = null

    private var switch: SmartROsc? = null

    private var actvAddress: AppCompatTextView? = null
    private var actvPlatformSrpId: AppCompatTextView? = null

    private var srollcCompleteButton: SmartROllc? = null
    private var acivCleanupIcon: AppCompatImageView? = null

    init {
        inflate(getContext(), R.layout.sview_pserve_wrapper, this)

        clHeader = findViewById(R.id.cl__sview_f_pserve_wrapper__header_wrapper)
        actvTitle = findViewById(R.id.actv__sview_pserve_wrapper__title)
        actvScreenModeLabel = findViewById(R.id.actv__sview_pserve_wrapper__screen_mode_label)
        actvCleanupLabel = findViewById(R.id.actv__sview_pserve_wrapper__cleanup_label)

        switch = findViewById(R.id.srosc__sview_pserve_wrapper__screen_mode)

        actvAddress = findViewById(R.id.actv__sview_pserve_wrapper__address)

        actvPlatformSrpId = findViewById(R.id.actv__sview_pserve_wrapper__srpid)

        parent = findViewById(R.id.cl__sview_pserve_wrapper__parent)

        srollcCompleteButton = findViewById(R.id.srollc__sview_pserve_wrapper__complete_button)
        acivCleanupIcon = findViewById(R.id.aciv__sview_pserve_wrapper__cleanup_icon)
    }

    fun setOnCompleteServeListener(onCompleteServe: () -> Unit) {
        mOnCompleteServe = onCompleteServe
    }

    fun setOnSwitchMode(onSwitchMode: () -> Unit) {
        mOnSwitchMode = onSwitchMode
    }

    fun setScreenLabel(text: String) {
//        actvScreenLabel?.text = "Списком"
        actvScreenModeLabel?.text = text
    }

    fun setPlatformEntity(platformEntity: PlatformEntity, activity: FragmentActivity) {
        // IN WRAPPER
        val srpId = platformEntity.srpId
        val containersSize = platformEntity.containerS.size

        actvPlatformSrpId?.text = "№${srpId} / ${containersSize} конт."

        switch?.setOnCheckedChangeListener { _, _ ->
            mOnSwitchMode?.invoke()
        }

        srollcCompleteButton?.setOnClickListener {
            mOnCompleteServe?.invoke()
        }

        if(platformEntity.needCleanup) {
            if(clHeader != null && srollcCompleteButton != null) {
                val buttonCompleteTransitionDrawable = srollcCompleteButton?.background as TransitionDrawable
                buttonCompleteTransitionDrawable.startTransition(500)
            }
        } else {
            acivCleanupIcon?.visibility = View.GONE
        }

        actvAddress?.text = platformEntity.address
        if (platformEntity.containerS.size >= 7 ) {
            actvAddress?.apply {
                setOnClickListener { view ->
                    maxLines = if (maxLines < 3) {
                        3
                    } else {
                        1
                    }
                }
            }
        } else {
            actvAddress?.maxLines = 3
        }

        val platformServeMode = platformEntity.getServeMode()
        if(platformServeMode == PlatformEntity.Companion.ServeMode.PServeF){
            switch?.visibility = View.GONE
        } else {
            switch?.visibility = View.VISIBLE
        }

    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        parent?.addView(child, index, params) ?: super.addView(child, index, params)
    }

    fun setSwitchVisibility(isVisible: Boolean) {
        switch?.isVisible = isVisible
    }

    fun setSwitchChecked(isChecked: Boolean) {
        switch?.isChecked = isChecked
    }
}