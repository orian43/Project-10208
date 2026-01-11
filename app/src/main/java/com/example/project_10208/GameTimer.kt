package com.example.project_10208

import android.os.Handler
import android.os.Looper

class GameTimer(
    private var delay: Long = 1000, //var instead of val to allow modification
    private val onTick: () -> Unit
) {
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            onTick()
            handler.postDelayed(this, delay)
        }
    }

    fun setDelay(newDelay: Long) {
        this.delay = newDelay
    }
    fun start() {
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, delay)
    }

    fun stop() {
        handler.removeCallbacks(runnable)
    }
}
