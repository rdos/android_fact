package ru.smartro.worknote.di
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.smartro.worknote.ui.auth.AuthViewModel
import ru.smartro.worknote.ui.camera.CameraViewModel
import ru.smartro.worknote.work.map.choose.OrganisationViewModel
import ru.smartro.worknote.work.map.choose.VehicleViewModel
import ru.smartro.worknote.work.map.choose.WayListViewModel
import ru.smartro.worknote.work.map.choose.TaskWorkorderAct
import ru.smartro.worknote.ui.debug.DebugViewModel
import ru.smartro.worknote.ui.journal.JournalViewModel
import ru.smartro.worknote.work.map.MapAct
import ru.smartro.worknote.ui.platform_serve.PlatformServeViewModel
import ru.smartro.worknote.ui.problem.ProblemViewModel

val viewModelModule = module {
    viewModel { AuthViewModel(androidApplication()) }
    viewModel { OrganisationViewModel(androidApplication()) }
    viewModel { VehicleViewModel(androidApplication()) }
    viewModel { WayListViewModel(androidApplication()) }
    viewModel { TaskWorkorderAct.WayTaskViewModel(androidApplication()) }
    viewModel { MapAct.MapViewModel(androidApplication()) }
    viewModel { PlatformServeViewModel(androidApplication()) }
    viewModel { CameraViewModel(androidApplication()) }
    viewModel { ProblemViewModel(androidApplication()) }
    viewModel { DebugViewModel(androidApplication()) }
    viewModel { JournalViewModel(androidApplication()) }
}