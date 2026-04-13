package com.example.brainbites.utils

import com.example.brainbites.platform.Logger
import com.example.brainbites.platform.TimeProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

private const val TAG = "GameTimer"
class GameTimer(
    private val timeProvider: TimeProvider
) {
    companion object{
        fun formatTime(millis: Long): String {
            val totalSeconds = millis / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60

            val minStr = if (minutes < 10) "0$minutes" else "$minutes"
            val secStr = if (seconds < 10) "0$seconds" else "$seconds"

            return "$minStr:$secStr"
        }
    }
    private var startTime: Long = 0L
    private var job: Job? = null

    // Tracks time already elapsed before pause
    private var pausedTime: Long = 0L

    private val _elapsedTime = MutableStateFlow(0L)
    val elapsedTime: StateFlow<Long> = _elapsedTime

    private var isRunning = false

    fun start(scope: CoroutineScope) {
        Logger.d(TAG, "Starting timer")
        if (isRunning) return

        startTime = timeProvider.currentTimeMillis()
        pausedTime = 0L
        isRunning = true

        startTicker(scope)
    }

    fun pause() {
        if (!isRunning) return

        pausedTime = _elapsedTime.value
        stopInternal()
        isRunning = false
    }

    fun resume(scope: CoroutineScope) {
        if (isRunning) return

        // Shift startTime back so elapsed continues correctly
        startTime = timeProvider.currentTimeMillis() - pausedTime
        isRunning = true

        startTicker(scope)
    }

    fun stop() {
        stopInternal()
        isRunning = false
        pausedTime = 0L
    }

    fun reset() {
        stopInternal()
        _elapsedTime.value = 0L
        pausedTime = 0L
        isRunning = false
    }

    private fun startTicker(scope: CoroutineScope) {
        job = scope.launch {
            while (isActive) {
                val now = timeProvider.currentTimeMillis()
                _elapsedTime.value = now - startTime
                delay(1000)
            }
        }
    }

    private fun stopInternal() {
        job?.cancel()
        job = null
    }
}