package com.example.brainbites.platform

import com.example.brainbites.utils.AppLifecycleObserver
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIApplicationDidBecomeActiveNotification
import platform.UIKit.UIApplicationWillResignActiveNotification
import platform.darwin.NSObjectProtocol

actual class PlatformLifecycle {

    private var backgroundObserver: NSObjectProtocol? = null
    private var foregroundObserver: NSObjectProtocol? = null

    actual fun register(observer: AppLifecycleObserver) {

        backgroundObserver = NSNotificationCenter.defaultCenter.addObserverForName(
            name = UIApplicationWillResignActiveNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue
        ) {
            observer.onBackground()
        }

        foregroundObserver = NSNotificationCenter.defaultCenter.addObserverForName(
            name = UIApplicationDidBecomeActiveNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue
        ) {
            observer.onForeground()
        }
    }

    actual fun unregister() {
        backgroundObserver?.let {
            NSNotificationCenter.defaultCenter.removeObserver(it)
        }
        foregroundObserver?.let {
            NSNotificationCenter.defaultCenter.removeObserver(it)
        }
    }
}