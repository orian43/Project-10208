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
    private val scoreManager: ScoreManager


    private var tiltDetector: TiltDetector? = null
    private var lastUpdate: Long = 0
    private val MOVE_DELAY = 400L

    private var gameMode: String = GameConfig.MODE_BUTTONS
    private var gameSpeed: Long = GameConfig.SPEED_SLOW
    private var gameOver = false

    init {

        scoreManager = ScoreManager(activity)


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
        timer = GameTimer { updateGame() }
        coinController = CoinController(activity, grid) { newCount -> updateCoinText(newCount) }
        distanceManager = DistanceManager { newDistance ->
            updateDistanceText(newDistance)
        }
    }

    fun setup(binding: ActivityMainBinding, mode: String, speed: Long) {
        this.binding = binding
        this.gameMode = mode
        this.gameSpeed = speed

        timer.setDelay(gameSpeed)


        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupControls()
    }

    private fun setupControls() {
        if (gameMode == GameConfig.MODE_SENSORS) {
            // Sensor Mode: Hiding Buttons and Enabling Sensor
            binding.btnLeft.visibility = View.INVISIBLE
            binding.btnRight.visibility = View.INVISIBLE

            tiltDetector = TiltDetector(activity, object : TiltCallback {
                override fun onSensorEvent(x: Float, y: Float, z: Float) {
                    checkSensorLogic(x)
                }
            })
        } else {
            // Button mode: Displaying buttons and defining clicks
            binding.btnLeft.visibility = View.VISIBLE
            binding.btnRight.visibility = View.VISIBLE
            binding.btnLeft.setOnClickListener { movePlayerLeft() }
            binding.btnRight.setOnClickListener { movePlayerRight() }
        }
    }


    private fun checkSensorLogic(x: Float) {
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastUpdate > MOVE_DELAY) {
            if (x > 3.0) {
                movePlayerLeft()
                lastUpdate = currentTime
            } else if (x < -3.0) {
                movePlayerRight()
                lastUpdate = currentTime
            }
        }
    }

    fun initGame() {
        playerController.placePlayer()
        meteorController.spawnInitialMeteors()
        livesManager.reset()
        distanceManager.reset()
        coinController.reset()
        gameOver = false

        if (::binding.isInitialized) {
            binding.tvGameOver.visibility = View.GONE
        }
    }

    fun start() {
        timer.start()
        tiltDetector?.start()
    }

    fun stop() {
        timer.stop()
        tiltDetector?.stop()
    }

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
                Toast.makeText(activity, "Crash!!", Toast.LENGTH_SHORT).show()
                val isGameOver = livesManager.loseLife()
                if (isGameOver) {
                    showGameOver()
                }
            }
        }
    }

    private fun updateDistanceText(distance: Int) {
        if (::binding.isInitialized) {
            binding.tvDistance.text = "Distance: $distance m"
        }
    }

    private fun updateCoinText(count: Int) {
        if (::binding.isInitialized) {
            binding.tvCoins.text = "Coins: $count"
        }
    }

    private fun checkCoinCollection(coinsAtBottom: List<Pair<Int, Int>>) {
        for ((_, c) in coinsAtBottom) {
            if (c == playerController.position) {
                coinController.coinCollected()
            }
        }
    }

    private fun showGameOver() {
        gameOver = true
        stop()

        val finalDistance = distanceManager.getDistance()
        val finalCoins = coinController.getCoinsCount()

        // Saving data in the record table
        scoreManager.saveScore(finalDistance, finalCoins, 32.0853, 34.8854)

        binding.tvGameOver.visibility = View.VISIBLE
        Vibration.vibrate(activity, 600)

        Toast.makeText(
            activity,
            "Game Over! Distance: $finalDistance m, Coins: $finalCoins",
            Toast.LENGTH_LONG
        ).show()
    }
}