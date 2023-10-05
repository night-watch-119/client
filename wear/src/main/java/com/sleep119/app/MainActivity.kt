package com.sleep119.app

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper

import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope

import androidx.wear.remote.interactions.RemoteActivityHelper
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.util.concurrent.CancellationException

import java.util.concurrent.Executors
import kotlin.random.Random

class MainActivity : AppCompatActivity(){

    private lateinit var warningIndexValue: TextView

    private val SEND_SMS_CAPABILITY_NAME = "send_sms"
    private val SEND_SMS_MESSAGE_PATH = "/send_sms"

    private suspend fun sendLiveCountData(str:String){
        var ctxt = this
        try {
            var nodes = Wearable.getNodeClient(ctxt).connectedNodes.await()
            for (n in nodes) {
                if(n.isNearby) {
                    println(">>> send to nearby node node: ${n.displayName}")

                    Wearable.getMessageClient(this)
                        .sendMessage(n.id, SEND_SMS_MESSAGE_PATH, str.toByteArray()).await()
                }
            }
        } catch (cancellationException: CancellationException) {
            Log.d("d", ">>> sendLiveCountData cancellationException:.${cancellationException.localizedMessage}")
            // Request was cancelled normally
        } catch (throwable: Throwable) {
            Log.d("d", ">>> sendLiveCountData failed:.${throwable.localizedMessage}")
        }
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            Log.i("i",permissions.toString())
            if (permissions.values.all { it }) {
                Log.i("i", "권한 승인")
            } else {
                Log.i("i", "권한 거부")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener() {
            println("!23")
            val remoteActivityHelper =
                RemoteActivityHelper(this, Executors.newSingleThreadExecutor())
            val uri1: Uri = Uri.parse("itms-watch://mainactivity")
            val intentAndroid: Intent = Intent(Intent.ACTION_VIEW)
                .addCategory(Intent.CATEGORY_BROWSABLE)
                .setData(uri1)
            remoteActivityHelper.startRemoteActivity(intentAndroid, null)
        }

        val warningIndexValue = findViewById<TextView>(R.id.warningIndexValue)
        warningIndexValue.text = 86.toString()

        val bs = android.Manifest.permission.BODY_SENSORS
        val bsg = android.Manifest.permission.BODY_SENSORS_BACKGROUND

        if (checkSelfPermission(bs) != PackageManager.PERMISSION_GRANTED){
            permissionLauncher.launch(arrayOf(bs))
        }

        if(checkSelfPermission(bs) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(bsg) == PackageManager.PERMISSION_GRANTED){
            Log.i("i", "통과")
        }
    }

    private val handler = Handler(Looper.getMainLooper())

    // 함수 실행 간격 (10초)
    private val intervalMillis: Long = 10000

    // 실행할 함수
    private val runnable: Runnable = object : Runnable {
        override fun run() {
            val randomHeartRate = Random.nextInt(70, 110)
            val randomOxygenSaturation = Random.nextInt(60, 100)

            warningIndexValue = findViewById<TextView>(R.id.warningIndexValue)
            warningIndexValue.text = randomOxygenSaturation.toString()

            lifecycleScope.launch() {
                val healthData = JSONObject()
                healthData.put("heart_rate", randomHeartRate)
                healthData.put("oxygen_saturation", randomOxygenSaturation)

                sendLiveCountData(healthData.toString())
            }

            // 다음 호출을 예약
            handler.postDelayed(this, intervalMillis)
        }
    }

    // 함수 실행을 시작하는 메서드
    private fun startFunctionExecution() {
        // 처음 호출
        handler.postDelayed(runnable, intervalMillis)
    }

    // 함수 실행을 중지하는 메서드
    private fun stopFunctionExecution() {
        handler.removeCallbacks(runnable)
    }

    // 예시: Activity 생명주기에 따라 함수 실행 시작 및 중지
    override fun onResume() {
        super.onResume()
        startFunctionExecution()
    }

    override fun onPause() {
        super.onPause()
        stopFunctionExecution()
    }
}