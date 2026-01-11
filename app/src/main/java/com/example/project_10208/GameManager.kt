package com.example.project_10208

import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

interface GameCallback {
    fun onUpdateDistance(distance: Int)
    fun onUpdateCoins(count: Int)
    fun onGameOver(distance: Int, coins: Int)
}

class GameManager(
    private val activity: AppCompatActivity,
    private val callback: GameCallback,
    private val scoreManager: ScoreManager
) {

    private val grid: Array<Array<LinearLayout>>
    private val playerController: PlayerController
    private val timer: GameTimer
    private val meteorController: MeteorController
    private val livesManager: LivesManager
    private val distanceManager: DistanceManager
    private val coinController: CoinController

    private var tiltDetector: TiltDetector? = null
    private var lastUpdate: Long = 0
    private val MOVE_DELAY = 400L

    private var gameMode: String = GameConfig.MODE_BUTTONS
    private var gameSpeed: Long = GameConfig.SPEED_SLOW

    private var gameOver = false

    private var isRunning = false

    init {
        SoundEffectPlayer.init(activity)
        try {
            SoundEffectPlayer.load(activity, R.raw.snd_crash)
            SoundEffectPlayer.load(activity, R.raw.snd_coin)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        grid = Array(GameConfig.ROWS) { r ->
            Array(GameConfig.COLS) { c ->
                val id = activity.resources.getIdentifier("linCell_${r}_${c}", "id", activity.packageName)
                activity.findViewById(id)
            }
        }

        val playerCells = Array(GameConfig.COLS) { i ->
            val id = activity.resources.getIdentifier("linPlayerCell$i", "id", activity.packageName)
            activity.findViewById<LinearLayout>(id)
        }

        playerController = PlayerController(activity, playerCells)
        meteorController = MeteorController(activity, grid)
        livesManager = LivesManager(activity)

        timer = GameTimer { updateGame() }

        coinController = CoinController(
            activity,
            grid,
            onCoinCollected = { newCount -> callback.onUpdateCoins(newCount) },
            placeCoin = { cell ->
                cell.gravity = android.view.Gravity.CENTER
                val coinView = android.widget.ImageView(activity)
                coinView.setImageResource(R.drawable.ic_coin)
                coinView.scaleType = android.widget.ImageView.ScaleType.FIT_CENTER
                coinView.adjustViewBounds = true
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                coinView.layoutParams = params
                cell.addView(coinView)
                cell.tag = "COIN"
            }
        )

        distanceManager = DistanceManager { newDistance ->
            callback.onUpdateDistance(newDistance)
        }
    }

    fun setup(mode: String, speed: Long) {
        this.gameMode = mode
        this.gameSpeed = speed
        timer.setDelay(gameSpeed)
        setupControls()
    }

    private fun setupControls() {
        if (gameMode == GameConfig.MODE_SENSORS) {
            tiltDetector = TiltDetector(activity, object : TiltCallback {
                override fun onSensorEvent(x: Float, y: Float, z: Float) {
                    checkSensorLogic(x, y)
                }
            })
        }
    }

    private fun checkSensorLogic(x: Float, y: Float) {
        if (!isRunning) return

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

        val newSpeed = when {
            y < -3.0 -> GameConfig.SPEED_FAST
            y > 3.0 -> GameConfig.SPEED_SLOW + 200
            else -> GameConfig.SPEED_SLOW
        }

        if (gameSpeed != newSpeed) {
            gameSpeed = newSpeed
            timer.setDelay(gameSpeed)
        }
    }

    fun initGame() {
        playerController.placePlayer()
        meteorController.spawnInitialMeteors()
        livesManager.reset()
        distanceManager.reset()
        coinController.reset()
        gameOver = false
    }

    fun start() {
        isRunning = true
        timer.start()
        tiltDetector?.start()
    }

    fun stop() {
        isRunning = false
        timer.stop()
        tiltDetector?.stop()
    }

    fun isGameRunning(): Boolean {
        return !gameOver && isRunning
    }

    fun movePlayerLeft() {
        if (isRunning && !gameOver) playerController.moveLeft()
    }

    fun movePlayerRight() {
        if (isRunning && !gameOver) playerController.moveRight()
    }

    private fun updateGame() {
        if (gameOver || !isRunning) return

        distanceManager.increaseDistance()
        val meteorsAtBottom = meteorController.moveMeteorsDown()
        val coinsAtBottom = coinController.moveCoinsDown()

        if (isRunning) {
            checkCollisions(meteorsAtBottom)
            checkCoinCollection(coinsAtBottom)
            meteorController.spawnMeteorsOnePerRow()
            coinController.spawnCoin()
        }
    }

    private fun checkCollisions(meteorsAtBottom: List<Pair<Int, Int>>) {
        if (!isRunning) return

        for ((_, c) in meteorsAtBottom) {
            if (c == playerController.position) {
                SoundEffectPlayer.play(R.raw.snd_crash)
                Vibration.vibrate(activity, 200)

                if (!activity.isFinishing && !activity.isDestroyed) {
                    Toast.makeText(activity, "Crash!!", Toast.LENGTH_SHORT).show()
                }

                val isGameOver = livesManager.loseLife()
                if (isGameOver) {
                    showGameOver()
                }
            }
        }
    }

    private fun checkCoinCollection(coinsAtBottom: List<Pair<Int, Int>>) {
        if (!isRunning) return

        for ((_, c) in coinsAtBottom) {
            if (c == playerController.position) {
                SoundEffectPlayer.play(R.raw.snd_coin)
                coinController.coinCollected()
            }
        }
    }

    private fun showGameOver() {
        gameOver = true
        stop()

        val finalDistance = distanceManager.getDistance()
        val finalCoins = coinController.getCoinsCount()

        scoreManager.saveScore(finalDistance, finalCoins, 32.0853, 34.8854)
        Vibration.vibrate(activity, 600)

        callback.onGameOver(finalDistance, finalCoins)
    }
}