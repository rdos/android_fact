package ru.smartro.worknote

import android.os.Bundle
import androidx.fragment.app.Fragment
import ru.smartro.worknote.abs.ActNOAbst
import ru.smartro.worknote.log.SupportFragmentNavigator
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.commands.BackTo
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Replace

const val SCREEN_EARLY_COMPLETE: String = "SCREEN_EARLY_COMPLETE"
const val SCREEN_SUCCESS_COMPLETE: String = "SCREEN_SUCCESS_COMPLETE"
//todo: INDEterminate))
class TerminateAct : ActNOAbst(), Navigator, NavigatorHolder {

    private  val mNavigator = object : SupportFragmentNavigator(supportFragmentManager, R.id.fragment_container) {
        override fun createFragment(screenKey: String?, data: Any?): Fragment {
            when(screenKey) {
                SCREEN_EARLY_COMPLETE -> {
                    val res = CompleteEarlyF.newInstance(data!!)
                    return res
                }
                SCREEN_SUCCESS_COMPLETE -> {
                    val res = CompleteSuccessF.newInstance(data!!)
                    return res
                }
//                case default_LIST_SCREEN:
//                return DetailsFragment.getNewInstance(data);
                else -> return CompleteEarlyF.newInstance(data)
//                throw new default     RuntimeException(“Unknown СИКРЕТ key!”);
            }
        }

        override fun showSystemMessage(message: String?) {
//            TODO("Not yet implemented")
        }

        override fun exit() {
//            TODO("Not yet implemented")
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        FRAG
        setContentView(R.layout.a_main)
        supportActionBar?.hide()
        val workOrderId = getPutExtraParam_ID()
        val link = getPutExtraParam_NAME()
        var isLintTrue_successComplet = false
        val workOrderS = App.getAppliCation().getDB().getWorkOrderEntityS(true)
        for (workorder in workOrderS) {
            if (workorder.cnt_platform_status_new <= 0) {
                isLintTrue_successComplet = true
            }

        }
        if (isLintTrue_successComplet) {
            App.getAppliCation().getRouter().navigateTo(SCREEN_SUCCESS_COMPLETE, workOrderId)
        } else {
            App.getAppliCation().getRouter().navigateTo(SCREEN_EARLY_COMPLETE, workOrderId)
        }
    }

                        override fun onBackPressed() {
                            super.onBackPressed()
                            finish()
                        }

    fun navigateToNewRootWithMessage(
        screenKey: String?,
        data: Any?,
        message: String?
    ) {
        executeCommand(BackTo(null))
        executeCommand(Replace(screenKey, data))
//        executeCommand(SystemMessage(screenKey, data))
    }
    protected fun executeCommand(vararg commands: Command) {
//        App.getAppliCation().getRouter().navigateTo(executeCommands(commands)
    }

    override fun onResume() {
        super.onResume()
        App.getAppliCation().getNavigatorHolder().setNavigator(mNavigator)
    }

    override fun onPause() {
        App.getAppliCation().getNavigatorHolder().removeNavigator()
        super.onPause()
    }


    override fun applyCommand(command: Command?) {
//        TODO("Not yet implemented")
    }

    override fun setNavigator(navigator: Navigator?) {
//        TODO("Not yet implemented")
    }

    override fun removeNavigator() {
//        TODO("Not yet implemented")
    }


//    interface NavigatorHolder {
//
//        /**
//         * Set an active Navigator for the Cicerone and start receive commands.
//         *
//         * @param navigator new active Navigator
//         */
//        fun setNavigator(navigator: Navigator?)
//
//        /**
//         * Remove the current Navigator and stop receive commands.
//         */
//        fun removeNavigator()
//    }


}