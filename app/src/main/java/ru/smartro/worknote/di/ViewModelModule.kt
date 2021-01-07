package ru.smartro.worknote.di
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.smartro.worknote.ui.auth.AuthViewModel
import ru.smartro.worknote.ui.owner_1.OwnerViewModel

val viewModelModule = module {
    viewModel { AuthViewModel(androidApplication()) }
    viewModel { OwnerViewModel(androidApplication()) }

}