package com.example.project_10208

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton

class FragmentList : Fragment() {

    private lateinit var mainLayout: LinearLayout
    var callback: Callback_HighScoreClicked? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        mainLayout = view.findViewById(R.id.main_layout_list)

        initViews()
        return view
    }

    private fun initViews() {
        val scoreManager = ScoreManager(requireContext())
        val scores = scoreManager.getAllScores()

        scores.forEachIndexed { index, score ->
            val button = MaterialButton(requireContext())
            button.text = "${index + 1}. Score: ${score.score}"
            button.setOnClickListener {
                callback?.onScoreClicked(score.lat, score.lon)
            }
            mainLayout.addView(button)
        }
    }
}