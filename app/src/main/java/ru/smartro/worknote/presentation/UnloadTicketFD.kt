package ru.smartro.worknote.presentation

import android.content.DialogInterface
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.FragmentDialogA
import ru.smartro.worknote.andPOintD.SmartROllc
import ru.smartro.worknote.andPOintD.swipebtn.SmartROviewSwipeButton
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.ConfigName

//todo: смотри прикол, VT !!!UnloadInfo++ploadTicket
class UnloadTicketFD: FragmentDialogA() {
    private var acetTalonValue: AppCompatEditText? = null
    private var acetWeightAfter: AppCompatEditText? = null
    private var acetWeightBefore: AppCompatEditText? = null

    //    https://en.wikipedia.org/wiki/Virtual_machine :))))))
    private val vm: ServePlatformVM by activityViewModels()

    override fun onGetLayout(): Int {
        return R.layout.f_unload_ticket
    }
    
    override fun onInitLayoutView(sview: SmartROllc): Boolean {
        val platformUnloadEntity = vm.getPlatformEntity().unloadEntity

        val acibPhotoBefore = sview.findViewById<AppCompatImageButton>(R.id.acib__f_unload_ticket__photo_before)
        acibPhotoBefore.setOnClickListener {
            navigateNext(R.id.UnloadPhotoBeforeMediaF)
        }

        val acibPhotoAfter = sview.findViewById<AppCompatImageButton>(R.id.acib__f_unload_ticket__photo_after)
        acibPhotoAfter.setOnClickListener {
            navigateNext(R.id.UnloadPhotoAfterMediaF)
        }

        val acbFinish: SmartROviewSwipeButton? = sview.findViewById(R.id.acb_f_unload_ticket__finish)
        acbFinish?.onSwipe = {
            toast("выключился режим Выгрузки")
            vm.database.setConfig(ConfigName.AAPP__IS_MODE__UNLOAD, false)
            findNavController().previousBackStackEntry?.savedStateHandle?.set("buildNavigatorPlatformUnload", false)
            navigate(R.id.MapPlatformsF)
        }


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

        return true
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
}