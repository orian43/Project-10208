package com.example.project_10208

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import java.util.Random

class MeteorController(
    private val context: Context,
    private val grid: Array<Array<LinearLayout>>
) {

    private var spawnToggle = false
    private val random = Random()

    fun spawnInitialMeteors() {
        spawnMeteorsOnePerRow()
    }

    fun spawnMeteorsOnePerRow() {
        val r = 0
        val rowHasMeteor = grid[r].any { it.childCount > 0 }

        if (!rowHasMeteor && spawnToggle) {
            val c = random.nextInt(grid[r].size)
            addMeteor(r, c)
        }
        spawnToggle = !spawnToggle
    }

    private fun addMeteor(r: Int, c: Int) {
        val meteor = ImageView(context)
        meteor.setImageResource(R.drawable.img_meteor)
        grid[r][c].addView(meteor)
    }

    fun moveMeteorsDown(): List<Pair<Int, Int>> {
        val meteorsAtBottom = mutableListOf<Pair<Int, Int>>()

        for (r in 4 downTo 0) {
            for (c in grid[r].indices) {
                if (grid[r][c].childCount > 0) {
                    val meteor = grid[r][c].getChildAt(0)
                    grid[r][c].removeAllViews()

                    if (r == 4) {
                        meteorsAtBottom.add(Pair(r, c))
                    } else {
                        grid[r + 1][c].addView(meteor)
                    }
                }
            }
        }
        return meteorsAtBottom
    }
}
