package com.example.meezan360.di

import com.example.meezan360.viewmodel.DashboardViewModel
import com.example.meezan360.viewmodel.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    viewModel { LoginViewModel(get(named("data_repo"))) }
    viewModel { DashboardViewModel(get(named("data_repo"))) }
}