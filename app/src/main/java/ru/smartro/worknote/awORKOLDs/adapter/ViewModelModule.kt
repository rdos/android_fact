package ru.smartro.worknote.awORKOLDs.adapter

import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.smartro.worknote.presentation.checklist.ChecklistViewModel
import ru.smartro.worknote.presentation.checklist.workorder.StartWorkOrderViewModel
import ru.smartro.worknote.presentation.platform_serve.PlatformServeSharedViewModel
import ru.smartro.worknote.presentation.terminate.TerminateViewModel
import ru.smartro.worknote.presentation.came.PhotoViewModel
import ru.smartro.worknote.work.ac.StartAct
import ru.smartro.worknote.work.ui.DebugFragment
import ru.smartro.worknote.work.ui.JournalChatFragment


val viewModelModule = module {
    viewModel { StartAct.AuthViewModel(androidApplication()) }

    viewModel { ChecklistViewModel(androidApplication()) }
    viewModel { DebugFragment.DebugViewModel(androidApplication()) }

    viewModel { JournalChatFragment.JournalViewModel(androidApplication()) }
    viewModel { StartWorkOrderViewModel(androidApplication()) }

    viewModel { PlatformServeSharedViewModel(androidApplication()) }

    viewModel { PhotoViewModel(androidApplication()) }

    viewModel { TerminateViewModel(androidApplication()) }
}