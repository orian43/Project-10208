package com.example.project_10208

import android.app.Activity
import android.widget.TextView

class DistanceManager(
    private val onDistanceUpdate: (Int) -> Unit
) {

    private var distance = 0

    fun reset() {
        distance = 0
        onDistanceUpdate(distance)
    }
    fun getDistance(): Int {
        return distance
    }
    fun increaseDistance() {
        distance++
        onDistanceUpdate(distance)
    }


}