package com.liuyuheng.a202304100228.myapplication.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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

class MapFragment : Fragment() {
    companion object {
        private const val TAG = "MapFragment"
    }

    private lateinit var mapView: MapView
    private var aMap: AMap? = null
    private lateinit var restaurantInfoText: TextView
    private lateinit var btnZoomIn: Button
    private lateinit var btnZoomOut: Button
    private lateinit var btnReset: Button
    private lateinit var btnLocate: Button
    private lateinit var btnNearby: Button
    
    private var locationClient: AMapLocationClient? = null
    private var userLocation: LatLng? = null
    private var userLocationMarker: Marker? = null
    private var showNearbyOnly = false
    private val restaurantMarkers = mutableListOf<Marker>()
    private val restaurants = mutableListOf<Restaurant>()
    private var isLocationClientInitialized = false
    private var isFragmentVisible = false
    private var isFragmentDestroyed = false
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private val runnableList = mutableListOf<Runnable>()
    private var locationRetryCount = 0
    private val maxLocationRetry = 3
    
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d(TAG, "locationPermissionLauncher: 权限回调触发，isGranted=$isGranted, isFragmentDestroyed=$isFragmentDestroyed")
        
        if (isFragmentDestroyed) {
            Log.w(TAG, "locationPermissionLauncher: Fragment已销毁，忽略权限回调")
            return@registerForActivityResult
        }
        
        if (isGranted) {
            Log.d(TAG, "locationPermissionLauncher: 定位权限已授予")
            startLocation()
        } else {
            Log.e(TAG, "locationPermissionLauncher: 定位权限被拒绝")
            if (!isFragmentDestroyed && isAdded) {
                Toast.makeText(requireContext(), "需要定位权限才能显示您的位置", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: ========== 开始创建MapFragment ==========")
        
        try {
            Log.d(TAG, "onCreateView: 设置高德地图隐私合规")
            AMapLocationClient.updatePrivacyShow(requireContext(), true, true)
            AMapLocationClient.updatePrivacyAgree(requireContext(), true)
            Log.d(TAG, "onCreateView: 高德地图隐私合规设置完成")
        } catch (e: Exception) {
            Log.e(TAG, "onCreateView: 隐私合规设置失败", e)
            e.printStackTrace()
        }
        
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ========== 开始初始化地图 ==========")

        try {
            mapView = view.findViewById(R.id.map_view)
            restaurantInfoText = view.findViewById(R.id.restaurant_info_text)
            btnZoomIn = view.findViewById(R.id.btn_zoom_in)
            btnZoomOut = view.findViewById(R.id.btn_zoom_out)
            btnReset = view.findViewById(R.id.btn_reset)
            btnLocate = view.findViewById(R.id.btn_locate)
            btnNearby = view.findViewById(R.id.btn_nearby)

            Log.d(TAG, "onViewCreated: findViewById完成")
            
            mapView.onCreate(savedInstanceState)
            Log.d(TAG, "onViewCreated: mapView.onCreate完成")
            
            setupRestaurants()
            setupButtons()
            
            try {
                Log.d(TAG, "onViewCreated: 开始调用setupLocation")
                setupLocation()
                Log.d(TAG, "onViewCreated: setupLocation调用成功")
            } catch (e: SecurityException) {
                Log.e(TAG, "onViewCreated: setupLocation安全异常", e)
                e.printStackTrace()
                isLocationClientInitialized = false
                locationClient = null
                requireActivity().runOnUiThread {
                    restaurantInfoText.text = "地图已加载，定位服务初始化失败（权限问题），请点击定位按钮重试"
                    Toast.makeText(requireContext(), "定位服务初始化失败（权限问题），请点击定位按钮重试", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "onViewCreated: setupLocation调用失败", e)
                e.printStackTrace()
                isLocationClientInitialized = false
                locationClient = null
                requireActivity().runOnUiThread {
                    restaurantInfoText.text = "地图已加载，定位服务初始化失败，请点击定位按钮重试"
                    Toast.makeText(requireContext(), "定位服务初始化失败: ${e.message}，请点击定位按钮重试", Toast.LENGTH_LONG).show()
                }
            }
            
            if (isLocationClientInitialized) {
                restaurantInfoText.text = "地图已加载，点击定位按钮获取您的位置"
            }
            
            mapView.viewTreeObserver.addOnGlobalLayoutListener(object : android.view.ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (mapView.width > 0 && mapView.height > 0) {
                        Log.d(TAG, "onViewCreated: mapView布局完成，尺寸: ${mapView.width}x${mapView.height}")
                        if (mapView.viewTreeObserver.isAlive) {
                            mapView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        }
                        initMap()
                    }
                }
            })
            
            Log.d(TAG, "onViewCreated: ========== 地图初始化完成 ==========")
        } catch (e: Exception) {
            Log.e(TAG, "onViewCreated: 初始化异常", e)
            e.printStackTrace()
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "地图初始化失败: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun initMap() {
        try {
            Log.d(TAG, "initMap: ========== 开始初始化AMap ==========")
            Log.d(TAG, "initMap: mapView是否已初始化: ${::mapView.isInitialized}")
            Log.d(TAG, "initMap: mapView.width: ${mapView.width}, height: ${mapView.height}")
            Log.d(TAG, "initMap: mapView.parent: ${mapView.parent}")
            
            checkNetworkConnection()
            
            aMap = mapView.map
            
            if (aMap == null) {
                Log.e(TAG, "initMap: aMap为null，地图初始化失败")
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "地图初始化失败，请检查API Key配置", Toast.LENGTH_LONG).show()
                }
                return
            }
            
            Log.d(TAG, "initMap: aMap获取成功，开始配置地图")
            Log.d(TAG, "initMap: aMap.mapType: ${aMap?.mapType}")
            
            aMap?.mapType = AMap.MAP_TYPE_NORMAL
            aMap?.uiSettings?.isZoomControlsEnabled = false
            aMap?.uiSettings?.isCompassEnabled = true
            aMap?.uiSettings?.isMyLocationButtonEnabled = false
            aMap?.uiSettings?.isScaleControlsEnabled = true
            
            val myLocationStyle = MyLocationStyle()
            myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_foreground))
            myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0))
            myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0))
            myLocationStyle.interval(2000)
            myLocationStyle.showMyLocation(true)
            aMap?.myLocationStyle = myLocationStyle
            aMap?.isMyLocationEnabled = true
            
            val beijing = LatLng(39.9042, 116.4074)
            aMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(beijing, 12f))
            Log.d(TAG, "initMap: 相机移动完成")
            Log.d(TAG, "initMap: 相机位置: ${aMap?.cameraPosition}")
            
            var mapLoadedListenerTriggered = false
            
            aMap?.setOnMapLoadedListener {
                Log.d(TAG, "initMap: ========== 地图加载完成 ==========")
                Log.d(TAG, "initMap: isFragmentDestroyed: $isFragmentDestroyed")
                mapLoadedListenerTriggered = true
                Log.d(TAG, "initMap: 地图可见性: ${mapView.visibility}")
                Log.d(TAG, "initMap: 地图类型: ${aMap?.mapType}")
                Log.d(TAG, "initMap: 地图缩放级别: ${aMap?.cameraPosition?.zoom}")
                if (!isFragmentDestroyed && isAdded) {
                    requireActivity().runOnUiThread {
                        if (!isFragmentDestroyed) {
                            Toast.makeText(requireContext(), "地图加载成功", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            
            aMap?.setOnMapClickListener { 
                Log.d(TAG, "initMap: 地图被点击")
            }
            
            aMap?.setOnMarkerClickListener { marker ->
                val restaurant = marker.`object` as? Restaurant
                if (restaurant != null) {
                    displayRestaurantInfo(restaurant)
                    true
                } else {
                    false
                }
            }
            
            Log.d(TAG, "initMap: ========== AMap初始化完成 ==========")
            
            val checkRunnable = Runnable {
                Log.d(TAG, "initMap: ========== 延迟检查（5秒后）==========")
                Log.d(TAG, "initMap: isFragmentDestroyed: $isFragmentDestroyed")
                
                if (isFragmentDestroyed) {
                    Log.w(TAG, "initMap: Fragment已销毁，取消延迟检查")
                    return@Runnable
                }
                
                Log.d(TAG, "initMap: mapLoadedListenerTriggered: $mapLoadedListenerTriggered")
                Log.d(TAG, "initMap: aMap?.mapType: ${aMap?.mapType}")
                Log.d(TAG, "initMap: aMap?.cameraPosition: ${aMap?.cameraPosition}")
                Log.d(TAG, "initMap: mapView.width: ${mapView.width}, height: ${mapView.height}")
                Log.d(TAG, "initMap: mapView.visibility: ${mapView.visibility}")
                Log.d(TAG, "initMap: mapView.alpha: ${mapView.alpha}")
                Log.d(TAG, "initMap: mapView.isShown: ${mapView.isShown}")
                
                checkNetworkConnection()
                
                if (!mapLoadedListenerTriggered) {
                    Log.e(TAG, "initMap: 地图加载监听器未被触发，可能存在网络或API Key问题")
                    if (!isFragmentDestroyed && isAdded) {
                        requireActivity().runOnUiThread {
                            if (!isFragmentDestroyed) {
                                Toast.makeText(requireContext(), "地图可能未正确加载，请检查网络连接和API Key配置", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "initMap: 地图加载监听器已触发，地图应该正常显示")
                    
                    Log.d(TAG, "initMap: 尝试切换到卫星地图测试")
                    aMap?.mapType = AMap.MAP_TYPE_SATELLITE
                    
                    val satelliteTestRunnable = Runnable {
                        if (!isFragmentDestroyed) {
                            Log.d(TAG, "initMap: 卫星地图测试完成，切换回普通地图")
                            aMap?.mapType = AMap.MAP_TYPE_NORMAL
                        }
                    }
                    runnableList.add(satelliteTestRunnable)
                    handler.postDelayed(satelliteTestRunnable, 3000)
                }
            }
            runnableList.add(checkRunnable)
            handler.postDelayed(checkRunnable, 5000)
        } catch (e: Exception) {
            Log.e(TAG, "initMap: 初始化异常", e)
            e.printStackTrace()
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "地图初始化异常: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun checkNetworkConnection() {
        try {
            val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = connectivityManager.activeNetwork
                val capabilities = connectivityManager.getNetworkCapabilities(network)
                
                if (capabilities != null) {
                    Log.d(TAG, "checkNetworkConnection: ========== 网络连接状态 ==========")
                    Log.d(TAG, "checkNetworkConnection: 网络已连接")
                    Log.d(TAG, "checkNetworkConnection: WiFi: ${capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)}")
                    Log.d(TAG, "checkNetworkConnection: 移动数据: ${capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)}")
                    Log.d(TAG, "checkNetworkConnection: 以太网: ${capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)}")
                    Log.d(TAG, "checkNetworkConnection: 网络有效: ${capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)}")
                    Log.d(TAG, "checkNetworkConnection: 网络互联网: ${capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)}")
                    Log.d(TAG, "checkNetworkConnection: 网络不按流量计费: ${capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)}")
                    
                    if (!capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                        Log.e(TAG, "checkNetworkConnection: 警告：网络可能没有互联网访问权限")
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireContext(), "警告：网络可能没有互联网访问权限，地图可能无法加载", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Log.e(TAG, "checkNetworkConnection: 网络未连接或网络能力为null")
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "网络未连接，地图可能无法加载", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val networkInfo = connectivityManager.activeNetworkInfo
                Log.d(TAG, "checkNetworkConnection: ========== 网络连接状态（旧版API） ==========")
                Log.d(TAG, "checkNetworkConnection: 网络已连接: ${networkInfo?.isConnected}")
                Log.d(TAG, "checkNetworkConnection: 网络类型: ${networkInfo?.typeName}")
                Log.d(TAG, "checkNetworkConnection: 网络子类型: ${networkInfo?.subtypeName}")
                
                if (networkInfo == null || !networkInfo.isConnected) {
                    Log.e(TAG, "checkNetworkConnection: 网络未连接")
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "网络未连接，地图可能无法加载", Toast.LENGTH_LONG).show()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "checkNetworkConnection: 检查网络连接异常", e)
            e.printStackTrace()
        }
    }

    private fun setupRestaurants() {
        Log.d(TAG, "setupRestaurants: 开始添加餐厅数据")
        restaurants.addAll(listOf(
            Restaurant(
                id = 1,
                name = "老北京炸酱面",
                address = "临安区武肃街288号",
                rating = 4.5f,
                description = "正宗老北京炸酱面，面条劲道，酱香浓郁，配菜丰富。",
                cuisineType = "中餐",
                priceRange = "¥30-50",
                latitude = 30.241871,
                longitude = 119.732963
            ),
            Restaurant(
                id = 2,
                name = "意大利风情餐厅",
                address = "临安区锦北街道科技大道",
                rating = 4.8f,
                description = "正宗意大利料理，手工披萨，意面丰富，环境优雅。",
                cuisineType = "西餐",
                priceRange = "¥100-200",
                latitude = 30.237871,
                longitude = 119.726963
            ),
            Restaurant(
                id = 3,
                name = "樱花日料",
                address = "临安区农林大路168号",
                rating = 4.6f,
                description = "新鲜刺身，寿司精致，日式居酒屋风格，氛围温馨。",
                cuisineType = "日料",
                priceRange = "¥80-150",
                latitude = 30.239871,
                longitude = 119.731963
            ),
            Restaurant(
                id = 4,
                name = "首尔烤肉",
                address = "临安区城中街356号",
                rating = 4.4f,
                description = "正宗韩式烤肉，肉质鲜嫩，配菜丰富，服务周到。",
                cuisineType = "韩料",
                priceRange = "¥60-120",
                latitude = 30.240871,
                longitude = 119.728963
            ),
            Restaurant(
                id = 5,
                name = "重庆火锅",
                address = "临安区锦城街道人民路",
                rating = 4.7f,
                description = "正宗重庆火锅，麻辣鲜香，食材新鲜，汤底浓郁。",
                cuisineType = "火锅",
                priceRange = "¥80-150",
                latitude = 30.236871,
                longitude = 119.730963
            ),
            Restaurant(
                id = 6,
                name = "草原烧烤",
                address = "临安区科技大道128号",
                rating = 4.3f,
                description = "内蒙古风味烧烤，肉质鲜美，烤制专业，分量足。",
                cuisineType = "烧烤",
                priceRange = "¥50-100",
                latitude = 30.242871,
                longitude = 119.727963
            ),
            Restaurant(
                id = 7,
                name = "麦当劳",
                address = "临安区农林大路256号",
                rating = 4.0f,
                description = "国际连锁快餐，汉堡薯条，快捷方便，价格实惠。",
                cuisineType = "快餐",
                priceRange = "¥20-50",
                latitude = 30.235871,
                longitude = 119.733963
            ),
            Restaurant(
                id = 8,
                name = "甜蜜时光",
                address = "临安区城中街128号",
                rating = 4.9f,
                description = "精致甜品，蛋糕香甜，冰淇淋丝滑，环境浪漫。",
                cuisineType = "甜品",
                priceRange = "¥30-80",
                latitude = 30.238871,
                longitude = 119.729963
            ),
            Restaurant(
                id = 9,
                name = "海底捞火锅",
                address = "临安区锦城街道科技大道",
                rating = 4.8f,
                description = "知名火锅连锁，服务一流，食材新鲜，环境舒适。",
                cuisineType = "火锅",
                priceRange = "¥100-200",
                latitude = 30.241871,
                longitude = 119.734963
            ),
            Restaurant(
                id = 10,
                name = "全聚德烤鸭",
                address = "临安区农林大路368号",
                rating = 4.7f,
                description = "百年老字号，正宗北京烤鸭，皮脆肉嫩，味道鲜美。",
                cuisineType = "中餐",
                priceRange = "¥150-300",
                latitude = 30.237871,
                longitude = 119.735963
            ),
            Restaurant(
                id = 11,
                name = "星巴克咖啡",
                address = "临安区城中街168号",
                rating = 4.2f,
                description = "国际咖啡连锁，环境舒适，咖啡香浓，适合办公休闲。",
                cuisineType = "咖啡",
                priceRange = "¥30-60",
                latitude = 30.239871,
                longitude = 119.731963
            ),
            Restaurant(
                id = 12,
                name = "必胜客",
                address = "临安区锦北街道科技大道",
                rating = 4.1f,
                description = "国际披萨连锁，披萨多样，意面丰富，适合家庭聚餐。",
                cuisineType = "西餐",
                priceRange = "¥50-100",
                latitude = 30.242871,
                longitude = 119.726963
            )
        ))
        
        Log.d(TAG, "setupRestaurants: 已添加 ${restaurants.size} 家餐厅")
        addRestaurantMarkers()
    }

    private fun addRestaurantMarkers() {
        try {
            if (isFragmentDestroyed) {
                Log.w(TAG, "addRestaurantMarkers: Fragment已销毁，取消添加餐厅标记")
                return
            }
            
            Log.d(TAG, "addRestaurantMarkers: 开始显示餐厅")
            clearMarkers()
            
            val restaurantsToShow = if (showNearbyOnly && userLocation != null) {
                restaurants.filter { isNearby(it) }
            } else {
                restaurants
            }
            
            Log.d(TAG, "addRestaurantMarkers: 将显示 ${restaurantsToShow.size} 家餐厅")
            
            restaurantsToShow.forEach { restaurant ->
                val markerColor = when (restaurant.cuisineType) {
                    "中餐" -> BitmapDescriptorFactory.HUE_RED
                    "西餐" -> BitmapDescriptorFactory.HUE_BLUE
                    "日料" -> BitmapDescriptorFactory.HUE_ROSE
                    "韩料" -> BitmapDescriptorFactory.HUE_ORANGE
                    "火锅" -> BitmapDescriptorFactory.HUE_YELLOW
                    "烧烤" -> BitmapDescriptorFactory.HUE_MAGENTA
                    "快餐" -> BitmapDescriptorFactory.HUE_GREEN
                    "甜品" -> BitmapDescriptorFactory.HUE_CYAN
                    "咖啡" -> BitmapDescriptorFactory.HUE_VIOLET
                    else -> BitmapDescriptorFactory.HUE_RED
                }
                
                val marker = aMap?.addMarker(
                    MarkerOptions()
                        .position(LatLng(restaurant.latitude, restaurant.longitude))
                        .title(restaurant.name)
                        .snippet("${restaurant.cuisineType} | ${restaurant.priceRange}")
                        .icon(BitmapDescriptorFactory.defaultMarker(markerColor))
                        .draggable(false)
                )
                if (marker != null) {
                    marker.`object` = restaurant
                    restaurantMarkers.add(marker)
                    Log.d(TAG, "addRestaurantMarkers: 添加餐厅标记 - ${restaurant.name}")
                }
            }
            Log.d(TAG, "addRestaurantMarkers: 餐厅显示完成")
            
            if (!isFragmentDestroyed && isAdded) {
                requireActivity().runOnUiThread {
                    if (!isFragmentDestroyed && ::restaurantInfoText.isInitialized) {
                        if (showNearbyOnly) {
                            restaurantInfoText.text = "显示附近餐厅：${restaurantsToShow.size} 家"
                        } else {
                            restaurantInfoText.text = "点击地图上的标记查看餐厅详情"
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "addRestaurantMarkers错误", e)
            e.printStackTrace()
        }
    }

    private fun clearMarkers() {
        restaurantMarkers.forEach { it.remove() }
        restaurantMarkers.clear()
    }

    private fun isNearby(restaurant: Restaurant): Boolean {
        if (userLocation == null) return false
        
        val results = FloatArray(1)
        android.location.Location.distanceBetween(
            userLocation!!.latitude,
            userLocation!!.longitude,
            restaurant.latitude,
            restaurant.longitude,
            results
        )
        
        return results[0] <= 3000
    }

    private fun setupButtons() {
        btnLocate.setOnClickListener {
            if (isFragmentDestroyed) return@setOnClickListener
            Log.d(TAG, "setupButtons: 定位按钮被点击")
            checkLocationPermission()
        }
        
        btnNearby.setOnClickListener {
            if (isFragmentDestroyed) return@setOnClickListener
            Log.d(TAG, "setupButtons: 附近按钮被点击，当前状态: showNearbyOnly=$showNearbyOnly")
            
            if (userLocation == null) {
                Toast.makeText(requireContext(), "请先定位到您的位置", Toast.LENGTH_SHORT).show()
                checkLocationPermission()
                return@setOnClickListener
            }
            
            showNearbyOnly = !showNearbyOnly
            addRestaurantMarkers()
            
            if (showNearbyOnly) {
                btnNearby.text = "全部"
                val nearbyCount = restaurantMarkers.size
                restaurantInfoText.text = "显示附近餐厅：$nearbyCount 家"
                Toast.makeText(requireContext(), "已筛选附近餐厅", Toast.LENGTH_SHORT).show()
            } else {
                btnNearby.text = "附近"
                restaurantInfoText.text = "点击地图上的标记查看餐厅详情"
                Toast.makeText(requireContext(), "显示全部餐厅", Toast.LENGTH_SHORT).show()
            }
        }
        
        btnZoomIn.setOnClickListener {
            if (isFragmentDestroyed) return@setOnClickListener
            aMap?.animateCamera(CameraUpdateFactory.zoomIn())
        }

        btnZoomOut.setOnClickListener {
            if (isFragmentDestroyed) return@setOnClickListener
            aMap?.animateCamera(CameraUpdateFactory.zoomOut())
        }

        btnReset.setOnClickListener {
            if (isFragmentDestroyed) return@setOnClickListener
            val beijing = LatLng(39.9042, 116.4074)
            aMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(beijing, 12f))
            restaurantInfoText.text = "点击地图上的标记查看餐厅详情"
            showNearbyOnly = false
            btnNearby.text = "附近"
            addRestaurantMarkers()
            Toast.makeText(requireContext(), "地图已重置", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupLocation() {
        try {
            Log.d(TAG, "setupLocation: ========== 开始初始化定位客户端 ==========")
            
            val context = requireContext().applicationContext
            Log.d(TAG, "setupLocation: 获取ApplicationContext成功")
            
            locationClient = AMapLocationClient(context)
            
            if (locationClient == null) {
                Log.e(TAG, "setupLocation: 定位客户端创建失败，返回null")
                isLocationClientInitialized = false
                Toast.makeText(requireContext(), "定位客户端创建失败，请检查设备兼容性", Toast.LENGTH_LONG).show()
                return
            }
            
            isLocationClientInitialized = true
            Log.d(TAG, "setupLocation: 定位客户端创建成功")
            
            val locationOption = AMapLocationClientOption()
            locationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            locationOption.isOnceLocation = false
            locationOption.isNeedAddress = true
            locationOption.isOnceLocationLatest = true
            locationOption.httpTimeOut = 20000
            locationOption.interval = 2000
            locationClient?.setLocationOption(locationOption)
            Log.d(TAG, "setupLocation: 定位选项配置完成")
            
            locationClient?.setLocationListener { location ->
                Log.d(TAG, "setupLocation: ========== 收到定位回调 ==========")
                Log.d(TAG, "setupLocation: location是否为null: ${location == null}")
                Log.d(TAG, "setupLocation: isFragmentDestroyed: $isFragmentDestroyed")
                
                if (isFragmentDestroyed) {
                    Log.w(TAG, "setupLocation: Fragment已销毁，忽略定位回调")
                    return@setLocationListener
                }
                
                if (location != null) {
                    Log.d(TAG, "setupLocation: 错误码: ${location.errorCode}, 错误信息: ${location.errorInfo}")
                    
                    if (location.errorCode == 0) {
                        userLocation = LatLng(location.latitude, location.longitude)
                        Log.d(TAG, "setupLocation: 定位成功 - 经度: ${location.longitude}, 纬度: ${location.latitude}")
                        Log.d(TAG, "setupLocation: 地址: ${location.address}")
                        
                        updateUserLocationMarker()
                        aMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
                        
                        val nearbyCount = restaurants.count { isNearby(it) }
                        val infoText = """
                            已定位到您的位置
                            ${location.address ?: "未知地址"}
                            经度: ${String.format("%.6f", location.longitude)}
                            纬度: ${String.format("%.6f", location.latitude)}
                            附近餐厅：$nearbyCount 家
                        """.trimIndent()
                        
                        if (!isFragmentDestroyed && isAdded) {
                            requireActivity().runOnUiThread {
                                if (::restaurantInfoText.isInitialized) {
                                    restaurantInfoText.text = infoText
                                }
                                if (!isFragmentDestroyed) {
                                    Toast.makeText(requireContext(), "定位成功", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } else {
                        Log.e(TAG, "setupLocation: 定位失败 - 错误码: ${location.errorCode}, 错误信息: ${location.errorInfo}")
                        
                        locationRetryCount++
                        if (locationRetryCount < maxLocationRetry) {
                            Log.d(TAG, "setupLocation: 定位失败，准备重试 ($locationRetryCount/$maxLocationRetry)")
                            if (!isFragmentDestroyed && isAdded) {
                                requireActivity().runOnUiThread {
                                    if (!isFragmentDestroyed) {
                                        Toast.makeText(requireContext(), "定位失败，正在重试 ($locationRetryCount/$maxLocationRetry)...", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                handler.postDelayed({
                                    if (!isFragmentDestroyed && isLocationClientInitialized) {
                                        locationClient?.startLocation()
                                    }
                                }, 2000)
                            }
                        } else {
                            Log.e(TAG, "setupLocation: 定位失败，已达到最大重试次数")
                            if (!isFragmentDestroyed && isAdded) {
                                requireActivity().runOnUiThread {
                                    if (!isFragmentDestroyed) {
                                        val errorMsg = when(location.errorCode) {
                                            1 -> "定位服务启动失败"
                                            2 -> "定位服务网络异常"
                                            3 -> "定位服务查询异常"
                                            4 -> "定位服务连接超时"
                                            5 -> "定位服务定位失败"
                                            6 -> "定位服务权限不足"
                                            7 -> "定位服务参数错误"
                                            8 -> "定位服务未知错误"
                                            9 -> "定位服务定位超时"
                                            else -> "定位失败：${location.errorInfo}"
                                        }
                                        Toast.makeText(requireContext(), "$errorMsg (已重试$maxLocationRetry 次)", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Log.e(TAG, "setupLocation: 定位回调返回null")
                    if (!isFragmentDestroyed && isAdded) {
                        requireActivity().runOnUiThread {
                            if (!isFragmentDestroyed) {
                                Toast.makeText(requireContext(), "定位服务返回空数据", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
            Log.d(TAG, "setupLocation: 定位监听器设置完成")
            Log.d(TAG, "setupLocation: ========== 定位客户端初始化完成 ==========")
        } catch (e: SecurityException) {
            Log.e(TAG, "setupLocation: 安全异常 - 可能缺少权限", e)
            e.printStackTrace()
            isLocationClientInitialized = false
            locationClient = null
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "定位权限不足，请检查权限设置", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "setupLocation: 初始化异常", e)
            e.printStackTrace()
            isLocationClientInitialized = false
            locationClient = null
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "定位服务初始化失败: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateUserLocationMarker() {
        try {
            if (isFragmentDestroyed) {
                Log.w(TAG, "updateUserLocationMarker: Fragment已销毁，取消更新")
                return
            }
            
            if (userLocation == null) {
                Log.d(TAG, "updateUserLocationMarker: 用户位置为null，无法更新标记")
                return
            }
            
            userLocationMarker?.remove()
            
            userLocationMarker = aMap?.addMarker(
                MarkerOptions()
                    .position(userLocation!!)
                    .title("我的位置")
                    .snippet("当前位置")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .draggable(false)
            )
            
            Log.d(TAG, "updateUserLocationMarker: 用户位置标记已更新")
        } catch (e: Exception) {
            Log.e(TAG, "updateUserLocationMarker错误", e)
            e.printStackTrace()
        }
    }

    private fun checkLocationPermission() {
        if (isFragmentDestroyed) {
            Log.w(TAG, "checkLocationPermission: Fragment已销毁，取消检查权限")
            return
        }
        
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        Log.d(TAG, "checkLocationPermission: 精确定位权限: $fineLocationGranted, 粗略定位权限: $coarseLocationGranted")
        
        if (fineLocationGranted || coarseLocationGranted) {
            startLocation()
        } else {
            Log.d(TAG, "checkLocationPermission: 请求定位权限")
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    
    private fun startLocation() {
        try {
            if (isFragmentDestroyed) {
                Log.w(TAG, "startLocation: Fragment已销毁，取消启动定位")
                return
            }
            
            Log.d(TAG, "startLocation: ========== 开始启动定位 ==========")
            Log.d(TAG, "startLocation: locationClient是否为null: ${locationClient == null}")
            Log.d(TAG, "startLocation: isLocationClientInitialized: $isLocationClientInitialized")
            
            if (locationClient == null) {
                Log.e(TAG, "startLocation: locationClient为null，尝试重新初始化")
                Toast.makeText(requireContext(), "定位服务未初始化，正在重新初始化...", Toast.LENGTH_SHORT).show()
                
                try {
                    setupLocation()
                    Log.d(TAG, "startLocation: 重新初始化完成")
                    Log.d(TAG, "startLocation: 重新初始化后locationClient是否为null: ${locationClient == null}")
                    Log.d(TAG, "startLocation: 重新初始化后isLocationClientInitialized: $isLocationClientInitialized")
                    
                    if (locationClient == null) {
                        Log.e(TAG, "startLocation: 重新初始化失败，locationClient仍为null")
                        Toast.makeText(requireContext(), "定位服务初始化失败，请重启应用", Toast.LENGTH_LONG).show()
                        return
                    }
                    
                    if (!isLocationClientInitialized) {
                        Log.e(TAG, "startLocation: 重新初始化后isLocationClientInitialized仍为false")
                        Toast.makeText(requireContext(), "定位服务初始化状态异常，请重启应用", Toast.LENGTH_LONG).show()
                        return
                    }
                } catch (e: SecurityException) {
                    Log.e(TAG, "startLocation: 重新初始化时发生安全异常", e)
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "定位权限不足，请检查权限设置", Toast.LENGTH_LONG).show()
                    return
                } catch (e: Exception) {
                    Log.e(TAG, "startLocation: 重新初始化时发生异常", e)
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "定位服务初始化失败: ${e.message}", Toast.LENGTH_LONG).show()
                    return
                }
            }
            
            if (!isLocationClientInitialized) {
                Log.e(TAG, "startLocation: 定位客户端未初始化")
                Toast.makeText(requireContext(), "定位服务未初始化，请重启应用", Toast.LENGTH_LONG).show()
                return
            }
            
            Log.d(TAG, "startLocation: 开始启动定位服务")
            locationClient?.startLocation()
            Log.d(TAG, "startLocation: 定位服务启动命令已发送")
            Toast.makeText(requireContext(), "正在定位中，请稍候...", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "startLocation: ========== 定位启动命令已发送 ==========")
        } catch (e: SecurityException) {
            Log.e(TAG, "startLocation: 安全异常", e)
            e.printStackTrace()
            Toast.makeText(requireContext(), "定位权限不足，请检查权限设置", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e(TAG, "startLocation: 启动异常", e)
            e.printStackTrace()
            Toast.makeText(requireContext(), "定位服务启动失败: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun displayRestaurantInfo(restaurant: Restaurant) {
        try {
            if (isFragmentDestroyed) {
                Log.w(TAG, "displayRestaurantInfo: Fragment已销毁，取消显示餐厅信息")
                return
            }
            
            Log.d(TAG, "displayRestaurantInfo: 显示餐厅信息 - ${restaurant.name}")
            val info = """
                <b>${restaurant.name}</b><br/>
                类型: ${restaurant.cuisineType}<br/>
                评分: ${"★".repeat(restaurant.rating.toInt())}${"☆".repeat(5 - restaurant.rating.toInt())}<br/>
                地址: ${restaurant.address}<br/>
                价格: ${restaurant.priceRange}<br/>
                简介: ${restaurant.description}
            """.trimIndent()

            restaurantInfoText.text = info
        } catch (e: Exception) {
            Log.e(TAG, "displayRestaurantInfo错误", e)
            e.printStackTrace()
        }
    }

    override fun onResume() {
        try {
            Log.d(TAG, "onResume: ========== onResume开始 ==========")
            super.onResume()
            if (::mapView.isInitialized) {
                Log.d(TAG, "onResume: 调用mapView.onResume()")
                mapView.onResume()
                
                val checkRunnable = Runnable {
                    Log.d(TAG, "onResume: 延迟检查mapView状态")
                    Log.d(TAG, "onResume: isFragmentDestroyed: $isFragmentDestroyed")
                    
                    if (isFragmentDestroyed) {
                        Log.w(TAG, "onResume: Fragment已销毁，取消延迟检查")
                        return@Runnable
                    }
                    
                    Log.d(TAG, "onResume: mapView.width: ${mapView.width}, height: ${mapView.height}")
                    Log.d(TAG, "onResume: mapView.visibility: ${mapView.visibility}")
                    Log.d(TAG, "onResume: mapView.isShown: ${mapView.isShown}")
                    Log.d(TAG, "onResume: mapView.parent: ${mapView.parent}")
                    
                    if (::mapView.isInitialized && !mapView.isShown) {
                        Log.w(TAG, "onResume: mapView未显示，尝试重新激活")
                        mapView.visibility = View.VISIBLE
                        mapView.requestLayout()
                        
                        val recheckRunnable = Runnable {
                            if (!isFragmentDestroyed && ::mapView.isInitialized) {
                                Log.d(TAG, "onResume: 重新激活后检查 - mapView.isShown: ${mapView.isShown}")
                            }
                        }
                        runnableList.add(recheckRunnable)
                        handler.postDelayed(recheckRunnable, 500)
                    }
                }
                runnableList.add(checkRunnable)
                handler.postDelayed(checkRunnable, 300)
            }
            isFragmentVisible = true
            Log.d(TAG, "onResume: ========== onResume完成 ==========")
        } catch (e: Exception) {
            Log.e(TAG, "onResume: 异常", e)
            e.printStackTrace()
        }
    }

    override fun onPause() {
        try {
            Log.d(TAG, "onPause: ========== onPause开始 ==========")
            isFragmentVisible = false
            if (::mapView.isInitialized) {
                Log.d(TAG, "onPause: 调用mapView.onPause()")
                mapView.onPause()
            }
            super.onPause()
            Log.d(TAG, "onPause: ========== onPause完成 ==========")
        } catch (e: Exception) {
            Log.e(TAG, "onPause: 异常", e)
            e.printStackTrace()
            super.onPause()
        }
    }
    
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        Log.d(TAG, "onHiddenChanged: hidden=$hidden")
        if (!hidden) {
            Log.d(TAG, "onHiddenChanged: Fragment变为可见，重新激活地图")
            if (::mapView.isInitialized) {
                mapView.onResume()
                mapView.visibility = View.VISIBLE
                mapView.requestLayout()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        try {
            if (::mapView.isInitialized) {
                mapView.onSaveInstanceState(outState)
            }
            super.onSaveInstanceState(outState)
        } catch (e: Exception) {
            Log.e(TAG, "onSaveInstanceState错误", e)
            e.printStackTrace()
            super.onSaveInstanceState(outState)
        }
    }

    override fun onDestroyView() {
        try {
            Log.d(TAG, "onDestroyView: 开始清理资源")
            
            isFragmentDestroyed = true
            isFragmentVisible = false
            
            cancelAllRunnables()
            
            if (::mapView.isInitialized) {
                mapView.onDestroy()
            }
            if (isLocationClientInitialized && locationClient != null) {
                locationClient?.stopLocation()
                locationClient?.onDestroy()
                isLocationClientInitialized = false
            }
            userLocationMarker?.remove()
            userLocationMarker = null
            restaurantMarkers.forEach { it.remove() }
            restaurantMarkers.clear()
            restaurants.clear()
            userLocation = null
            aMap = null
            Log.d(TAG, "onDestroyView: 资源清理完成")
        } catch (e: Exception) {
            Log.e(TAG, "onDestroyView错误", e)
            e.printStackTrace()
        }
        super.onDestroyView()
    }
    
    private fun cancelAllRunnables() {
        try {
            Log.d(TAG, "cancelAllRunnables: 取消所有延迟任务")
            runnableList.forEach { runnable ->
                handler.removeCallbacks(runnable)
            }
            runnableList.clear()
            handler.removeCallbacksAndMessages(null)
            Log.d(TAG, "cancelAllRunnables: 延迟任务已取消")
        } catch (e: Exception) {
            Log.e(TAG, "cancelAllRunnables错误", e)
            e.printStackTrace()
        }
    }
}
