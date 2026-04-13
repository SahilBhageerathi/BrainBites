package com.example.brainbites.platform

actual object Logger {
    actual fun d(tag: String, message: String) {
    }

    actual fun e(tag: String, message: String, throwable: Throwable?) {
    }

    actual fun i(tag: String, message: String) {
    }

    actual fun w(tag: String, message: String) {
    }
}