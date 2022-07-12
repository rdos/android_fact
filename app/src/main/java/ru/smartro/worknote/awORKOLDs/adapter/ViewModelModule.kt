package ru.smartro.worknote.awORKOLDs.adapter

import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.smartro.worknote.MapAct
import ru.smartro.worknote.presentation.checklist.StartOwnerViewModel
import ru.smartro.worknote.presentation.checklist.StartVehicleViewModel
import ru.smartro.worknote.presentation.checklist.StartWaybillViewModel
import ru.smartro.worknote.presentation.checklist.StartWorkOrderViewModel
import ru.smartro.worknote.presentation.platform_serve.PlatformServeSharedViewModel
import ru.smartro.worknote.work.ac.StartAct
import ru.smartro.worknote.work.ac.checklist.StartOwnerAct
import ru.smartro.worknote.work.ac.checklist.StartWorkOrderAct
import ru.smartro.worknote.work.ac.checklist.StartVehicleAct
import ru.smartro.worknote.work.ac.checklist.StartWayBillAct
import ru.smartro.worknote.work.cam.PhotoViewModel
import ru.smartro.worknote.work.ui.*

val viewModelModule = module {
    viewModel { StartAct.AuthViewModel(androidApplication()) }

    viewModel { StartOwnerViewModel(androidApplication()) }
    viewModel { StartVehicleViewModel(androidApplication()) }
    viewModel { StartWaybillViewModel(androidApplication()) }
    viewModel { StartWorkOrderViewModel(androidApplication()) }

    viewModel {
        MapAct.MapViewModel(androidApplication())
    }
    viewModel {
        ContainerFailureAct.ContainerFailureViewModel(androidApplication())
    }
    viewModel {
        ContainerBreakdownAct.ContainerBreakdownViewModel(androidApplication())
    }

    viewModel {
        PlatformFailureAct.NonPickupPlatformViewModel(androidApplication())
    }

    viewModel { PlatformServeSharedViewModel(androidApplication()) }
    viewModel { PhotoViewModel(androidApplication()) }
    viewModel { DebugAct.DebugViewModel(androidApplication()) }
    viewModel { JournalChatAct.JournalViewModel(androidApplication()) }
}