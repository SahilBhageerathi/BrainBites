package com.example.brainbites.utils

interface AppLifecycleObserver {
    fun onForeground()
    fun onBackground()
}