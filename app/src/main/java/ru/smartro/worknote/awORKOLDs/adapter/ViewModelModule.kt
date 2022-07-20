package ru.smartro.worknote.awORKOLDs.adapter

import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.smartro.worknote.MapAct
import ru.smartro.worknote.presentation.checklist.ChecklistViewModel
import ru.smartro.worknote.presentation.checklist.owner.StartOwnerViewModel
import ru.smartro.worknote.presentation.checklist.vehicle.StartVehicleViewModel
import ru.smartro.worknote.presentation.checklist.waybill.StartWaybillViewModel
import ru.smartro.worknote.presentation.checklist.workorder.StartWorkOrderViewModel
import ru.smartro.worknote.presentation.platform_serve.PlatformServeSharedViewModel
import ru.smartro.worknote.presentation.terminate.TerminateViewModel
import ru.smartro.worknote.work.ac.StartAct
import ru.smartro.worknote.work.cam.PhotoViewModel
import ru.smartro.worknote.work.ui.*

val viewModelModule = module {
    viewModel { StartAct.AuthViewModel(androidApplication()) }

//    viewModel { StartOwnerViewModel(androidApplication()) }
//    viewModel { StartVehicleViewModel(androidApplication()) }
//    viewModel { StartWaybillViewModel(androidApplication()) }
//    viewModel { StartWorkOrderViewModel(androidApplication()) }
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