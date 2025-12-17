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

        gameManager = GameManager(this)
        gameManager.setup(binding)
        gameManager.initGame()
    }

    override fun onStart() {
        super.onStart()
        gameManager.start()
    }

    override fun onStop() {
        super.onStop()
        gameManager.stop()
    }
}
