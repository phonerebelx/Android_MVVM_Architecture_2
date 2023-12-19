package com.example.meezan360.di

import com.example.meezan360.viewmodel.MyViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    viewModel { MyViewModel(get(named("data_repo"))) }
}