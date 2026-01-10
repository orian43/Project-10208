package com.example.project_10208

import android.content.Context
import android.widget.LinearLayout
import android.widget.ImageView

class PlayerController(
    private val context: Context,
    private val playerCells: Array<LinearLayout>
) {

    var position = playerCells.size / 2
        private set

    fun placePlayer() {
        playerCells.forEach { it.removeAllViews() }
        val ship = ImageView(context)
        ship.setImageResource(R.drawable.img_spaceship)
        playerCells[position].addView(ship)
    }

    fun moveLeft() {
        if (position > 0) {
            position--
            placePlayer()
        }
    }

    fun moveRight() {
        if (position < playerCells.size - 1) {
            position++
            placePlayer()
        }
    }
}
