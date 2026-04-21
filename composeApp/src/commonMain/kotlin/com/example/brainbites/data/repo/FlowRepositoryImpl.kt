package com.example.brainbites.data.repo

import brainbites.composeapp.generated.resources.Res
import com.example.brainbites.domain.models.FlowPuzzle
import com.example.brainbites.domain.repo.FlowRepository
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi

class FlowRepositoryImpl : FlowRepository {

    private var puzzles: List<FlowPuzzle> = emptyList()

    private val json = Json { ignoreUnknownKeys = true }

    @OptIn(ExperimentalResourceApi::class)
    private suspend fun loadPuzzles() {
        if (puzzles.isEmpty()) {
            val jsonString = Res.readBytes("files/flow_puzzles.json").decodeToString()
            puzzles = json.decodeFromString(
                ListSerializer(FlowPuzzle.serializer()),
                jsonString
            )
        }
    }


    override suspend fun getAllPuzzles(): List<FlowPuzzle> {
        loadPuzzles()
        return puzzles
    }

    override fun getPuzzleCount(): Int {
        return puzzles.size
    }

}