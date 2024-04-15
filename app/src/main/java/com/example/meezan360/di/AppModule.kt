package com.example.meezan360.di

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.meezan360.datamodule.repository.ConnectivityRepository
import com.example.meezan360.viewmodel.DashboardViewModel
import com.example.meezan360.viewmodel.LoginViewModel
import com.example.meezan360.viewmodel.ReportViewModel
import com.example.meezan360.viewmodel.SearchFragViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

@RequiresApi(Build.VERSION_CODES.N)
val appModule = module {
    viewModel { LoginViewModel(get(named("data_repo")), ConnectivityRepository(androidApplication())) }
    viewModel { DashboardViewModel(get(named("data_repo"))) }
    viewModel { ReportViewModel(get(named("data_repo"))) }
    viewModel { SearchFragViewModel(get(named("data_repo"))) }
}