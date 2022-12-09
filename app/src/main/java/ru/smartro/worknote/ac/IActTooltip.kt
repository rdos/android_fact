package ru.smartro.worknote.ac

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

interface IActTooltip {
    fun onNewfromAFragment(isFromRecylcer: Boolean = false)
    fun setSpecialProcessingForRecycler(recyclerView: RecyclerView?)
    fun createvDialog(): View
    fun getvgRootAct(): ViewGroup
    fun createDialogBuilder(): AlertDialog.Builder
}