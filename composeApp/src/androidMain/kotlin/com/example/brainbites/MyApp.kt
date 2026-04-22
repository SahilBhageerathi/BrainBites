package com.example.brainbites

import android.app.Application
import com.example.brainbites.di.appModules
import com.example.brainbites.platform.DriverFactory
import com.example.brainbites.platform.setDataStoreContext
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        setDataStoreContext(applicationContext)
        startKoin {
            androidContext(this@MyApp)
            modules(appModules())
            koin.loadModules(
                listOf(
                    module{
                        single { DriverFactory(this@MyApp) }
                    }
                )
            )
        }
    }
}