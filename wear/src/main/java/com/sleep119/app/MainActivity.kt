package com.sleep119.app

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import com.sleep119.app.databinding.ActivityMainBinding

class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        String.format(resources.getString(R.string.warning_index_value), 86)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val warningIndexValue = findViewById<TextView>(R.id.warningIndexValue)
        warningIndexValue.text = 86.toString()
    }
}