package com.example.project_10208

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val btnSlow = findViewById<MaterialButton>(R.id.btnModeButtonsSlow)
        val btnFast = findViewById<MaterialButton>(R.id.btnModeButtonsfast)
        val btnSensor = findViewById<MaterialButton>(R.id.btnModeSensors)
        val btnHighScores = findViewById<MaterialButton>(R.id.btnHighScores)



        btnSlow.setOnClickListener {
            startGame(GameConfig.SPEED_SLOW, GameConfig.MODE_BUTTONS)
        }


        btnFast.setOnClickListener {
            startGame(GameConfig.SPEED_FAST, GameConfig.MODE_BUTTONS)
        }


        btnSensor.setOnClickListener {
            startGame(GameConfig.SPEED_SLOW, GameConfig.MODE_SENSORS)
        }

        btnHighScores.setOnClickListener {
            val intent = Intent(this, HighScoresActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startGame(speed: Long, mode: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(GameConfig.EXTRA_SPEED, speed)
        intent.putExtra(GameConfig.EXTRA_MODE, mode)
        startActivity(intent)
    }
}