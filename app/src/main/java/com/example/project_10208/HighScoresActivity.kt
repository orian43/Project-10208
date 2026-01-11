package com.example.project_10208

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.FrameLayout

class HighScoresActivity : AppCompatActivity(), Callback_HighScoreClicked {

    private lateinit var fragmentList: FragmentList
    private lateinit var fragmentMap: FragmentMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_high_scores)

        fragmentList = FragmentList()
        fragmentMap = FragmentMap()

       fragmentList.callback = this


        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_list, fragmentList)
            .replace(R.id.frame_map, fragmentMap)
            .commit()
    }

    // The implementation of the interface: When the list notifies of a click, the activity updates the map
    override fun onScoreClicked(lat: Double, lon: Double) {
        fragmentMap.zoom(lat, lon)
    }
}