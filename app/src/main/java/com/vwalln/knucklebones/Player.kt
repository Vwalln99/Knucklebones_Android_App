package com.vwalln.knucklebones

import android.util.Log

class Player(val name: String) {
    var boardScores = IntArray(3)
    var totalScore = 0

    fun addScore(value: Int, columnIndex: Int) {
        if (columnIndex in boardScores.indices) {
            boardScores[columnIndex] += value
            updateTotalScore()
        } else {
            Log.e("Player", "Invalid column index: $columnIndex")
        }
    }

    fun subScore(value: Int, columnIndex: Int) {
        if (columnIndex in boardScores.indices) {
            if (boardScores[columnIndex] >= value) {
                boardScores[columnIndex] -= value
            }
            updateTotalScore()
        } else {
            Log.e("Player", "Invalid column index: $columnIndex")
        }
    }

    private fun updateTotalScore() {
        totalScore = boardScores.sum()
    }
}
