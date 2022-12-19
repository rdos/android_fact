package ru.smartro.worknote.presentation

import android.content.Intent
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.activityViewModels
import ru.smartro.worknote.App
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R
import ru.smartro.worknote.presentation.ac.AConfirmDF
import ru.smartro.worknote.ac.SmartROllc
import ru.smartro.worknote.log.todo.PlatformEntity

class DFConfirmReAuth : AConfirmDF() {

    private val viewModel: VMPserve by activityViewModels()
    private var platforms: List<PlatformEntity>? = null

    override fun onGetNavId(): Int {
        return R.id.DFConfirmReAuth
    }
    
    override fun onGetEntity(): PlatformEntity? {
        LOG.warn("DON'T_USE")   //not use
        return null
    }

    override fun onBindLayoutState(): Boolean {
        return false
    }

    override fun onLiveData(tbIbYO__item: PlatformEntity) {
        val lastSynchroTimeInSec = App.getAppParaMS().lastSynchroAttemptTimeInSec
        val m30MinutesInSec = 30 * 60
        if (App.getAppliCation().timeStampInSec() - lastSynchroTimeInSec > m30MinutesInSec) {
            platforms = viewModel.database.findPlatforms30min()
            LOG.debug( "SYNCworkER PLATFORMS IN LAST 30 min")
        }
        if (platforms == null || platforms!!.isEmpty()) {
            platforms = viewModel.database.findLastPlatforms()
            LOG.debug("SYNCworkER LAST PLATFORMS")
        }
    }

    override fun onStyle(sview: SmartROllc, acbGotoBack: AppCompatButton) {
        // DO NOTHING
    }

    override fun onNextFragment(entity: PlatformEntity) {
        App.getAppParaMS().showClearCurrentTasks = false
        startActivity(Intent(requireActivity(), ActMain::class.java))
        requireActivity().finish()
    }

    override fun onGetNextText(): String {
        return "Нет, вернуться к заданию"
    }

    override fun onBackFragment(entity: PlatformEntity) {
        App.getAppParaMS().setAppRestartParams()
        viewModel.database.clearDataBase()

        // TODO :::
//            findNavController().navigate(R.id.CheckListF)
        startActivity(Intent(requireActivity(), AXChecklist::class.java))

        requireActivity().finish()
    }

    override fun onGetBackText(): String {
        return "Да, стереть и выйти"
    }

    override fun onGetContentText(): String {
        val noSentPlatformCnt = platforms?.size ?: 0
        val noServedPlatformCnt = viewModel.database.findPlatformsIsNew().size
        var dialogString = ""

        if (noSentPlatformCnt > 0) {
            dialogString += "Не отправлено ${noSentPlatformCnt} данных, если не взять в работу, данные не будут отправлены на сервер;\n"
        }

        if (noServedPlatformCnt > 0) {
            dialogString += "Не обслужено ${noServedPlatformCnt} площадок.\n"
        }

        dialogString += "Вы уверены, что хотите выйти из задания?"

        return dialogString
    }
}