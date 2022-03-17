package ru.smartro.worknote.work.ui

import android.os.Bundle
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.ActNOAbst

class ScannerAct : ActNOAbst() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

    }
}