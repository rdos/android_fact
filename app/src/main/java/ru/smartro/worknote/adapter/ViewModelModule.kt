package ru.smartro.worknote.di
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.smartro.worknote.work.ui.CameraViewModel
import ru.smartro.worknote.work.ui.DebugAct
import ru.smartro.worknote.work.ui.JournalViewModel
import ru.smartro.worknote.work.ui.ContainerBreakdownAct
import ru.smartro.worknote.work.ui.ContainerFailureAct
import ru.smartro.worknote.work.ui.PlatformFailureAct
import ru.smartro.worknote.work.ac.map.MapAct
import ru.smartro.worknote.work.platform_serve.PlatformServeViewModel
import ru.smartro.worknote.work.ac.StartAct
import ru.smartro.worknote.work.ac.checklist.StartOwnerAct
import ru.smartro.worknote.work.ac.checklist.StartWorkOrderAct
import ru.smartro.worknote.work.ac.checklist.StartVehicleAct
import ru.smartro.worknote.work.ac.checklist.StartWayBillAct

val viewModelModule = module {
    viewModel { StartAct.AuthViewModel(androidApplication()) }

    viewModel {
        StartOwnerAct.OrganisationViewModel(androidApplication())
    }
    viewModel {
        StartVehicleAct.VehicleViewModel(androidApplication())
    }
    viewModel {
        StartWayBillAct.WayListViewModel(androidApplication())
    }
    viewModel {
        StartWorkOrderAct.WayTaskViewModel(androidApplication())
    }
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

    viewModel { PlatformServeViewModel(androidApplication()) }
    viewModel { CameraViewModel(androidApplication()) }
    viewModel { DebugAct.DebugViewModel(androidApplication()) }
    viewModel { JournalViewModel(androidApplication()) }
}