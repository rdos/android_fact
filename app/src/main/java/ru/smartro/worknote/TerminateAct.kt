package ru.smartro.worknote

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import ru.smartro.worknote.abs.ActNOAbst
import ru.smartro.worknote.log.SupportFragmentNavigator
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.commands.Command

const val SCREEN_EARLY_COMPLETE: String = "SCREEN_EARLY_COMPLETE"
const val SCREEN_SUCCESS_COMPLETE: String = "SCREEN_SUCCESS_COMPLETE"
//todo: INDEterminate))
class TerminateAct : ActNOAbst(), Navigator, NavigatorHolder {
    private var mTEMPscreenKeyVAL: String? = null
    private  val mNavigator = object : SupportFragmentNavigator(supportFragmentManager, R.id.fragment_container) {
        override fun createFragment(screenKey: String?, data: Any?): Fragment {
            mTEMPscreenKeyVAL = screenKey
            when(screenKey) {
                SCREEN_EARLY_COMPLETE -> {
                    val res = CompleteF.newInstance(data)
                    return res
                }
                SCREEN_SUCCESS_COMPLETE -> {
                    val res = FinishCompleteF.newInstance(data)
                    return res
                }
//                case default_LIST_SCREEN:
//                return DetailsFragment.getNewInstance(data);
                else -> return CompleteF.newInstance(data)
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
        supportActionBar?.title = "Завершение заданий"
//        val workOrderId = getPutExtraParam_ID()
//        val link = getPutExtraParam_NAME()
        App.getAppliCation().getRouter().navigateTo(SCREEN_EARLY_COMPLETE)
    }
    override fun onResume() {
        super.onResume()
        App.getAppliCation().getNavigatorHolder().setNavigator(mNavigator)
    }

    override fun onPause() {
        App.getAppliCation().getNavigatorHolder().removeNavigator()
        super.onPause()
    }

    override fun onBackPressed() {
        if (mTEMPscreenKeyVAL == SCREEN_SUCCESS_COMPLETE) {
            return
        }
        super.onBackPressed()
        finish()
    }
    override fun applyCommand(command: Command?) {
//        TODO("Not yet implemented")
        Log.w("TAGS", "applyCommand")
    }

    override fun setNavigator(navigator: Navigator?) {
        Log.w("TAGS", "setNavigator")
//        TODO("Not yet implemented")
    }

    override fun removeNavigator() {
        Log.w("TAGS", "removeNavigator")

//        TODO("Not yet implemented")
    }
/**
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
*/

}