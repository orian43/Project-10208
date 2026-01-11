package com.example.project_10208

import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project_10208.databinding.ActivityMainBinding

class GameManager(private val activity: AppCompatActivity) {

    private lateinit var binding: ActivityMainBinding

    private val grid: Array<Array<LinearLayout>>
    private val playerController: PlayerController
    private val timer: GameTimer
    private val meteorController: MeteorController
    private val livesManager: LivesManager
    private val distanceManager: DistanceManager
    private val coinController: CoinController


    private var lives = 3
    private var gameOver = false

    init {
        grid = Array(GameConfig.ROWS) { r ->
            Array(GameConfig.COLS) { c ->
                val id = activity.resources.getIdentifier(
                    "linCell_${r}_${c}",
                    "id",
                    activity.packageName
                )
                activity.findViewById(id)
            }
        }

        val playerCells = Array(GameConfig.COLS) { i ->
            val id = activity.resources.getIdentifier(
                "linPlayerCell$i",
                "id",
                activity.packageName
            )
            activity.findViewById<LinearLayout>(id)
        }

        playerController = PlayerController(activity, playerCells)
        meteorController = MeteorController(activity, grid)
        livesManager = LivesManager(activity)
        distanceManager = DistanceManager(activity)
        timer = GameTimer { updateGame() }
        coinController = CoinController(activity, grid)

    }

    fun setup(binding: ActivityMainBinding) {
        this.binding = binding


        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnLeft.setOnClickListener { movePlayerLeft() }
        binding.btnRight.setOnClickListener { movePlayerRight() }
    }

    fun initGame() {
        playerController.placePlayer()
        meteorController.spawnInitialMeteors()
        livesManager.reset()
        distanceManager.reset()
        coinController.reset()

    }

    fun start() = timer.start()
    fun stop() = timer.stop()
    fun movePlayerLeft() {
        if (!gameOver) playerController.moveLeft()
    }

    fun movePlayerRight() {
        if (!gameOver) playerController.moveRight()
    }

    private fun updateGame() {
        if (!gameOver) {
            distanceManager.increaseDistance()
            val meteorsAtBottom = meteorController.moveMeteorsDown()
            val coinsAtBottom = coinController.moveCoinsDown()
            checkCollisions(meteorsAtBottom)
            checkCoinCollection(coinsAtBottom)
            meteorController.spawnMeteorsOnePerRow()
            coinController.spawnCoin()

        }
    }

    private fun checkCollisions(meteorsAtBottom: List<Pair<Int, Int>>) {
        for ((_, c) in meteorsAtBottom) {
            if (c == playerController.position) {
                Vibration.vibrate(activity, 200)
                Toast.makeText(activity, "crash!!", Toast.LENGTH_SHORT).show()
                val isGameOver = livesManager.loseLife()
                if (isGameOver) {
                    showGameOver()
                }
            }
        }
    }

    private fun checkCoinCollection(coinsAtBottom: List<Pair<Int, Int>>) {
        for ((_, c) in coinsAtBottom) {
            if (c == playerController.position) {
                val currentCoins = coinController.coinCollected()
                Toast.makeText(activity, "Coins: $currentCoins", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun showGameOver() {
        gameOver = true
        stop()
        val finalCoins = coinController.getCoinsCount()
        binding.tvGameOver.visibility = View.VISIBLE
        Vibration.vibrate(activity, 600)

    }
}
