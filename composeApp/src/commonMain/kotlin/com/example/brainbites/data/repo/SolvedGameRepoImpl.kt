package com.example.brainbites.data.repo

import com.example.brainbites.AppDatabase
import com.example.brainbites.SolvedGamesEntity
import com.example.brainbites.domain.interfaces.SolvedGamesRepo
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

class SolvedGameRepoImpl(
    db : AppDatabase
) : SolvedGamesRepo {

    private val queries = db.solvedGamesEntityQueries

    override suspend fun insertGame(level : Long, gameName : String, time : Long) {
        queries.insertGame(
            level = level,
            gameName = gameName,
            time = time
        )
    }

    override suspend fun updateGame(game: SolvedGamesEntity) {
        queries.updateGame(
            level = game.level,
            gameName = game.gameName,
            time = game.time,
            id = game.id
        )
    }

    override suspend fun deleteGame(id: Long) {
        queries.deleteGame(id)
    }

    override fun getAllGames(): Flow<List<SolvedGamesEntity>> {
        return queries.selectAll().asFlow().mapToList(Dispatchers.IO)
    }

    override fun getGamesByName(name: String): Flow<List<SolvedGamesEntity>> {
        return queries.selectByName(name).asFlow().mapToList(Dispatchers.IO)
    }
}