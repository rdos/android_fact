package ru.smartro.worknote.awORKOLDs.adapter

import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.smartro.worknote.MapAct
import ru.smartro.worknote.presentation.checklist.ChecklistViewModel
import ru.smartro.worknote.presentation.platform_serve.PlatformServeSharedViewModel
import ru.smartro.worknote.presentation.terminate.TerminateViewModel
import ru.smartro.worknote.presentation.cam.PhotoViewModel
import ru.smartro.worknote.work.ui.*

val viewModelModule = module {

    viewModel { ChecklistViewModel(androidApplication()) }

    viewModel {
        MapAct.MapViewModel(androidApplication())
    }

    viewModel { PlatformServeSharedViewModel(androidApplication()) }

    viewModel { PhotoViewModel(androidApplication()) }

    viewModel { DebugAct.DebugViewModel(androidApplication()) }

    viewModel { JournalChatAct.JournalViewModel(androidApplication()) }

    viewModel { TerminateViewModel(androidApplication()) }
}