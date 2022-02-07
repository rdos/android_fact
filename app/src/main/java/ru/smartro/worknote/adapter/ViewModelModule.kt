package ru.smartro.worknote.di
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.smartro.worknote.ui.camera.CameraViewModel
import ru.smartro.worknote.ui.debug.DebugActivity
import ru.smartro.worknote.ui.journal.JournalViewModel
import ru.smartro.worknote.work.ac.map.MapAct
import ru.smartro.worknote.ui.platform_serve.PlatformServeViewModel
import ru.smartro.worknote.ui.problem.ProblemViewModel
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

    viewModel { PlatformServeViewModel(androidApplication()) }
    viewModel { CameraViewModel(androidApplication()) }
    viewModel { ProblemViewModel(androidApplication()) }
    viewModel { DebugActivity.DebugViewModel(androidApplication()) }
    viewModel { JournalViewModel(androidApplication()) }
}