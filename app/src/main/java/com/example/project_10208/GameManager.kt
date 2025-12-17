package com.example.project_10208

import android.view.View
import android.widget.LinearLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project_10208.databinding.ActivityMainBinding
import java.util.Random
import kotlin.ranges.downTo

class GameManager(private val activity: AppCompatActivity) {

    private lateinit var binding: ActivityMainBinding
    private val grid: Array<Array<LinearLayout>>
    private val playerController: PlayerController
    private val timer: GameTimer

    private var lives = 3
    private var gameOver = false

    init {
        grid = Array(5) { r ->
            Array(3) { c ->
                val id = activity.resources.getIdentifier(
                    "linCell_${r}_${c}",
                    "id",
                    activity.packageName
                )
                activity.findViewById<LinearLayout>(id)
            }
        }

        val playerCells = Array(3) { i ->
            val id = activity.resources.getIdentifier(
                "linPlayerCell$i",
                "id",
                activity.packageName
            )
            activity.findViewById<LinearLayout>(id)
        }

        playerController = PlayerController(activity, playerCells)
        timer = GameTimer { updateGame() }
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
        spawnInitialMeteors()
        updateHeartsUI()
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
            moveMeteorsDown()
            spawnMeteorsOnePerRow()
        }
    }

    private fun spawnInitialMeteors() {
        spawnMeteorsOnePerRow()
    }

    private fun spawnMeteorsOnePerRow() {
        val rand = Random()
        for (r in 0..3) {
            val rowHasMeteor = grid[r].any { it.childCount > 0 }
            if (!rowHasMeteor) {
                val c = rand.nextInt(3)
                addMeteor(r, c)
            }
        }
    }

    private fun addMeteor(r: Int, c: Int) {
        val meteor = ImageView(activity)
        meteor.setImageResource(R.drawable.img_meteor)
        grid[r][c].addView(meteor)
    }

    private fun moveMeteorsDown() {
        for (r in 4 downTo 0) {
            for (c in 0..2) {
                if (grid[r][c].childCount > 0) {
                    val meteor = grid[r][c].getChildAt(0)
                    grid[r][c].removeAllViews()

                    if (r == 4 && c == playerController.position) {
                        lives--
                        updateHeartsUI()
                        if (lives <= 0) {
                            gameOver = true
                            showGameOver()
                        }
                    } else if (r < 4) {
                        grid[r + 1][c].addView(meteor)
                    }
                }
            }
        }
    }


    private fun updateHeartsUI() {
        val hearts = arrayOf(
            binding.imgHeart1,
            binding.imgHeart2,
            binding.imgHeart3
        )
        for (i in hearts.indices) {
            if (i < lives) hearts[i].setImageResource(R.drawable.ic_heart)
            else hearts[i].setImageResource(R.drawable.ic_heart_empty)
        }
    }

    private fun showGameOver() {
        gameOver = true
        stop()
        binding.tvGameOver.visibility = View.VISIBLE
    }
}
