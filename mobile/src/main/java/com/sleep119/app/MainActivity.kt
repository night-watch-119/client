package com.sleep119.app

import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.*

class MainActivity : AppCompatActivity(), MessageClient.OnMessageReceivedListener{

    private lateinit var latestHealthInfo: JSONObject

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null // 현재 위치를 가져오기 위한 변수
    lateinit var mLastLocation: Location // 위치 값을 가지고 있는 객체
    lateinit var mLocationRequest: LocationRequest

    lateinit var userAddress: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sendSMSPermissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS)
        if (sendSMSPermissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.SEND_SMS)) {
                Toast.makeText(this, "권한이 필요합니다", Toast.LENGTH_SHORT).show()
            }
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SEND_SMS), 1)
        }

        val locationPermissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
        if (locationPermissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "권한이 필요합니다", Toast.LENGTH_SHORT).show()
            }
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        mLocationRequest =  LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        startLocationUpdates()

        Wearable.getMessageClient(this).addListener(this)
        // 하단 탭이 눌렸을 때 화면을 전환하기 위해선 이벤트 처리하기 위해 BottomNavigationView 객체 생성
        var bnv_main = findViewById<BottomNavigationView>(R.id.bnv_main)

        // OnNavigationItemSelectedListener를 통해 탭 아이템 선택 시 이벤트를 처리
        // navi_menu.xml 에서 설정했던 각 아이템들의 id를 통해 알맞은 프래그먼트로 변경하게 한다.
        bnv_main.run { setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.first -> {
                    val homeFragment = HomeFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.fl_container, homeFragment).commit()
                }
                R.id.second -> {
                    val analyticsFragment = AnalyticsFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.fl_container, analyticsFragment).commit()
                }
                R.id.third -> {
                    val userFragment = UserFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.fl_container, userFragment).commit()
                }
                R.id.fourth -> {
                    val settingFragment = SettingFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.fl_container, settingFragment).commit()
                }
            }
            true
        }
            selectedItemId = R.id.first
        }
    }

    // 함수 실행을 시작하는 메서드

    // Activity 생명주기에 따라 함수 실행 시작 및 중지
    override fun onResume() {
        super.onResume()
        Wearable.getMessageClient(this).addListener(this)
    }

    override fun onPause() {
        super.onPause()
        Wearable.getMessageClient(this).removeListener(this)
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        try {
            if(messageEvent.path == "live_count"){
                println(">>> live_count message")
            }
            val s1 = String(messageEvent.data, StandardCharsets.UTF_8)
            val healthData = JSONObject(s1)
            val heartRate = healthData.get("heart_rate") as Int
            val oxygenSaturation = healthData.get("oxygen_saturation") as Int

            if(oxygenSaturation < 80) {
                val permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS)
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.SEND_SMS)) {
                        Toast.makeText(this, "권한이 필요합니다", Toast.LENGTH_SHORT).show()
                    }
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SEND_SMS), 1)
                } else {
                    ProtectorService.getProtectorOfUser(this, 1){res->
                        val geocoder = Geocoder(this, Locale.KOREA)
                        val latitude = mLastLocation.latitude
                        val longitude = mLastLocation.longitude

                        val addrList =
                            geocoder.getFromLocation(latitude, longitude, 1)
                        for (addr in addrList!!) {
                            val splitedAddr = addr.getAddressLine(0).split(" ")
                            userAddress = splitedAddr
                        }
                        // 보호자가 있는 경우
                        if(res.length() > 0){
                            for(i in 0 until res.length()){
                                val healthData = res.getJSONObject(i)
                                Log.i("i", healthData.toString())
                                val sms = this.getSystemService(SmsManager::class.java)
                                sms.sendTextMessage(healthData.getString("telno"), null,"[보호자 응급 알림]\n환자의 현재 상태\n심박수:$heartRate\n산소포화도:$oxygenSaturation%", null, null)
                            }
                        // 보호자가 없는 경우
                        } else {
                            val sms = this.getSystemService(SmsManager::class.java)
                            sms?.sendTextMessage("010-9544-8995", null, "119응급신고\n010-9544-8995\n$latitude;$longitude\n${userAddress[1]}${userAddress[2]}${userAddress[3]}${userAddress[4]}", null, null)
                        }
                    }
                }
            }

            HealthInfoService.addHealthInfo(this, heartRate,"$oxygenSaturation%", 1){
                res -> latestHealthInfo = res
            }
        } catch (e: Exception) {
            println(
                ">>> Handled in sending message back to the sending node"
            )
            e.printStackTrace()
        }
    }
    private fun startLocationUpdates() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        Looper.myLooper()?.let {
            mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback,
                it
            )
        }
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            mLastLocation = locationResult.lastLocation
        }
    }
}
