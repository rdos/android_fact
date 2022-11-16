package ru.smartro.worknote.presentation.ac

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import ru.smartro.worknote.abs.FragmentA
import ru.smartro.worknote.R

class EndF : FragmentA() {

    override fun onGetLayout(): Int {
        return R.layout.f_finish_complete
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideProgress()
//        EndF::class.java.constructors.get(0).newInstance()
        view.findViewById<Button>(R.id.finish_accept_btn).setOnClickListener {
            getAct().startActivity(Intent(getAct(), StartAct::class.java))
            getAct().finish()
        }
        view.findViewById<Button>(R.id.exit_btn).setOnClickListener {
            getAct().logout()
        }
    }

    override fun onBackPressed() {
        // TODO ::: Знаю, знаю..
    }
}