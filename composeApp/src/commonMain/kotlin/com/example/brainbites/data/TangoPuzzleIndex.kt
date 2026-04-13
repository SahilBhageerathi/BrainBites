package com.example.brainbites.data

import com.russhwolf.settings.Settings

class TangoPuzzleIndex(private val settings: Settings) {

    private val KEY_INDEX = "last_solved_index"

    fun saveIndex(index: Int) {
        settings.putInt(KEY_INDEX, index)
    }

    fun getIndex(): Int {
        return settings.getInt(KEY_INDEX, 0)
    }
}