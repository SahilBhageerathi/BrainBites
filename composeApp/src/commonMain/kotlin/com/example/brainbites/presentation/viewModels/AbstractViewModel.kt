package com.example.brainbites.presentation.viewModels

import androidx.lifecycle.ViewModel

abstract class AbstractViewModel : ViewModel() {
    abstract fun onPlayNext()
    abstract fun onLeaderboardClick()
}