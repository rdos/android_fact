package ru.smartro.worknote.presentation

import android.content.DialogInterface
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.ADF
import ru.smartro.worknote.ac.SmartROacb
import ru.smartro.worknote.ac.SmartROllc
import ru.smartro.worknote.ac.swipebtn.SmartROviewSwipeButton
import ru.smartro.worknote.toast
import ru.smartro.worknote.log.todo.ConfigName
import ru.smartro.worknote.toStr
import ru.smartro.worknote.tryCatch

//todo: смотри прикол, VT !!!UnloadInfo++ploadTicket
class DFModeUnloadTicket: ADF() {

    companion object {
        const val NAV_ID = R.id.DFModeUnloadTicket
    }

    private var acetTalonValue: AppCompatEditText? = null
    private var acetWeightAfter: AppCompatEditText? = null
    private var acetWeightBefore: AppCompatEditText? = null

    //    https://en.wikipedia.org/wiki/Virtual_machine :))))))
    private val vm: VMPserve by activityViewModels()

    override fun onGetLayout(): Int {
        return R.layout.f_unload_ticket
    }

    override fun onInitLayoutView(sview: SmartROllc): Boolean {
        val platformUnloadEntity = vm.getPlatformEntity().unloadEntity

        val acbPhotoBefore = sview.findViewById<SmartROacb>(R.id.acb__f_unload_ticket__photo_before)
        acbPhotoBefore.setOnClickListener {
            navigateNext(DFModeUnloadPhotoBeforeMedia.NAV_ID)
        }
        val actvPhotoBeforeCount = sview.findViewById<AppCompatTextView>(R.id.actv__f_unload_ticket__photo_before_count)
        actvPhotoBeforeCount.setText(platformUnloadEntity?.beforeMedia?.size.toStr())


        val acbPhotoAfter = sview.findViewById<SmartROacb>(R.id.acb__f_unload_ticket__photo_after)
        acbPhotoAfter.setOnClickListener {
            navigateNext(FModeUnloadPhotoAfterMedia.NAV_ID)
        }
        val actvPhotoAfterCount = sview.findViewById<AppCompatTextView>(R.id.actv__f_unload_ticket__photo_after_count)
        actvPhotoAfterCount.setText(platformUnloadEntity?.afterMedia?.size.toStr())




        acetWeightBefore = sview.findViewById(R.id.acet__f_unload_ticket__value_before)
        if (platformUnloadEntity?.beforeValue != null) {
            acetWeightBefore?.setText(platformUnloadEntity?.beforeValue.toString())
        }

        acetWeightAfter = sview.findViewById(R.id.acet__f_unload_ticket__value_after)
        if (platformUnloadEntity?.afterValue != null) {
            acetWeightAfter?.setText(platformUnloadEntity?.afterValue.toString())
        }
        acetTalonValue = sview.findViewById(R.id.acet__f_unload_ticket__value_talon)

        if (platformUnloadEntity?.ticketValue != null) {
            acetTalonValue?.setText(platformUnloadEntity?.ticketValue.toString())
        }

        val acbFinish: SmartROviewSwipeButton? = sview.findViewById(R.id.acb_f_unload_ticket__finish)
        acbFinish?.onSwipe = {
            val weightBefore = acetWeightBefore?.text.toString().toFloatOrNull()?:0f
            val weightAfter = acetWeightAfter?.text.toString().toFloatOrNull()?:0f
            if (weightAfter > weightBefore) {
                toast("Показания с весов \"После\" не должны превышать \"До\"")
            } else {
                if (isNotAllFieldFill()) {
                    toast("Заполните все поля в карточке")
                } else {
                    vm.database.setConfig(ConfigName.AAPP__IS_MODE__UNLOAD, false)
                    tryCatch {
                        val navHostFragment = (getAct().supportFragmentManager.findFragmentById(R.id.fcv_container) as NavHostFragment)
                        val fragmentPMap = (navHostFragment.childFragmentManager.fragments[0] as FPMap)
                        fragmentPMap.clearNavigator()
                        fragmentPMap.toggleUnloadButton(false)
                    }
                    navigate(FPMap.NAV_ID)
                    toast("выключился режим Выгрузки")
                }
            }
        }

        return true
    }

    private fun isNotAllFieldFill(): Boolean {
        // TODO: бред!!!
        val result = true
        val platformUnloadEntity = vm.getPlatformEntity().unloadEntity
        val photoBeforeSize = platformUnloadEntity!!.beforeMedia.size
        if (photoBeforeSize <= 0) {
            return result
        }
        val weightBefore = acetWeightBefore?.text.toString().toFloatOrNull()
        if (weightBefore == null) {
            return result
        }
        val photoAfterSize = platformUnloadEntity!!.afterMedia.size
        if (photoAfterSize <= 0) {
            return result
        }
        val weightAfter = acetWeightAfter?.text.toString().toFloatOrNull()
        if (weightAfter == null) {
            return result
        }
        val talonValue = acetTalonValue?.text.toString().toFloatOrNull()
        if (talonValue == null) {
            return result
        }
        return false
    }

    override fun onLiveData() {
//        TODO("Not yet implemented")
    }

    override fun onBindLayoutState(): Boolean {
        return false
    }

    override fun onBackPressed() {
        navigateBack()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val platformUnloadEntity = vm.getPlatformEntity().unloadEntity

        platformUnloadEntity?.beforeValue = acetWeightBefore?.text.toString().toFloatOrNull()
        platformUnloadEntity?.afterValue = acetWeightAfter?.text.toString().toFloatOrNull()
        platformUnloadEntity?.ticketValue = acetTalonValue?.text.toString().toFloatOrNull()

        if(platformUnloadEntity != null)
            vm.database.setPlatformUnloadEntity(platformUnloadEntity)
    }

    override fun onResume() {
        super.onResume()
        //todo:r_dos:: сделать прозрачными
        val params: WindowManager.LayoutParams? = dialog?.window?.attributes
        params?.height = FrameLayout.LayoutParams.WRAP_CONTENT
        params?.width = FrameLayout.LayoutParams.MATCH_PARENT
        params?.horizontalMargin = 81f
//        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.attributes = params
    }
}