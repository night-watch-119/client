package com.sleep119.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 하단 탭이 눌렸을 때 화면을 전환하기 위해선 이벤트 처리하기 위해 BottomNavigationView 객체 생성
        var bnv_main = findViewById<BottomNavigationView>(R.id.bnv_main)

        // OnNavigationItemSelectedListener를 통해 탭 아이템 선택 시 이벤트를 처리
        // navi_menu.xml 에서 설정했던 각 아이템들의 id를 통해 알맞은 프래그먼트로 변경하게 한다.
        bnv_main.run { setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.first -> {
                    // 다른 프래그먼트 화면으로 이동하는 기능
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
}