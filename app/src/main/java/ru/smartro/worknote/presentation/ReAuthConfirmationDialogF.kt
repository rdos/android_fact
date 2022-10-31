package ru.smartro.worknote.presentation

import android.content.Intent
import androidx.fragment.app.activityViewModels
import ru.smartro.worknote.App
import ru.smartro.worknote.LOG
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.presentation.ac.MainAct
import ru.smartro.worknote.presentation.ac.XChecklistAct
import ru.smartro.worknote.presentation.platform_serve.ServePlatformVM
import ru.smartro.worknote.work.PlatformEntity

class ReAuthConfirmationDialogF: AActionDialogF() {

    private val vm: ServePlatformVM by activityViewModels()

    override fun onLayoutInitialized() {

        val lastSynchroTimeInSec = App.getAppParaMS().lastSynchroAttemptTimeInSec
        var platforms: List<PlatformEntity> = emptyList()

        val m30MinutesInSec = 30 * 60
        if (MyUtil.timeStampInSec() - lastSynchroTimeInSec > m30MinutesInSec) {
            platforms = vm.database.findPlatforms30min()
            LOG.debug( "SYNCworkER PLATFORMS IN LAST 30 min")
        }
        if (platforms.isEmpty()) {
            platforms = vm.database.findLastPlatforms()
            LOG.debug("SYNCworkER LAST PLATFORMS")
        }

        val noSentPlatformCnt = platforms.size
        val noServedPlatformCnt = vm.database.findPlatformsIsNew().size
        var dialogString = ""

        if (noSentPlatformCnt > 0) {
            dialogString += "Не отправлено ${noSentPlatformCnt} данных, если не взять в работу, данные не будут отправлены на сервер;\n"
        }

        if (noServedPlatformCnt > 0) {
            dialogString += "Не обслужено ${noServedPlatformCnt} площадок.\n"
        }

        dialogString += "Вы уверены, что хотите выйти из задания?"

        actvTitle?.text = "Предупреждение"
        actvContent?.text = dialogString

        acbAccept?.text = "Нет, вернуться к заданию"
        acbAccept?.setOnClickListener {
            // TODO :::
//            findNavController().navigate(R.id.MapF)
            startActivity(Intent(requireActivity(), MainAct::class.java))

            requireActivity().finish()
        }

        acbDecline?.text = "Да, стереть и выйти"
        acbDecline?.setOnClickListener {
            App.getAppParaMS().setAppRestartParams()
            vm.database.clearDataBase()

            // TODO :::
//            findNavController().navigate(R.id.CheckListF)
            startActivity(Intent(requireActivity(), XChecklistAct::class.java))

            requireActivity().finish()
        }
    }
}