package com.example.project_10208

import android.app.Activity
import android.widget.TextView

class DistanceManager(private val activity: Activity) {

    private var distance = 0
    private val tvDistance: TextView = activity.findViewById(R.id.tvDistance)

    fun reset() {
        distance = 0
        updateUI()
    }
    fun getDistance(): Int {
        return distance
    }
    fun increaseDistance() {
        distance++
        updateUI()
    }

    private fun updateUI() {
        tvDistance.text = "Distance: $distance m"
    }
}