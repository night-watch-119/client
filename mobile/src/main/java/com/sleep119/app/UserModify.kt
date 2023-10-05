package com.sleep119.app

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import java.lang.Exception

class UserModify : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_modify)

        val builder = AlertDialog.Builder(this)

        val backBtn = findViewById<ImageButton>(R.id.backBtn)
        val successBtn = findViewById<Button>(R.id.successBtn)

        val typeOfBloodEditText = findViewById<EditText>(R.id.typeOfBlood)
        val phoneCallEditText = findViewById<EditText>(R.id.phoneCall)

        backBtn.setOnClickListener {
            finish()
        }

        successBtn.setOnClickListener {
            val typeOfBloodVal = typeOfBloodEditText.text.toString()
            val phoneCallVal = phoneCallEditText.text.toString()

            UserService.updateUser(this, 1, typeOfBloodVal, phoneCallVal, { res ->
                builder.setCancelable(false) // 다이얼로그를 닫지 못하도록 설정
                if (res.has("error")) {
                    builder.setTitle("요청 실패")
                    builder.setMessage("다시 시도해주세요.")
                    builder.setPositiveButton("확인") { dialog, _ ->
                        dialog.dismiss()
                    }
                } else {
                    builder.setTitle("요청 성공")
                    builder.setMessage("변경 요청이 성공했습니다.")
                    builder.setPositiveButton("확인") { dialog, _ ->
                        dialog.dismiss()
                        finish()
                    }
                }
                val alertDialog = builder.create()
                alertDialog.show() // AlertDialog를 화면에 표시
            })
        }
    }
}

