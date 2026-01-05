package com.liuyuheng.a202304100228.myapplication.ui.map

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import com.liuyuheng.a202304100228.myapplication.model.Restaurant
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class RestaurantMapView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val restaurants = mutableListOf<Restaurant>()
    private var selectedRestaurant: Restaurant? = null
    private var userLocation: UserLocation? = null
    private var nearbyRestaurants = mutableListOf<Restaurant>()
    private var showNearbyOnly = false
    
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val markerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val selectedMarkerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val userLocationPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val userLocationBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val nearbyMarkerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    
    private var offsetX = 0f
    private var offsetY = 0f
    private var scale = 1f
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var isDragging = false
    private var isScaling = false
    
    private val mapWidth = 2000f
    private val mapHeight = 2000f
    private val minScale = 0.5f
    private val maxScale = 3f
    
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    
    init {
        paint.color = Color.parseColor("#E8F5E9")
        paint.style = Paint.Style.FILL
        
        textPaint.color = Color.BLACK
        textPaint.textSize = 40f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        
        markerPaint.color = Color.parseColor("#FF5722")
        markerPaint.style = Paint.Style.FILL
        
        selectedMarkerPaint.color = Color.parseColor("#2196F3")
        selectedMarkerPaint.style = Paint.Style.FILL
        
        borderPaint.color = Color.parseColor("#4CAF50")
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = 5f
        
        userLocationPaint.color = Color.parseColor("#2196F3")
        userLocationPaint.style = Paint.Style.FILL
        
        userLocationBorderPaint.color = Color.parseColor("#1976D2")
        userLocationBorderPaint.style = Paint.Style.STROKE
        userLocationBorderPaint.strokeWidth = 4f
        
        nearbyMarkerPaint.color = Color.parseColor("#FF9800")
        nearbyMarkerPaint.style = Paint.Style.FILL
        
        scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
    }
    
    fun setRestaurants(restaurantList: List<Restaurant>) {
        restaurants.clear()
        restaurants.addAll(restaurantList)
        invalidate()
    }
    
    fun setUserLocation(location: UserLocation) {
        userLocation = location
        calculateNearbyRestaurants()
        invalidate()
    }
    
    fun getUserLocation(): UserLocation? = userLocation
    
    fun getNearbyRestaurants(): List<Restaurant> = nearbyRestaurants.toList()
    
    fun setShowNearbyOnly(show: Boolean) {
        showNearbyOnly = show
        invalidate()
    }
    
    private fun calculateNearbyRestaurants() {
        nearbyRestaurants.clear()
        if (userLocation == null) return
        
        val userX = userLocation!!.x * mapWidth
        val userY = userLocation!!.y * mapHeight
        val searchRadius = 500f
        
        for (restaurant in restaurants) {
            val restaurantX = restaurant.longitude.toFloat() * mapWidth
            val restaurantY = restaurant.latitude.toFloat() * mapHeight
            val dx = restaurantX - userX
            val dy = restaurantY - userY
            val distance = sqrt(dx * dx + dy * dy)
            
            if (distance <= searchRadius) {
                nearbyRestaurants.add(restaurant)
            }
        }
    }
    
    fun getSelectedRestaurant(): Restaurant? = selectedRestaurant
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        if (width == 0 || height == 0) return
        
        canvas.save()
        canvas.translate(offsetX, offsetY)
        canvas.scale(scale, scale)
        
        canvas.drawRect(0f, 0f, mapWidth, mapHeight, paint)
        canvas.drawRect(0f, 0f, mapWidth, mapHeight, borderPaint)
        
        paint.color = Color.parseColor("#C8E6C9")
        paint.strokeWidth = 2f
        paint.style = Paint.Style.STROKE
        
        for (i in 0..10) {
            val y = i * (mapHeight / 10)
            canvas.drawLine(0f, y, mapWidth, y, paint)
        }
        for (i in 0..10) {
            val x = i * (mapWidth / 10)
            canvas.drawLine(x, 0f, x, mapHeight, paint)
        }
        
        paint.style = Paint.Style.FILL
        
        for (restaurant in restaurants) {
            if (showNearbyOnly && !nearbyRestaurants.contains(restaurant)) {
                continue
            }
            
            val x = restaurant.longitude.toFloat() * mapWidth
            val y = restaurant.latitude.toFloat() * mapHeight
            val isNearby = nearbyRestaurants.contains(restaurant)
            
            if (restaurant == selectedRestaurant) {
                canvas.drawCircle(x, y, 50f, selectedMarkerPaint)
                canvas.drawCircle(x, y, 40f, markerPaint)
                
                textPaint.textSize = 40f
                textPaint.color = Color.WHITE
                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                val text = restaurant.name
                canvas.drawText(text, x - textPaint.measureText(text) / 2, y + 15f, textPaint)
            } else if (isNearby) {
                canvas.drawCircle(x, y, 45f, nearbyMarkerPaint)
                
                textPaint.textSize = 38f
                textPaint.color = Color.WHITE
                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                val text = restaurant.name
                canvas.drawText(text, x - textPaint.measureText(text) / 2, y + 13f, textPaint)
            } else {
                canvas.drawCircle(x, y, 40f, markerPaint)
                
                textPaint.textSize = 36f
                textPaint.color = Color.WHITE
                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                val text = restaurant.name
                canvas.drawText(text, x - textPaint.measureText(text) / 2, y + 12f, textPaint)
            }
        }
        
        if (userLocation != null) {
            val x = userLocation!!.x * mapWidth
            val y = userLocation!!.y * mapHeight
            
            canvas.drawCircle(x, y, 30f, userLocationPaint)
            canvas.drawCircle(x, y, 30f, userLocationBorderPaint)
            
            paint.color = Color.parseColor("#2196F3")
            paint.alpha = 50
            canvas.drawCircle(x, y, 500f, paint)
            paint.alpha = 255
            
            textPaint.textSize = 32f
            textPaint.color = Color.parseColor("#1976D2")
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            val text = "我的位置"
            canvas.drawText(text, x - textPaint.measureText(text) / 2, y - 50f, textPaint)
        }
        
        canvas.restore()
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.x
                lastTouchY = event.y
                isDragging = false
                isScaling = false
                
                val touchX = (event.x - offsetX) / scale
                val touchY = (event.y - offsetY) / scale
                
                selectedRestaurant = null
                for (restaurant in restaurants) {
                    val x = restaurant.longitude.toFloat() * mapWidth
                    val y = restaurant.latitude.toFloat() * mapHeight
                    val dx = touchX - x
                    val dy = touchY - y
                    val distance = sqrt(dx * dx + dy * dy)
                    if (distance < 60f) {
                        selectedRestaurant = restaurant
                        break
                    }
                }
                invalidate()
                return true
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                isScaling = true
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (!isScaling && event.pointerCount == 1) {
                    val dx = event.x - lastTouchX
                    val dy = event.y - lastTouchY
                    if (sqrt(dx * dx + dy * dy) > 5f) {
                        isDragging = true
                        offsetX += dx
                        offsetY += dy
                        clampOffset()
                        lastTouchX = event.x
                        lastTouchY = event.y
                        invalidate()
                    }
                }
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                if (!isDragging && !isScaling && selectedRestaurant != null) {
                    performClick()
                }
                isDragging = false
                isScaling = false
                return true
            }
        }
        return super.onTouchEvent(event)
    }
    
    private fun clampOffset() {
        val scaledWidth = mapWidth * scale
        val scaledHeight = mapHeight * scale
        
        val minX = width - scaledWidth
        val minY = height - scaledHeight
        
        if (scaledWidth >= width) {
            offsetX = offsetX.coerceIn(minX, 0f)
        } else {
            offsetX = (width - scaledWidth) / 2f
        }
        
        if (scaledHeight >= height) {
            offsetY = offsetY.coerceIn(minY, 0f)
        } else {
            offsetY = (height - scaledHeight) / 2f
        }
    }
    
    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
    
    fun resetView() {
        scale = 1f
        offsetX = (width - mapWidth) / 2f
        offsetY = (height - mapHeight) / 2f
        selectedRestaurant = null
        invalidate()
    }
    
    fun zoomIn() {
        val oldScale = scale
        scale = min(scale * 1.3f, maxScale)
        zoomCenter(oldScale, scale, width / 2f, height / 2f)
        invalidate()
    }
    
    fun zoomOut() {
        val oldScale = scale
        scale = max(scale / 1.3f, minScale)
        zoomCenter(oldScale, scale, width / 2f, height / 2f)
        invalidate()
    }
    
    private fun zoomCenter(oldScale: Float, newScale: Float, centerX: Float, centerY: Float) {
        val scaleFactor = newScale / oldScale
        offsetX = centerX - (centerX - offsetX) * scaleFactor
        offsetY = centerY - (centerY - offsetY) * scaleFactor
        clampOffset()
    }
    
    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val oldScale = scale
            scale *= detector.scaleFactor
            scale = scale.coerceIn(minScale, maxScale)
            
            val scaleFactor = scale / oldScale
            val centerX = detector.focusX
            val centerY = detector.focusY
            offsetX = centerX - (centerX - offsetX) * scaleFactor
            offsetY = centerY - (centerY - offsetY) * scaleFactor
            clampOffset()
            
            invalidate()
            return true
        }
        
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            isScaling = true
            return true
        }
        
        override fun onScaleEnd(detector: ScaleGestureDetector) {
            isScaling = false
        }
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (offsetX == 0f && offsetY == 0f) {
            offsetX = (w - mapWidth) / 2f
            offsetY = (h - mapHeight) / 2f
        }
    }
}
