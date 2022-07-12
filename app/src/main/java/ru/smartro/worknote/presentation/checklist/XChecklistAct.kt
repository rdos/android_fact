package ru.smartro.worknote.presentation.checklist

import android.os.Bundle
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.ActNOAbst
import ru.smartro.worknote.log.AAct

class XChecklistAct: ActNOAbst() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_checklist)

    }
}