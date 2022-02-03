package ru.smartro.worknote.di
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.smartro.worknote.ui.camera.CameraViewModel
import ru.smartro.worknote.ui.debug.DebugViewModel
import ru.smartro.worknote.ui.journal.JournalViewModel
import ru.smartro.worknote.work.ac.map.MapAct
import ru.smartro.worknote.ui.platform_serve.PlatformServeViewModel
import ru.smartro.worknote.ui.problem.ProblemViewModel
import ru.smartro.worknote.work.ac.StartAct
import ru.smartro.worknote.work.ac.choose.OwnerAct
import ru.smartro.worknote.work.ac.choose.TaskWorkorderAct
import ru.smartro.worknote.work.ac.choose.VehicleActivity
import ru.smartro.worknote.work.ac.choose.WayBillActivity

val viewModelModule = module {
    viewModel { StartAct.AuthViewModel(androidApplication()) }
    viewModel {
        OwnerAct.OrganisationViewModel(androidApplication())
    }
    viewModel {
        VehicleActivity.VehicleViewModel(androidApplication())
    }
    viewModel {
        WayBillActivity.WayListViewModel(androidApplication())
    }
    viewModel {
        TaskWorkorderAct.WayTaskViewModel(androidApplication())
    }
    viewModel {
        MapAct.MapViewModel(androidApplication())
    }

    viewModel { PlatformServeViewModel(androidApplication()) }
    viewModel { CameraViewModel(androidApplication()) }
    viewModel { ProblemViewModel(androidApplication()) }
    viewModel { DebugViewModel(androidApplication()) }
    viewModel { JournalViewModel(androidApplication()) }
}