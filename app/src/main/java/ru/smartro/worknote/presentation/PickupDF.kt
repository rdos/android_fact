package ru.smartro.worknote.presentation

import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import ru.smartro.worknote.R
import ru.smartro.worknote.andPOintD.AInformFD
import ru.smartro.worknote.andPOintD.SmartROllc
import ru.smartro.worknote.presentation.work.PlatformEntity
import ru.smartro.worknote.presentation.work.ServePlatformVM

class PickupDF : AInformFD() {

    private val viewModel: ServePlatformVM by activityViewModels()

    override fun onGetNextText(): String {
        return "Сохранить"
    }

    override fun onGetBackText(): String? {
        return null
    }

    override fun onLiveData(entity: PlatformEntity) {

        getFragEntity().volumePickup?.let {
            tietAdditional().setText(it.toString())
        }
    }

    override fun onStyle(sview: SmartROllc, acbGotoBack: AppCompatButton) {
        tietAdditional().isVisible = true
        tilAdditional().isVisible = true
        tilAdditional().hint = onGetContentText()
        actvTitle().isVisible = false
    }

    override fun onNextFragment(entity: PlatformEntity) {
        val newVolume = tietAdditional().text.toString().toDoubleOrNull()
        if (newVolume == null) {
//            acsbVolumePickup.progress = 0
        } else {
            navigateNext(R.id.PhotoPickupMediaF, viewModel.getPlatformId(), newVolume.toString())
        }
    }

    override fun onBackFragment(entity: PlatformEntity) {

    }

    override fun onGetContentText(): String? {
        return "Объём подбора в м³"
    }

    override fun onGetNavId(): Int {
        return R.id.PickupDF
    }

    override fun onGetEntity(): PlatformEntity? {
        return viewModel.getPlatformEntity()
    }

    override fun onBindLayoutState(): Boolean {
        return false
    }
}