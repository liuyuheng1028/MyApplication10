package com.liuyuheng.a202304100228.myapplication.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.liuyuheng.a202304100228.myapplication.model.Diary
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {
    @Insert
    suspend fun insertDiary(diary: Diary)

    @Query("SELECT * FROM diary ORDER BY createTime DESC")
    fun getAllDiaries(): Flow<List<Diary>> // 使用Flow实现数据监听

    @Delete
    suspend fun deleteDiary(diary: Diary)
}