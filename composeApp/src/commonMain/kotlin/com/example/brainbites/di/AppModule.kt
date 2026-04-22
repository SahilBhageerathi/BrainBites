package com.example.brainbites.di

import com.example.brainbites.AppDatabase
import com.example.brainbites.data.repo.FlowRepositoryImpl
import com.example.brainbites.data.repo.SolvedGameRepoImpl
import com.example.brainbites.domain.interfaces.SolvedGamesRepo
import com.example.brainbites.domain.repo.FlowRepository
import com.example.brainbites.platform.DriverFactory
import com.example.brainbites.platform.TimeProvider
import com.example.brainbites.platform.createDataStore
import com.example.brainbites.presentation.viewModels.Flow.FlowViewModel
import com.example.brainbites.presentation.viewModels.Tango.TangoViewModel
import com.example.brainbites.utils.DefaultDispatcherProvider
import com.example.brainbites.utils.DispatcherProvider
import com.example.brainbites.utils.GameTimer
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun appModules() = listOf(dataModule, utilsModule, viewModelModule)

val dataModule = module {
    single<FlowRepository> { FlowRepositoryImpl() }
    single { createDataStore() }
    single { get<DriverFactory>().createDriver() }
    single { AppDatabase(get()) }
    single<SolvedGamesRepo> { SolvedGameRepoImpl(get()) }
}

val utilsModule = module {
    factory { TimeProvider() }
    factory { GameTimer(get()) }
    single<DispatcherProvider> { DefaultDispatcherProvider() }
}

val viewModelModule = module {
    viewModelOf(::FlowViewModel)
    single {
        TangoViewModel(get(), get(), get())
    }
}