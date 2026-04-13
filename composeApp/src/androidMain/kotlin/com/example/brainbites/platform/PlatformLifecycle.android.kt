package com.example.brainbites.platform

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.brainbites.utils.AppLifecycleObserver

actual class PlatformLifecycle {

    private var observer: AppLifecycleObserver? = null

    private val lifecycleObserver = object : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            observer?.onForeground()
        }

        override fun onStop(owner: LifecycleOwner) {
            observer?.onBackground()
        }
    }

    actual fun register(observer: AppLifecycleObserver) {
        this.observer = observer
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleObserver)
    }

    actual fun unregister() {
        ProcessLifecycleOwner.get().lifecycle.removeObserver(lifecycleObserver)
    }
}