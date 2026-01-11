package com.example.project_10208

import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import java.util.Random

class CoinController(
    private val activity: AppCompatActivity,
    private val grid: Array<Array<LinearLayout>>,
    private val onCoinCollected: (Int) -> Unit,
    private val placeCoin: (LinearLayout) -> Unit
) {
    private val random = Random()
    private var coinsCount = 0

    fun spawnCoin() {
        if (random.nextInt(100) < 30) {
            val r = 0
            val c = random.nextInt(GameConfig.COLS)
            val cell = grid[r][c]

            if (cell.childCount == 0) {
                placeCoin(cell)
            }
        }
    }

    fun moveCoinsDown(): List<Pair<Int, Int>> {
        val coinsAtBottom = mutableListOf<Pair<Int, Int>>()

        for (r in GameConfig.ROWS - 1 downTo 0) {
            for (c in 0 until GameConfig.COLS) {
                val cell = grid[r][c]

                if (cell.childCount > 0 && cell.tag == "COIN") {
                    val view = cell.getChildAt(0)
                    cell.removeView(view)

                    if (r + 1 < GameConfig.ROWS) {
                        val nextCell = grid[r + 1][c]
                        if (nextCell.childCount == 0) {
                            nextCell.addView(view)
                            nextCell.tag = "COIN"
                        }
                    } else {
                        coinsAtBottom.add(Pair(r, c))
                    }
                }
            }
        }
        return coinsAtBottom
    }

    fun coinCollected() {
        coinsCount++
        onCoinCollected(coinsCount)
    }

    fun getCoinsCount(): Int {
        return coinsCount
    }

    fun reset() {
        coinsCount = 0
        onCoinCollected(0)
        for (row in grid) {
            for (cell in row) {
                if (cell.tag == "COIN") {
                    cell.removeAllViews()
                    cell.tag = null
                }
            }
        }
    }
}