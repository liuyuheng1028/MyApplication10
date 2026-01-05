package com.liuyuheng.a202304100228.myapplication.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RetrofitClient {
    // 模拟数据源地址（替换为真实接口）
    private const val BASE_URL = "https://mock.example.com/"

    // 单例Retrofit实例
    private val retrofit: Retrofit by lazy {
        // 日志拦截器
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // API服务实例
    val apiService: ApiService by lazy {
        retrofit.create()
    }
}