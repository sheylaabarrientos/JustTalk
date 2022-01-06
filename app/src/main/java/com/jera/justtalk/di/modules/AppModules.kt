package com.jera.justtalk.di.modules

import com.jera.justtalk.chatfeature.ChatViewModel
import com.jera.justtalk.dashboardfeature.DashboardViewModel
import com.jera.justtalk.firebase.AuthRepository
import com.jera.justtalk.firebase.FirebaseFirestoreRepository
import com.jera.justtalk.firebase.StorageRepository
import com.jera.justtalk.firebase.UserRepository
import com.jera.justtalk.loginfeature.LoginViewModel
import com.jera.justtalk.navigationbarfeature.GroupsViewModel
import com.jera.justtalk.registerfeature.AuthViewModel
import com.jera.justtalk.registerfeature.RetrivePasswordViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModules = module {
    viewModel { LoginViewModel(get(), get(), get()) }
    factory { AuthRepository() }
    factory { UserRepository() }
    viewModel { RetrivePasswordViewModel(get()) }
    factory { StorageRepository() }
    factory { FirebaseFirestoreRepository() }
    viewModel { AuthViewModel(get(), get(), get()) }
    viewModel { DashboardViewModel(get(), get()) }
    viewModel { ChatViewModel(get(), get()) }
    viewModel { GroupsViewModel(get(), get()) }
}
