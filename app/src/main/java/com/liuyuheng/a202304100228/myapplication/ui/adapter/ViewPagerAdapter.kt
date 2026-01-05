package com.liuyuheng.a202304100228.myapplication.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.liuyuheng.a202304100228.myapplication.ui.decision.DecisionFragment
import com.liuyuheng.a202304100228.myapplication.ui.diary.DiaryFragment
import com.liuyuheng.a202304100228.myapplication.ui.map.MapFragment
import com.liuyuheng.a202304100228.myapplication.ui.recipe.RecipeFragment
import com.liuyuheng.a202304100228.myapplication.ui.restaurant.RestaurantFragment
import android.util.Log

class ViewPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    companion object {
        private const val TAG = "ViewPagerAdapter"
    }
    
    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {
        try {
            Log.d(TAG, "createFragment: 创建Fragment，位置 $position")
            return when (position) {
                0 -> DecisionFragment() // 决策转盘
                1 -> RestaurantFragment() // 餐厅推荐
                2 -> MapFragment() // 地图餐饮
                3 -> RecipeFragment() // 菜谱推荐
                4 -> DiaryFragment() // 探店日记
                else -> DecisionFragment()
            }
        } catch (e: Exception) {
            Log.e(TAG, "createFragment错误", e)
            e.printStackTrace()
            return DecisionFragment()
        }
    }
}