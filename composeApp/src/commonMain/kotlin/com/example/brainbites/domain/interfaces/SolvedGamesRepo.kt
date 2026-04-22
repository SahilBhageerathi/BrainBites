package com.example.brainbites.domain.interfaces

import com.example.brainbites.SolvedGamesEntity
import kotlinx.coroutines.flow.Flow


interface SolvedGamesRepo {
    suspend fun insertGame(level : Long, gameName : String, time : Long)
    suspend fun updateGame(game: SolvedGamesEntity)
    suspend fun deleteGame(id: Long)

    fun getAllGames(): Flow<List<SolvedGamesEntity>>
    fun getGamesByName(name: String): Flow<List<SolvedGamesEntity>>
}