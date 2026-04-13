package com.example.brainbites.platform

actual class TimeProvider {
    actual fun currentTimeMillis(): Long = System.currentTimeMillis()
}