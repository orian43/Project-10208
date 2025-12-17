package com.example.project_10208

import android.os.Handler
import android.os.Looper

class GameTimer(
    private val delay: Long = 1000,
    private val onTick: () -> Unit
) {
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            onTick()
            handler.postDelayed(this, delay)
        }
    }

    fun start() {
        handler.postDelayed(runnable, delay)
    }

    fun stop() {
        handler.removeCallbacks(runnable)
    }
}
