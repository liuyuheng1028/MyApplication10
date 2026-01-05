package com.liuyuheng.a202304100228.myapplication.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.liuyuheng.a202304100228.myapplication.model.Diary
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiary(diary: Diary): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiaries(diaries: List<Diary>): List<Long>

    @Query("SELECT * FROM diary ORDER BY createTime DESC")
    fun getAllDiaries(): Flow<List<Diary>>

    @Query("SELECT * FROM diary WHERE id = :id")
    suspend fun getDiaryById(id: Int): Diary?

    @Query("SELECT * FROM diary WHERE restaurantName LIKE '%' || :name || '%' ORDER BY createTime DESC")
    fun searchDiariesByRestaurantName(name: String): Flow<List<Diary>>

    @Query("SELECT * FROM diary WHERE rating >= :minRating ORDER BY createTime DESC")
    fun getDiariesByMinRating(minRating: Float): Flow<List<Diary>>

    @Query("SELECT * FROM diary ORDER BY rating DESC LIMIT :limit")
    fun getTopRatedDiaries(limit: Int): Flow<List<Diary>>

    @Query("SELECT COUNT(*) FROM diary")
    suspend fun getDiaryCount(): Int

    @Query("SELECT AVG(rating) FROM diary")
    suspend fun getAverageRating(): Float?

    @Update
    suspend fun updateDiary(diary: Diary)

    @Delete
    suspend fun deleteDiary(diary: Diary)

    @Query("DELETE FROM diary WHERE id = :id")
    suspend fun deleteDiaryById(id: Int)

    @Query("DELETE FROM diary")
    suspend fun deleteAllDiaries()

    @Transaction
    suspend fun deleteDiaries(diaries: List<Diary>) {
        diaries.forEach { deleteDiary(it) }
    }
}