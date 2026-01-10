package com.example.project_10208


import android.app.Activity
import android.widget.ImageView

class LivesManager(private val activity: Activity) {

    private var lives = GameConfig.INITIAL_LIVES

    private val hearts: Array<ImageView> = arrayOf(
        activity.findViewById(R.id.imgHeart1),
        activity.findViewById(R.id.imgHeart2),
        activity.findViewById(R.id.imgHeart3)
    )

    fun reset() {
        lives = GameConfig.INITIAL_LIVES
        updateUI()
    }


    fun loseLife(): Boolean {
        lives--
        updateUI()
        return lives <= 0
    }

    private fun updateUI() {
        for (i in hearts.indices) {
            if (i < lives) {
                hearts[i].setImageResource(R.drawable.ic_heart)
            } else {
                hearts[i].setImageResource(R.drawable.ic_heart_empty)
            }
        }
    }
}
