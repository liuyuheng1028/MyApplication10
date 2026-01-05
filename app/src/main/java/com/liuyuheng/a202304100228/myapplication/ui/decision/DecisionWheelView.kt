package com.liuyuheng.a202304100228.myapplication.ui.decision

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class DecisionWheelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    
    private val paint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
        isDither = true
    }
    
    private val textPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        textSize = 36f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }
    
    private val pointerPaint = Paint().apply {
        isAntiAlias = true
        color = Color.RED
        style = Paint.Style.FILL
    }
    
    private val options = arrayOf("中餐", "西餐", "日料", "韩料", "火锅", "烧烤", "快餐", "甜品")
    private val colors = intArrayOf(
        Color.parseColor("#FF6200EE"),
        Color.parseColor("#FF3700B3"),
        Color.parseColor("#FF03DAC6"),
        Color.parseColor("#FF03DAC5"),
        Color.parseColor("#FFFF6B6B"),
        Color.parseColor("#FFFFCA28"),
        Color.parseColor("#FF7E57C2"),
        Color.parseColor("#FF29B6F6")
    )
    
    private val path = Path()
    private val rect = RectF()
    private val textBounds = Rect()
    private val pointerPath = Path()
    
    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f
    
    private var currentRotation = 0f
    private var targetRotation = 0f
    private var isSpinning = false
    private var spinAnimator: ValueAnimator? = null
    
    private var onSpinCompleteListener: ((String) -> Unit)? = null
    
    fun setOnSpinCompleteListener(listener: (String) -> Unit) {
        onSpinCompleteListener = listener
    }
    
    fun spin() {
        if (isSpinning) return
        
        isSpinning = true
        spinAnimator?.cancel()
        
        val minSpins = 5
        val extraDegrees = (Math.random() * 360).toFloat()
        targetRotation = currentRotation + (minSpins * 360) + extraDegrees
        
        spinAnimator = ValueAnimator.ofFloat(currentRotation, targetRotation).apply {
            duration = 3000
            interpolator = android.view.animation.DecelerateInterpolator()
            addUpdateListener { animation ->
                currentRotation = animation.animatedValue as Float
                invalidate()
            }
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    isSpinning = false
                    val normalizedRotation = (currentRotation % 360 + 360) % 360
                    val pointerAngle = 270f
                    val effectiveAngle = (pointerAngle - normalizedRotation + 360) % 360
                    val angleStep = 360f / options.size
                    val selectedIndex = (effectiveAngle / angleStep).toInt() % options.size
                    onSpinCompleteListener?.invoke(options[selectedIndex])
                }
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
            start()
        }
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        canvas.save()
        canvas.rotate(currentRotation, centerX, centerY)
        
        val angleStep = 360f / options.size
        
        for (i in options.indices) {
            paint.color = colors[i]
            path.reset()
            path.moveTo(centerX, centerY)
            path.arcTo(rect, i * angleStep, angleStep)
            path.close()
            canvas.drawPath(path, paint)
            
            val angle = Math.toRadians((i * angleStep + (i + 1) * angleStep) / 2.0)
            val textX = (centerX + radius * 0.6 * cos(angle)).toFloat()
            val textY = (centerY + radius * 0.6 * sin(angle)).toFloat()
            
            textPaint.getTextBounds(options[i], 0, options[i].length, textBounds)
            canvas.drawText(options[i], textX, textY + textBounds.height() / 2f, textPaint)
        }
        
        canvas.restore()
        
        drawPointer(canvas)
    }
    
    private fun drawPointer(canvas: Canvas) {
        val pointerSize = radius * 0.15f
        val pointerX = centerX
        val pointerY = centerY - radius - pointerSize
        
        pointerPath.reset()
        pointerPath.moveTo(pointerX, pointerY)
        pointerPath.lineTo(pointerX - pointerSize / 2, pointerY - pointerSize)
        pointerPath.lineTo(pointerX + pointerSize / 2, pointerY - pointerSize)
        pointerPath.close()
        
        canvas.drawPath(pointerPath, pointerPaint)
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        
        val padding = 20f
        centerX = w / 2f
        centerY = h / 2f
        radius = (minOf(w, h) / 2f - padding)
        
        rect.set(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )
    }
}