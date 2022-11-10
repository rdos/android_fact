package ru.smartro.worknote.presentation

import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import ru.smartro.worknote.App
import ru.smartro.worknote.Inull
import ru.smartro.worknote.R
import ru.smartro.worknote.andPOintD.ARGUMENT_NAME___PARAM_ID
import ru.smartro.worknote.andPOintD.SmartROllc
import ru.smartro.worknote.presentation.platform_serve.ServePlatformVM

class WarnDF: InfoDialogF() {
    private var mAcbAccept: AppCompatButton? = null
    private val vm: ServePlatformVM by activityViewModels()

    private fun acbAccept(): AppCompatButton {
        if (mAcbAccept == null) {
            return AppCompatButton(this.requireContext())
        }
        return mAcbAccept!!
    }

    private fun onGetNavId(): Int {
        return R.id.WarnDF
    }

    private fun setUseButtonStyleBackgroundRed(view: View) {
//        appCompatButton.alpha = 1f
        view.setBackgroundDrawable(ContextCompat.getDrawable(view.context, R.drawable.bg_button_red__usebutton))
    }


    private fun setDefButtonStyleBackground(view: View) {
//        appCompatButton.alpha = 1f
        view.setBackgroundDrawable(ContextCompat.getDrawable(view.context, R.drawable.bg_button_green__default))
    }


    override fun onInitLayoutView(sview: SmartROllc): Boolean {
        setUseButtonStyleBackgroundRed(sview)
        mAcbAccept = sview.findViewById<AppCompatButton>(R.id.acb__df_info__ok)


        return super.onInitLayoutView(sview)
    }

    override fun onNewLiveData() {
        val platformId = requireArguments().getInt(ARGUMENT_NAME___PARAM_ID, Inull)
        val TbIboy__item = vm.database.getPlatformEntity(platformId)
        if (App.getAppliCation().gps().isThisPoint(TbIboy__item.coordLat, TbIboy__item.coordLong)) {
            vm.setPlatformEntity(TbIboy__item)
            dismissAllowingStateLoss()
            navigateNext(R.id.PhotoBeforeMediaF, TbIboy__item.platformId)
        }

        acbAccept().setOnClickListener {
            vm.setPlatformEntity(TbIboy__item)
            navigateNext(R.id.PhotoBeforeMediaF, TbIboy__item.platformId)
        }
    }
    /**
     *
    fun AAct.showAlertPlatformByPoint(): View {
    val builder = AlertDialog.Builder(this)
    val inflater = this.layoutInflater
    val view = inflater.inflate(R.layout.act_map__dialog_platform_clicked_dtl__alert_by_point, null)
    builder.setView(view)
    showCustomDialog(builder)
    return view
    }


     */
}