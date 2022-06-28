package ru.smartro.worknote.presentation.terminate

import android.os.Bundle
import androidx.navigation.findNavController
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.ActNOAbst

//todo: INDEterminate))
class TerminateAct : ActNOAbst() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_main)
        supportActionBar?.title = "Завершение заданий"
    }

    override fun onBackPressed() {
        val currentDest = findNavController(R.id.fragment_container_end_tasks).currentDestination
        if (currentDest == null || currentDest.id == R.id.finishCompleteF) {
            return
        }
        super.onBackPressed()
        finish()
    }

}