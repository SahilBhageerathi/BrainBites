package com.example.brainbites

import android.app.Application
import com.example.brainbites.platform.setDataStoreContext

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        setDataStoreContext(applicationContext)
    }
}