package com.example.project_10208

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.abs

class TiltDetector(context: Context, private val onTilt: (Boolean) -> Unit) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var lastUpdate: Long = 0
    private val MOVE_DELAY = 400L

    fun start() {
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME)
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val x = it.values[0]
            val currentTime = System.currentTimeMillis()

            if (currentTime - lastUpdate > MOVE_DELAY) {
                if (x > 3.0) {
                    onTilt(false) // Left
                    lastUpdate = currentTime
                } else if (x < -3.0) {
                    onTilt(true)
                    lastUpdate = currentTime
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}