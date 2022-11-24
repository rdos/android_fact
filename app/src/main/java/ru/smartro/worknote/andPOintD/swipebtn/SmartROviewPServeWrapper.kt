package ru.smartro.worknote.andPOintD.swipebtn

import android.content.Context
import android.graphics.drawable.TransitionDrawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R
import ru.smartro.worknote.andPOintD.SmartROllc
import ru.smartro.worknote.andPOintD.SmartROsc
import ru.smartro.worknote.presentation.work.PlatformEntity

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
            LOG.debug("TEST:::: AUUUUU")
            mOnCompleteServe?.invoke()
        }

        if(platformEntity.needCleanup) {
            LOG.debug("TEST:::: CHANGE COLORS")
            if(clHeader != null && srollcCompleteButton != null) {
                val transitionDrawableHeader = clHeader?.background as TransitionDrawable
                val transitionDrawableComplete = srollcCompleteButton?.background as TransitionDrawable
                transitionDrawableHeader.startTransition(500)
                transitionDrawableComplete.startTransition(500)
                activity.window.statusBarColor = ContextCompat.getColor(activity, R.color.dark_orange)
            }
        } else {
            LOG.debug("TEST:::: NO CHANGE COLORS")
            acivCleanupIcon?.visibility = View.GONE
        }

        val platformServeMode = platformEntity.getServeMode()

        if(platformServeMode == PlatformEntity.Companion.ServeMode.PServeF){
            switch?.visibility = View.GONE
        } else {
            switch?.visibility = View.VISIBLE
        }

//        CHANGEMODE()
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        parent?.addView(child, index, params) ?: super.addView(child, index, params)
    }
}