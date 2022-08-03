package ru.smartro.worknote.presentation.ac

import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.findNavController
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


    override fun onBackPressed() {
        val navHostFragment = (supportFragmentManager.findFragmentById(R.id.fcv_container) as NavHostFragment)
        (navHostFragment.childFragmentManager.fragments[0] as AFragment).onBackPressed()
    }
}