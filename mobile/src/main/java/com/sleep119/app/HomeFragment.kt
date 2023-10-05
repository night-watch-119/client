package com.sleep119.app

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.json.JSONObject



class HomeFragment(): Fragment() {

    private lateinit var latestHealthInfo: JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val homeSleepScore = view.findViewById<TextView>(R.id.home_sleep_score)

        HealthInfoService.getHealthInfoLatestOne(this, 1) { res ->
            latestHealthInfo = res
            updateHomeSleepScore(homeSleepScore)
        }
        return view
    }

    private fun updateHomeSleepScore(homeSleepScore: TextView) {
        latestHealthInfo?.let {
            val oxygenSaturation = it.getString("oxygen_saturation")
            val value = oxygenSaturation.substring(0, oxygenSaturation.length - 1)
            homeSleepScore.text = "$value 점"
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(latestHealthInfo: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}