package com.example.brainbites.platform

import com.example.brainbites.utils.AppLifecycleObserver

expect class PlatformLifecycle() {
    fun register(observer: AppLifecycleObserver)
    fun unregister()
}