package com.liuyuheng.a202304100228.myapplication.ui.restaurant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liuyuheng.a202304100228.myapplication.R
import com.liuyuheng.a202304100228.myapplication.data.repository.RestaurantRepository
import com.liuyuheng.a202304100228.myapplication.model.Restaurant
import android.util.Log
import kotlinx.coroutines.launch

class RestaurantFragment : Fragment() {
    companion object {
        private const val TAG = "RestaurantFragment"
    }
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RestaurantAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView
    private lateinit var repository: RestaurantRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: 开始创建RestaurantFragment")
        val view = inflater.inflate(R.layout.fragment_restaurant, container, false)
        
        recyclerView = view.findViewById(R.id.restaurant_recycler_view)
        progressBar = view.findViewById(R.id.progress_bar)
        errorText = view.findViewById(R.id.error_text)
        
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        repository = RestaurantRepository()
        
        return view
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: 开始加载餐厅数据")
        loadRestaurants()
    }
    
    private fun loadRestaurants() {
        try {
            Log.d(TAG, "loadRestaurants: 开始加载")
            progressBar.visibility = View.VISIBLE
            errorText.visibility = View.GONE
            recyclerView.visibility = View.GONE
            
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val restaurants = repository.getRestaurants()
                    progressBar.visibility = View.GONE
                    
                    if (restaurants.isNotEmpty()) {
                        adapter = RestaurantAdapter(restaurants)
                        recyclerView.adapter = adapter
                        recyclerView.visibility = View.VISIBLE
                        Log.d(TAG, "loadRestaurants: 加载成功，共 ${restaurants.size} 家餐厅")
                    } else {
                        errorText.text = "暂无餐厅数据"
                        errorText.visibility = View.VISIBLE
                        Log.w(TAG, "loadRestaurants: 没有餐厅数据")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(TAG, "loadRestaurants: 加载失败", e)
                    progressBar.visibility = View.GONE
                    errorText.text = "加载失败：${e.message}"
                    errorText.visibility = View.VISIBLE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "loadRestaurants: 外部异常", e)
        }
    }
}