package ru.smartro.worknote.presentation

import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import ru.smartro.worknote.App
import ru.smartro.worknote.Inull
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.ARGUMENT_NAME___PARAM_ID
import ru.smartro.worknote.andPOintD.AInformFD
import ru.smartro.worknote.andPOintD.SmartROllc
import ru.smartro.worknote.presentation.work.ServePlatformVM
import ru.smartro.worknote.presentation.work.PlatformEntity

class PMapWarnF: AInformFD() {

    private val viewModel: ServePlatformVM by activityViewModels()



    override fun onGetEntity(): PlatformEntity {
        val platformId = requireArguments().getInt(ARGUMENT_NAME___PARAM_ID, Inull)
        val tbIbYO__item = viewModel.database.getPlatformEntity(platformId)
        return tbIbYO__item
    }

    override fun onBindLayoutState(): Boolean {
        return false
    }

    override fun onNextFragment(tbIbYO__item: PlatformEntity) {
        viewModel.setPlatformEntity(tbIbYO__item)
        navigateNext(R.id.PhotoBeforeMediaF, tbIbYO__item.platformId)
    }

    override fun onLiveData(tbIbYO__item: PlatformEntity) {
        if (App.getAppliCation().gps().isThisPoint(tbIbYO__item.coordLat, tbIbYO__item.coordLong)) {
            viewModel.setPlatformEntity(tbIbYO__item)
            dismissAllowingStateLoss()
            navigateNext(R.id.PhotoBeforeMediaF, tbIbYO__item.platformId)
        }
    }

    override fun onStyle(sview: SmartROllc) {
        setUseButtonStyleBackgroundRed(sview)
    }


    override fun onGetNavId(): Int {
        return R.id.WarnDF
    }

    override fun onGetContentText(): String {
        return getString(R.string.warning_gps_exception)
    }


    private fun setUseButtonStyleBackgroundRed(view: View) {
//        appCompatButton.alpha = 1f
        view.setBackgroundDrawable(ContextCompat.getDrawable(view.context, R.drawable.bg_button_red__usebutton))
    }


    private fun setDefButtonStyleBackground(view: View) {
//        appCompatButton.alpha = 1f
        view.setBackgroundDrawable(ContextCompat.getDrawable(view.context, R.drawable.bg_button_green__default))
    }

}