package com.example.project_10208

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project_10208.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), GameCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var gameManager: GameManager

    private lateinit var scoreManager: ScoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val speed = intent.getLongExtra(GameConfig.EXTRA_SPEED, GameConfig.SPEED_SLOW)
        val mode = intent.getStringExtra(GameConfig.EXTRA_MODE) ?: GameConfig.MODE_BUTTONS

        scoreManager = ScoreManager(this)

        gameManager = GameManager(this, this, scoreManager)

        gameManager.setup(mode, speed)

        setupUI(mode)

        gameManager.initGame()
        binding.tvGameOver.visibility = View.GONE
    }

    private fun setupUI(mode: String) {
        if (mode == GameConfig.MODE_SENSORS) {
            binding.btnLeft.visibility = View.INVISIBLE
            binding.btnRight.visibility = View.INVISIBLE
        } else {
            binding.btnLeft.visibility = View.VISIBLE
            binding.btnRight.visibility = View.VISIBLE

            binding.btnLeft.setOnClickListener { gameManager.movePlayerLeft() }
            binding.btnRight.setOnClickListener { gameManager.movePlayerRight() }
        }
    }

    override fun onUpdateDistance(distance: Int) {
        runOnUiThread {
            if (!isFinishing && !isDestroyed) {
                binding.tvDistance.text = "Distance: $distance m"
            }
        }
    }

    override fun onUpdateCoins(count: Int) {
        runOnUiThread {
            if (!isFinishing && !isDestroyed) {
                binding.tvCoins.text = "Coins: $count"
            }
        }
    }

    override fun onGameOver(distance: Int, coins: Int) {
        runOnUiThread {
            if (!isFinishing && !isDestroyed) {
                binding.tvGameOver.visibility = View.VISIBLE
                Toast.makeText(this, "Game Over! Distance: $distance m, Coins: $coins", Toast.LENGTH_LONG).show()
            }
        }
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