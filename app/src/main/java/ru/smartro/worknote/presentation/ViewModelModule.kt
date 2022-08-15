package ru.smartro.worknote

import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.smartro.worknote.presentation.checklist.workorder.StartWorkOrderViewModel
import ru.smartro.worknote.presentation.platform_serve.PlatformServeSharedViewModel
import ru.smartro.worknote.presentation.came.PhotoViewModel
import ru.smartro.worknote.presentation.ac.StartAct
import ru.smartro.worknote.presentation.ac.XChecklistAct
import ru.smartro.worknote.presentation.terminate.CompleteF
import ru.smartro.worknote.presentation.DebugF
import ru.smartro.worknote.presentation.JournalChatF
import ru.smartro.worknote.presentation.platform_serve.PServeByTypesViewModel
import ru.smartro.worknote.presentation.platform_serve.PServeViewModel


val viewModelModule = module {
    viewModel { StartAct.AuthViewModel(androidApplication()) }

    viewModel { XChecklistAct.ChecklistViewModel(androidApplication()) }
    viewModel { DebugF.DebugViewModel(androidApplication()) }

    viewModel { JournalChatF.JournalChatViewModel(androidApplication()) }
    viewModel { StartWorkOrderViewModel(androidApplication()) }

    viewModel { PlatformServeSharedViewModel(androidApplication()) }
    viewModel { PServeViewModel(androidApplication()) }
    viewModel { PServeByTypesViewModel(androidApplication()) }

    viewModel { MapViewModel(androidApplication()) }

    viewModel { PhotoViewModel(androidApplication()) }

    viewModel { CompleteF.CompleteViewModel(androidApplication()) }
}