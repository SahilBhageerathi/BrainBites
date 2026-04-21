package com.example.brainbites.domain.repo

import com.example.brainbites.domain.models.FlowPuzzle

interface FlowRepository {
    suspend fun getAllPuzzles(): List<FlowPuzzle>
    fun getPuzzleCount(): Int
}