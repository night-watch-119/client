package com.sleep119.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton

class GuardianAdd : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guardian_add)
        val imageButton3 = findViewById<ImageButton>(R.id.imageButton5)

        imageButton3.setOnClickListener {
            finish()
        }
    }

}