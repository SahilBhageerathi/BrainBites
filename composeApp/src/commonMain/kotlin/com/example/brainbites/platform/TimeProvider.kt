package com.example.brainbites.platform

expect class TimeProvider() {
    fun currentTimeMillis(): Long
}