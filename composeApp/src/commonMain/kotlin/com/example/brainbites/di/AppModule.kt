package com.example.brainbites.di

import com.example.brainbites.platform.TimeProvider
import com.example.brainbites.platform.createDataStore
import com.example.brainbites.presentation.viewModels.TangoViewModel
import com.example.brainbites.utils.GameTimer
import org.koin.dsl.module

val appModule = module {
    single {
        createDataStore()
    }
    factory {
        TimeProvider()
    }
    factory {
        GameTimer(get())
    }

    single {
        TangoViewModel(get(), get())
    }
}