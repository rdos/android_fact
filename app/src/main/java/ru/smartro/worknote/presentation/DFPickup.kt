package ru.smartro.worknote.presentation

import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import ru.smartro.worknote.R
import ru.smartro.worknote.presentation.ac.AConfirmDF
import ru.smartro.worknote.ac.SmartROllc
import ru.smartro.worknote.log.todo.PlatformEntity

class DFPickup : AConfirmDF() {

    companion object {
        const val NAV_ID = R.id.DFPickup
    }

    private val viewModel: VMPserve by activityViewModels()

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
            navigateNext(FPhotoPickupMedia.NAV_ID, viewModel.getPlatformId(), newVolume.toString())
        }
    }

    override fun onBackFragment(entity: PlatformEntity) {

    }

    override fun onGetContentText(): String? {
        return "Объём подбора в м³"
    }

    override fun onGetEntity(): PlatformEntity? {
        return viewModel.getPlatformEntity()
    }

    override fun onBindLayoutState(): Boolean {
        return false
    }
}