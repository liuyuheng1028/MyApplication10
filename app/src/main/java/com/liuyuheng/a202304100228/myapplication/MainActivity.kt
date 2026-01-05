package com.liuyuheng.a202304100228.myapplication

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.liuyuheng.a202304100228.myapplication.ui.adapter.ViewPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.amap.api.location.AMapLocationClient

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            Log.d(TAG, "onCreate: 开始创建MainActivity")
            
            Log.d(TAG, "onCreate: 设置高德地图隐私合规")
            AMapLocationClient.updatePrivacyShow(this, true, true)
            AMapLocationClient.updatePrivacyAgree(this, true)
            Log.d(TAG, "onCreate: 高德地图隐私合规设置完成")
            
            setContentView(R.layout.activity_main)
            Log.d(TAG, "onCreate: setContentView完成")

            val viewPager: ViewPager2 = findViewById(R.id.view_pager)
            val bottomNav: BottomNavigationView = findViewById(R.id.bottom_nav)
            Log.d(TAG, "onCreate: findViewById完成")

            // 设置ViewPager适配器
            viewPager.adapter = ViewPagerAdapter(this)
            Log.d(TAG, "onCreate: ViewPagerAdapter设置完成")

            // 底部导航与ViewPager联动
            bottomNav.setOnItemSelectedListener { item ->
                try {
                    Log.d(TAG, "底部导航选择: ${item.itemId}")
                    when (item.itemId) {
                        R.id.nav_decision -> viewPager.currentItem = 0
                        R.id.nav_restaurant -> viewPager.currentItem = 1
                        R.id.nav_map -> viewPager.currentItem = 2
                        R.id.nav_recipe -> viewPager.currentItem = 3
                        R.id.nav_diary -> viewPager.currentItem = 4
                        else -> {}
                    }
                    true
                } catch (e: Exception) {
                    Log.e(TAG, "底部导航选择错误", e)
                    e.printStackTrace()
                    false
                }
            }

            // ViewPager滑动联动底部导航
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    try {
                        super.onPageSelected(position)
                        Log.d(TAG, "onPageSelected: 位置 $position")
                        if (position < bottomNav.menu.size()) {
                            bottomNav.menu.getItem(position).isChecked = true
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "onPageSelected错误", e)
                        e.printStackTrace()
                    }
                }
            })
            Log.d(TAG, "onCreate: MainActivity创建完成")
        } catch (e: Exception) {
            Log.e(TAG, "onCreate错误", e)
            e.printStackTrace()
        }
    }
}