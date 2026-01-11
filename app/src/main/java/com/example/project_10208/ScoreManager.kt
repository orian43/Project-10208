package com.example.project_10208

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ScoreManager(private val context: Context) {

    private val SP_FILE = "MY_SP"
    private val SP_KEY_SCORES = "SCORES"
    private val gson = Gson()

    fun saveScore(newScore: Int, newCoins: Int, lat: Double, lon: Double) {
        val scores = getAllScores().toMutableList()
        scores.add(Score(newScore, newCoins, lat, lon))
        
        // Sort in descending order and keep only the top 10
        scores.sortByDescending { it.score }
        if (scores.size > 10) {
            scores.removeAt(scores.size - 1)
        }

        saveListToSP(scores)
    }

    fun getAllScores(): List<Score> {
        val sharedPreferences = context.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE)
        val json = sharedPreferences.getString(SP_KEY_SCORES, null)
        return if (json == null) {
            emptyList()
        } else {
            val type = object : TypeToken<List<Score>>() {}.type
            gson.fromJson(json, type)
        }
    }

    private fun saveListToSP(list: List<Score>) {
        val json = gson.toJson(list)
        val sharedPreferences = context.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(SP_KEY_SCORES, json).apply()
    }
}