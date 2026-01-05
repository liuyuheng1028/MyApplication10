package com.liuyuheng.a202304100228.myapplication.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.Date

@Entity(tableName = "diary")
data class Diary(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val restaurantName: String, // 餐厅名称
    val content: String, // 日记内容
    val imagePath: String, // 图片路径
    val rating: Float, // 评分
    val createTime: Date = Date() // 创建时间
) : Serializable