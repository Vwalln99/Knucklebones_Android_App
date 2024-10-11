package com.vwalln.knucklebones

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.vwalln.knucklebones.R.id.back_to_game_button

class RulesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rules)

        val okButton: Button = findViewById(back_to_game_button)
        okButton.setOnClickListener {
            finish()
        }
    }
}
