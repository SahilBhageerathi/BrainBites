package com.example.brainbites.Fakes

import com.example.brainbites.utils.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher

class TestDispatcherProvider(
    private val testDispatcher: CoroutineDispatcher
) : DispatcherProvider {
    override val main = testDispatcher
    override val io = testDispatcher
    override val default = testDispatcher
}