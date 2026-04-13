package com.example.brainbites.platform

import com.example.brainbites.utils.AppLifecycleObserver

actual class PlatformLifecycle {
    actual fun register(observer: AppLifecycleObserver) {
    }

    actual fun unregister() {
    }
}