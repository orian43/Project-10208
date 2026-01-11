package com.example.project_10208

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import java.util.Random

class CoinController(
    private val context: Context,
    private val grid: Array<Array<LinearLayout>>,
    //private val onCoinUpdate: (Int) -> Unit

    ) {
    private val random = Random()
    private var coinsCount = 0

    fun reset() {
        coinsCount = 0
        //onCoinUpdate(coinsCount)

    }

    fun coinCollected(): Int {
        coinsCount++
       // onCoinUpdate(coinsCount)
        return coinsCount
    }
    fun getCoinsCount(): Int {
        return coinsCount
    }

    fun spawnCoin() {
        //coin is only created 30% of the times the function is called, so as not to clutter the screen
        if (random.nextInt(100) < 30) {
            val r = 0
            val c = random.nextInt(GameConfig.COLS)
            //Check if the cell is completely empty (there is no meteor in it)
            if (grid[r][c].childCount == 0) {
                addCoin(r, c)
            }
        }
    }

    private fun addCoin(r: Int, c: Int) {
        val coin = ImageView(context)
        coin.setImageResource(R.drawable.img_coin)
        coin.tag = "COIN"
        grid[r][c].addView(coin)
    }

    fun moveCoinsDown(): List<Pair<Int, Int>> {
        val coinsAtBottom = mutableListOf<Pair<Int, Int>>()

        for (r in GameConfig.ROWS - 1 downTo 0) {
            for (c in grid[r].indices) {
                // Only move if it's really a coin
                if (grid[r][c].childCount > 0 && grid[r][c].getChildAt(0).tag == "COIN") {
                    val coin = grid[r][c].getChildAt(0)
                    grid[r][c].removeAllViews()

                    if (r == GameConfig.ROWS - 1) {
                        coinsAtBottom.add(Pair(r, c))
                    } else {
                        grid[r + 1][c].addView(coin)
                    }
                }
            }
        }
        return coinsAtBottom
    }
}