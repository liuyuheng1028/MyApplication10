package com.liuyuheng.a202304100228.myapplication.ui.diary

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.liuyuheng.a202304100228.myapplication.database.AppDatabase
import com.liuyuheng.a202304100228.myapplication.model.Diary
import kotlinx.coroutines.launch

class DiaryActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化数据库
        db = AppDatabase.getInstance(applicationContext)

        // 添加日记示例
        val newDiary = Diary(
            restaurantName = "海底捞",
            content = "味道很好，服务超棒！",
            imagePath = "/storage/emulated/0/DCIM/Camera/123.jpg",
            rating = 4.5f
        )

        // 插入日记（协程）
        lifecycleScope.launch {
            db.diaryDao().insertDiary(newDiary)
        }

        // 监听日记列表变化
        lifecycleScope.launch {
            db.diaryDao().getAllDiaries().collect { diaries ->
                // 每次数据变化都会回调
                println("当前日记数量：${diaries.size}")
            }
        }
    }
}