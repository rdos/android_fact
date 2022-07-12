package ru.smartro.worknote.presentation.platform_serve

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import ru.smartro.worknote.*
import ru.smartro.worknote.abs.ActNOAbst
import ru.smartro.worknote.awORKOLDs.extensions.hideDialog
import ru.smartro.worknote.awORKOLDs.util.PhotoTypeEnum
import ru.smartro.worknote.awORKOLDs.util.StatusEnum
import ru.smartro.worknote.log.FragmentNavigator
import ru.smartro.worknote.work.cam.PhotoBeforeMediaF
import ru.smartro.worknote.work.cam.CameraAct
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.commands.Command

//todo: INDEterminate)
class PServeAct :
    ActNOAbst() , Navigator, NavigatorHolder {

//    private var mTEMPscreenKeyVAL: String? = null
//    private  val mNavigator = object : FragmentNavigator(supportFragmentManager, R.id.f_container) {
//        override fun createFragment(screenKey: String?, data: Any?): Fragment {
//            mTEMPscreenKeyVAL = screenKey
//            when(screenKey) {
//                SCREEN_PhotoBeforeMediaF -> {
//                    val result = PhotoBeforeMediaF.newInstance(data)
//                    return result
//                }
//                SCREEN_PServeF -> {
//                    val res = PServeF.newInstance(data)
//                    return res
//                }
////                case default_LIST_SCREEN:
////                return DetailsFragment.getNewInstance(data);
//                else -> return PhotoBeforeMediaF.newInstance(data)
////                throw new default     RuntimeException(“Unknown СИКРЕТ key!”);
//            }
//        }
//
//        override fun showSystemMessage(message: String?) {
////            TODO("Not yet implemented")
//        }
//
//        override fun exit() {
////            TODO("Not yet implemented")
//        }
//
//
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_platformserve)
        supportActionBar?.hide()
        val platformId = intent.getIntExtra("platform_id", Inull)
        if(vm.mBeforeMediaWasInited.value == false) {
            val bundle = Bundle()
            bundle.putInt("ARGUMENT_NAME___PARAM_ID", platformId)
            val navController = (supportFragmentManager.findFragmentById(R.id.f_container) as NavHostFragment).navController
            navController.navigate(R.id.PhotoBeforeMediaF, bundle)
//            setupActionBarWithNavController(navController)
//            App.getAppliCation().getRouter().navigateTo(SCREEN_PhotoBeforeMediaF, platformId)
            vm.mBeforeMediaWasInited.postValue(true)
        }


    }

    val vm: PlatformServeSharedViewModel by viewModels()
    private var mBackPressedCnt: Int = 2

    override fun onResume() {
        super.onResume()
//        App.getAppliCation().getNavigatorHolder().setNavigator(mNavigator)
    }

    override fun onPause() {
//        App.getAppliCation().getNavigatorHolder().removeNavigator()
        super.onPause()
    }

    // TODO: !~!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    override fun onBackPressed() {
        val navHostFragment = (supportFragmentManager.findFragmentById(R.id.f_container) as NavHostFragment)
        val navController = navHostFragment.navController
        if (navController.currentDestination?.id == R.id.PServeF) {
            mBackPressedCnt--
            if (mBackPressedCnt <= 0) {
                (navHostFragment.getChildFragmentManager().getFragments().get(0) as AFragment).onBackPressed()
                super.onBackPressed()
                return
            }
            toast("Вы не завершили обслуживание КП. Нажмите ещё раз, чтобы выйти")
            return
        }

        (navHostFragment.getChildFragmentManager().getFragments().get(0) as AFragment).onBackPressed()
//        super.onBackPressed()
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
