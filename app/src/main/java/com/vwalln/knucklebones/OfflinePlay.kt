package com.vwalln.knucklebones

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.vwalln.knucklebones.databinding.ActivityGameBinding
import java.util.*

class OfflinePlay : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var player: Player
    private lateinit var opponent: Player
    private lateinit var playerBoard: Array<Array<Int?>>
    private lateinit var opponentBoard: Array<Array<Int?>>
    private var currentDieValue = 0
    private val random = Random()
    private var isPlayerTurn = true
    private lateinit var player1Name: String
    private lateinit var player2Name: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        player1Name = intent.getStringExtra("main_name") ?: "Player 1"
        player2Name = intent.getStringExtra("second_name") ?: "Player 2"

        player = Player(player1Name)
        opponent = Player(player2Name)

        binding.mainName.text = player1Name
        binding.opponentName.text = player2Name

        setupViews()
        setupClickListeners()
        rollDieForCurrentPlayer()
    }

    private fun setupViews() {
        playerBoard = Array(3) { arrayOfNulls<Int?>(3) }
        opponentBoard = Array(3) { arrayOfNulls<Int?>(3) }

        updateCurrentPlayerName()
        updatePlayerScore()
        updateOpponentScore()
    }

    private fun setupClickListeners() {
        binding.backToMenu.setOnClickListener { finish() }

        binding.infoButtonRules.setOnClickListener {
            startActivity(Intent(this, RulesActivity::class.java))
        }

        for (row in 0 until 3) {
            for (col in 0 until 3) {
                val cell = binding.playerBoard.getChildAt(row * 3 + col)
                val opponentCell = binding.computerBoard.getChildAt(row * 3 + col)

                // Für Spieler 1
                cell.setOnClickListener {
                    if (isPlayerTurn) {
                        placeDieForCurrentPlayer(col, row, playerBoard)
                    }
                }

                // Für Spieler 2
                opponentCell.setOnClickListener {
                    if (!isPlayerTurn) {
                        placeDieForCurrentPlayer(col, row, opponentBoard)
                    }
                }
            }
        }
    }
    private fun dieResource(value: Int?): Int {
        return when (value) {
            1 -> R.drawable.die_1
            2 -> R.drawable.die_2
            3 -> R.drawable.die_3
            4 -> R.drawable.die_4
            5 -> R.drawable.die_5
            6 -> R.drawable.die_6
            else -> 0
        }
    }

    private fun rollDieForCurrentPlayer() {
        currentDieValue = random.nextInt(6) + 1

        if (isPlayerTurn) {
            binding.diemain.setImageResource(dieResource(value = currentDieValue))
            binding.diemain.visibility = View.VISIBLE
        } else {
            binding.dieOpponent.setImageResource(dieResource(value = currentDieValue))
            binding.dieOpponent.visibility = View.VISIBLE
        }
    }

    private fun placeDieForCurrentPlayer(col: Int, row: Int, board: Array<Array<Int?>>) {
        val targetRow = board.indexOfFirst { it[col] == null }

        if (targetRow != -1) {
            board[targetRow][col] = currentDieValue
            updateBoard(board, targetRow, col)

            val scoreValue = calculateScoreForColumn(board, col)
            if (isPlayerTurn) {
                player.addScore(scoreValue, col)
                removeMatchingDiceFromOpponentBoard(currentDieValue, col)
                updatePlayerScore()
            } else {
                opponent.addScore(scoreValue, col)
                removeMatchingDiceFromPlayerBoard(currentDieValue, col)
                updateOpponentScore()
            }

            if (checkGameEnd(board)) {
                endGame()
            } else {
                isPlayerTurn = !isPlayerTurn
                updateCurrentPlayerName()
                rollDieForCurrentPlayer()
            }
        }
    }


    private fun updateBoard(board: Array<Array<Int?>>, row: Int, col: Int) {
        val targetImageView = if (board === playerBoard) binding.playerBoard else binding.computerBoard
        val imageView = targetImageView.getChildAt(row * 3 + col) as ImageView
        imageView.setImageResource(dieResource(board[row][col]))
    }

    private fun updateCurrentPlayerName() {
        binding.mainName.text = if (isPlayerTurn) player.name else opponent.name
    }

    private fun updatePlayerScore() {
        binding.mainScore.text = player.totalScore.toString()
    }

    private fun updateOpponentScore() {
        binding.opponentScore.text = opponent.totalScore.toString()
    }

    private fun calculateScoreForColumn(board: Array<Array<Int?>>, col: Int): Int {
        val counts = mutableMapOf<Int, Int>()
        for (row in 0 until 3) {
            board[row][col]?.let { dieValue ->
                counts[dieValue] = counts.getOrDefault(dieValue, 0) + 1
            }
        }
        return counts.entries.sumOf { (value, count) -> value * count * count }
    }

    private fun checkGameEnd(board: Array<Array<Int?>>): Boolean {
        return board.all { row -> row.all { it != null } }
    }

    private fun endGame() {
        val message = when {
            player.totalScore > opponent.totalScore -> "${player.name} Wins!"
            player.totalScore < opponent.totalScore -> "${opponent.name} Wins!"
            else -> "It's a Draw!"
        }
        showEndGameDialog(message)
    }

    private fun showEndGameDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle(message)
            .setMessage("Would you like to play again?")
            .setPositiveButton("Yes") { _, _ -> recreate() }
            .setNegativeButton("No") { _, _ -> finish() }
            .show()
    }

    private fun removeMatchingDiceFromPlayerBoard(dieValue: Int, col: Int) {
        for (row in 2 downTo 0) {
            if (playerBoard[row][col] == dieValue) {
                playerBoard[row][col] = null
                val imageView = binding.playerBoard.getChildAt(row * 3 + col) as ImageView
                imageView.setImageResource(dieResource(0))
                player.subScore(row, col)
            }
        }

        shiftDiceDown(playerBoard, col)
    }

    private fun removeMatchingDiceFromOpponentBoard(dieValue: Int, col: Int) {
        for (row in 2 downTo 0) {
            if (opponentBoard[row][col] == dieValue) {
                opponentBoard[row][col] = null
                val imageView = binding.computerBoard.getChildAt(row * 3 + col) as ImageView
                imageView.setImageResource(dieResource(0))
                opponent.subScore(row, col)
            }
        }
        shiftDiceDown(opponentBoard, col)
    }

    private fun shiftDiceDown(board: Array<Array<Int?>>, col: Int) {
        for (row in 1 downTo 0) {
            if (board[row][col] == null) {
                for (shiftRow in row + 1..2) {
                    if (board[shiftRow][col] != null) {
                        board[row][col] = board[shiftRow][col]
                        board[shiftRow][col] = null
                        updateBoard(board, row, col)
                        updateBoard(board, shiftRow, col)
                        break
                    }
                }
            }
        }
    }
}
