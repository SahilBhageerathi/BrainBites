package com.example.brainbites.domain.usecases

import com.example.brainbites.platform.TimeProvider

class GameTimerUseCase(
    private val timeProvider: TimeProvider
) {

    private var startTime = 0L
    private var pausedTime = 0L
    private var isRunning = false

    fun start() {
        startTime = timeProvider.currentTimeMillis()
        pausedTime = 0L
        isRunning = true
    }

    fun pause(currentElapsed: Long) {
        if (!isRunning) return
        pausedTime = currentElapsed
        isRunning = false
    }

    fun resume() {
        if (isRunning) return
        startTime = timeProvider.currentTimeMillis() - pausedTime
        isRunning = true
    }

    fun stop() {
        isRunning = false
        pausedTime = 0L
    }

    fun getElapsedTime(): Long {
        return if (isRunning) {
            timeProvider.currentTimeMillis() - startTime
        } else {
            pausedTime
        }
    }
}