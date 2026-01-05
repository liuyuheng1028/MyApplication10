package com.liuyuheng.a202304100228.myapplication.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.maps.model.MyLocationStyle
import com.liuyuheng.a202304100228.myapplication.R
import com.liuyuheng.a202304100228.myapplication.model.Restaurant

class MapActivity : AppCompatActivity() {
    private lateinit var mapView: MapView
    private lateinit var aMap: AMap
    private lateinit var restaurantInfoText: TextView
    private lateinit var btnZoomIn: Button
    private lateinit var btnZoomOut: Button
    private lateinit var btnReset: Button
    
    private lateinit var locationClient: AMapLocationClient
    private var userLocation: LatLng? = null
    private val restaurantMarkers = mutableListOf<Marker>()
    private val restaurants = mutableListOf<Restaurant>()
    
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startLocation()
        } else {
            Toast.makeText(this, "需要定位权限才能显示您的位置", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        mapView = findViewById(R.id.map_view)
        restaurantInfoText = findViewById(R.id.restaurant_info_text)
        btnZoomIn = findViewById(R.id.btn_zoom_in)
        btnZoomOut = findViewById(R.id.btn_zoom_out)
        btnReset = findViewById(R.id.btn_reset)

        mapView.onCreate(savedInstanceState)
        initMap()
        setupRestaurants()
        setupButtons()
        setupLocation()
    }

    private fun initMap() {
        aMap = mapView.map
        
        val myLocationStyle = MyLocationStyle()
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_foreground))
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0))
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0))
        myLocationStyle.interval(2000)
        myLocationStyle.showMyLocation(true)
        aMap.myLocationStyle = myLocationStyle
        
        aMap.uiSettings.isZoomControlsEnabled = false
        aMap.uiSettings.isCompassEnabled = true
        aMap.uiSettings.isMyLocationButtonEnabled = false
        
        val beijing = LatLng(39.9042, 116.4074)
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(beijing, 12f))
        
        aMap.setOnMarkerClickListener { marker ->
            val restaurant = marker.`object` as? Restaurant
            if (restaurant != null) {
                displayRestaurantInfo(restaurant)
                true
            } else {
                false
            }
        }
    }

    private fun setupRestaurants() {
        restaurants.addAll(listOf(
            Restaurant(
                id = 1,
                name = "老北京炸酱面",
                address = "北京市朝阳区建国路88号",
                rating = 4.5f,
                description = "正宗老北京炸酱面，面条劲道，酱香浓郁，配菜丰富。",
                cuisineType = "中餐",
                priceRange = "¥30-50",
                latitude = 39.9142,
                longitude = 116.4174
            ),
            Restaurant(
                id = 2,
                name = "意大利风情餐厅",
                address = "北京市海淀区中关村大街1号",
                rating = 4.8f,
                description = "正宗意大利料理，手工披萨，意面丰富，环境优雅。",
                cuisineType = "西餐",
                priceRange = "¥100-200",
                latitude = 39.9842,
                longitude = 116.3174
            ),
            Restaurant(
                id = 3,
                name = "樱花日料",
                address = "北京市东城区王府井大街2号",
                rating = 4.6f,
                description = "新鲜刺身，寿司精致，日式居酒屋风格，氛围温馨。",
                cuisineType = "日料",
                priceRange = "¥80-150",
                latitude = 39.9242,
                longitude = 116.4174
            ),
            Restaurant(
                id = 4,
                name = "首尔烤肉",
                address = "北京市西城区西单北大街3号",
                rating = 4.4f,
                description = "正宗韩式烤肉，肉质鲜嫩，配菜丰富，服务周到。",
                cuisineType = "韩料",
                priceRange = "¥60-120",
                latitude = 39.9142,
                longitude = 116.3774
            ),
            Restaurant(
                id = 5,
                name = "重庆火锅",
                address = "北京市丰台区南三环西路4号",
                rating = 4.7f,
                description = "正宗重庆火锅，麻辣鲜香，食材新鲜，汤底浓郁。",
                cuisineType = "火锅",
                priceRange = "¥80-150",
                latitude = 39.8642,
                longitude = 116.4474
            ),
            Restaurant(
                id = 6,
                name = "草原烧烤",
                address = "北京市通州区新华大街5号",
                rating = 4.3f,
                description = "内蒙古风味烧烤，肉质鲜美，烤制专业，分量足。",
                cuisineType = "烧烤",
                priceRange = "¥50-100",
                latitude = 39.9042,
                longitude = 116.6574
            ),
            Restaurant(
                id = 7,
                name = "麦当劳",
                address = "北京市朝阳区三里屯路6号",
                rating = 4.0f,
                description = "国际连锁快餐，汉堡薯条，快捷方便，价格实惠。",
                cuisineType = "快餐",
                priceRange = "¥20-50",
                latitude = 39.9342,
                longitude = 116.4574
            ),
            Restaurant(
                id = 8,
                name = "甜蜜时光",
                address = "北京市朝阳区国贸商城7号",
                rating = 4.9f,
                description = "精致甜品，蛋糕香甜，冰淇淋丝滑，环境浪漫。",
                cuisineType = "甜品",
                priceRange = "¥30-80",
                latitude = 39.9042,
                longitude = 116.4674
            )
        ))
        
        addRestaurantMarkers()
    }

    private fun addRestaurantMarkers() {
        restaurants.forEach { restaurant ->
            val marker = aMap.addMarker(
                MarkerOptions()
                    .position(LatLng(restaurant.latitude, restaurant.longitude))
                    .title(restaurant.name)
                    .snippet(restaurant.cuisineType)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
            marker.`object` = restaurant
            restaurantMarkers.add(marker)
        }
    }

    private fun setupButtons() {
        btnZoomIn.setOnClickListener {
            aMap.animateCamera(CameraUpdateFactory.zoomIn())
        }

        btnZoomOut.setOnClickListener {
            aMap.animateCamera(CameraUpdateFactory.zoomOut())
        }

        btnReset.setOnClickListener {
            val beijing = LatLng(39.9042, 116.4074)
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(beijing, 12f))
            restaurantInfoText.text = "点击地图上的标记查看餐厅详情"
        }
    }

    private fun setupLocation() {
        locationClient = AMapLocationClient(applicationContext)
        val locationOption = AMapLocationClientOption()
        locationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        locationOption.isOnceLocation = true
        locationOption.isNeedAddress = true
        locationClient.setLocationOption(locationOption)
        
        locationClient.setLocationListener { location ->
            if (location != null) {
                if (location.errorCode == 0) {
                    userLocation = LatLng(location.latitude, location.longitude)
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
                    
                    restaurantInfoText.text = "已定位到您的位置\n${location.address}"
                    
                    Toast.makeText(this, "定位成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "定位失败：${location.errorInfo}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startLocation()
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    
    private fun startLocation() {
        locationClient.startLocation()
    }

    private fun displayRestaurantInfo(restaurant: Restaurant) {
        val info = """
            <b>${restaurant.name}</b><br/>
            类型: ${restaurant.cuisineType}<br/>
            评分: ${"★".repeat(restaurant.rating.toInt())}${"☆".repeat(5 - restaurant.rating.toInt())}<br/>
            地址: ${restaurant.address}<br/>
            价格: ${restaurant.priceRange}<br/>
            简介: ${restaurant.description}
        """.trimIndent()

        restaurantInfoText.text = info
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        locationClient.stopLocation()
        locationClient.onDestroy()
    }
}
