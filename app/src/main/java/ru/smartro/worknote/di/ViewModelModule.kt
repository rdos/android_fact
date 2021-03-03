package ru.smartro.worknote.di
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.smartro.worknote.ui.auth.AuthViewModel
import ru.smartro.worknote.ui.camera.CameraViewModel
import ru.smartro.worknote.ui.choose.owner_1.OrganisationViewModel
import ru.smartro.worknote.ui.choose.vehicle_2.VehicleViewModel
import ru.smartro.worknote.ui.choose.way_list_3.WayListViewModel
import ru.smartro.worknote.ui.choose.way_task_4.WayTaskViewModel
import ru.smartro.worknote.ui.map.MapViewModel
import ru.smartro.worknote.ui.point_service.PointServiceViewModel
import ru.smartro.worknote.ui.problem.ProblemViewModel

val viewModelModule = module {
    viewModel { AuthViewModel(androidApplication()) }
    viewModel { OrganisationViewModel(androidApplication()) }
    viewModel { VehicleViewModel(androidApplication()) }
    viewModel { WayListViewModel(androidApplication()) }
    viewModel { WayTaskViewModel(androidApplication()) }
    viewModel { MapViewModel(androidApplication()) }
    viewModel { PointServiceViewModel(androidApplication()) }
    viewModel { CameraViewModel(androidApplication()) }
    viewModel { ProblemViewModel(androidApplication()) }
}