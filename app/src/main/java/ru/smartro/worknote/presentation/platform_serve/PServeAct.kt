package ru.smartro.worknote.presentation.platform_serve

import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import ru.smartro.worknote.AFragment
import ru.smartro.worknote.Inull
import ru.smartro.worknote.R
import ru.smartro.worknote.Snull
import ru.smartro.worknote.abs.ActNOAbst
import ru.smartro.worknote.toast

//todo: INDEterminate)
class PServeAct :
    ActNOAbst() {
    val vm: PlatformServeSharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_platformserve)
        supportActionBar?.hide()
        val platformId = intent.getIntExtra("platform_id", Inull)
        val modeTMP_know1 = intent.getStringExtra("mode")?: Snull

        val bundle = Bundle()
        bundle.putInt("ARGUMENT_NAME___PARAM_ID", platformId)
        val navController = (supportFragmentManager.findFragmentById(R.id.f_container) as NavHostFragment).navController

        if(modeTMP_know1 == "itFireMode") {
            navController.navigate(R.id.FAStPhotoFailureMediaF, bundle)
            return
        }
        if(vm.mBeforeMediaWasInited.value == false) {

            navController.navigate(R.id.PhotoBeforeMediaF, bundle)
//            setupActionBarWithNavController(navController)
            vm.mBeforeMediaWasInited.postValue(true)
        }


    }


    private var mBackPressedCnt: Int = 2

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

    //todo:::
    override fun onDestroy() {
        super.onDestroy()
        if(paramS().walkthroughWasShownCnt < 3) {
            paramS().isWalkthroughWasShown = false
        }
    }

}