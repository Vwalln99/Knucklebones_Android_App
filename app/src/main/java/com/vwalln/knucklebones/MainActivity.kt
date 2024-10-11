package com.vwalln.knucklebones

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startButton = findViewById<Button>(R.id.easyButton)
        val mediumButton = findViewById<Button>(R.id.mediumButton)
        val offlineButton = findViewById<Button>(R.id.offlineButton)
        val playerNameInput = findViewById<EditText>(R.id.playerNameText)
        val infoButton: ImageButton = findViewById(R.id.infoButtonRules)

        infoButton.setOnClickListener {
            val intent = Intent(this, RulesActivity::class.java)
            startActivity(intent)
        }

        startButton.setOnClickListener {
            val playerName = playerNameInput.text.toString()
            if (playerName.isBlank()) {
                playerNameInput.error = "Please enter your name"
            } else {
                val intent = Intent(this, GameActivity::class.java)
                intent.putExtra("main_name", playerName)
                startActivity(intent)
            }
        }

        mediumButton.setOnClickListener {
            val playerName = playerNameInput.text.toString()
            if (playerName.isBlank()) {
                playerNameInput.error = "Please enter your name"
            } else {
                val intent = Intent(this, OpponentAI::class.java)
                intent.putExtra("main_name", playerName)
                intent.putExtra("difficulty", "medium")
                startActivity(intent)
            }
        }
        offlineButton.setOnClickListener {
            val playerName = playerNameInput.text.toString()
            if (playerName.isBlank()) {
                playerNameInput.error = "Please enter your name"
            } else {
                showSecondPlayerDialog(playerName)
            }
        }
    }
    private fun showSecondPlayerDialog(player1Name: String) {
        val secondPlayerNameInput = EditText(this)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Enter Second Player's Name")
            .setView(secondPlayerNameInput)
            .setPositiveButton("Start Game") { _, _ ->
                val secondPlayerName = secondPlayerNameInput.text.toString()
                if (secondPlayerName.isBlank()) {
                    secondPlayerNameInput.error = "Please enter the second player's name"
                } else {
                    startOfflineGame(player1Name, secondPlayerName)
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }
    private fun startOfflineGame(player1Name: String, player2Name: String) {
        val intent = Intent(this, OfflinePlay::class.java)
        intent.putExtra("main_name", player1Name)
        intent.putExtra("second_name", player2Name)
        startActivity(intent)
    }
}
