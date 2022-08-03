package ru.smartro.worknote.presentation.ac

import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import ru.smartro.worknote.abs.AFragment
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.AAct
import ru.smartro.worknote.presentation.platform_serve.PlatformServeSharedViewModel
import ru.smartro.worknote.toast

//todo: INDEterminate)
class MainAct :
    AAct() {
    val vm: PlatformServeSharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_main)
        supportActionBar?.hide()
    }


    private var mBackPressedCnt: Int = 2

    // TODO: !~!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    override fun onBackPressed() {
        val navHostFragment = (supportFragmentManager.findFragmentById(R.id.fcv_container) as NavHostFragment)
        val navController = navHostFragment.navController
        if (navController.currentDestination?.id == R.id.PServeF) {
            mBackPressedCnt--
            if (mBackPressedCnt <= 0) {
                (navHostFragment.childFragmentManager.fragments[0] as AFragment).onBackPressed()
                super.onBackPressed()
                return
            }
            toast("Вы не завершили обслуживание КП. Нажмите ещё раз, чтобы выйти")
            return
        }

        (navHostFragment.childFragmentManager.fragments[0] as AFragment).onBackPressed()
//        super.onBackPressed()
    }

    //todo:::
    override fun onDestroy() {
        super.onDestroy()
        if(paramS().walkthroughWasShownCnt < 3) {
            paramS().isWalkthroughWasShown = false
        }
    }

}