package com.example.project_10208

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.project_10208.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var gameManager: GameManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val speed = intent.getLongExtra(GameConfig.EXTRA_SPEED, GameConfig.SPEED_SLOW)
        val mode = intent.getStringExtra(GameConfig.EXTRA_MODE) ?: GameConfig.MODE_BUTTONS

        gameManager = GameManager(this)
        gameManager.setup(binding, mode, speed)
        gameManager.initGame()
    }

    override fun onResume() {
        super.onResume()
        gameManager.start()
    }

    override fun onPause() {
        super.onPause()
        gameManager.stop()
    }
}