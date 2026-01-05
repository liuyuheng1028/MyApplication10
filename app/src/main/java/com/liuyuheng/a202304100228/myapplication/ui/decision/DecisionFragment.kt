package com.liuyuheng.a202304100228.myapplication.ui.decision

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.liuyuheng.a202304100228.myapplication.R
import kotlin.random.Random

class DecisionFragment : Fragment() {

    private lateinit var decisionWheel: DecisionWheelView
    private lateinit var spinButton: Button
    private lateinit var resultText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_decision, container, false)
        
        decisionWheel = view.findViewById(R.id.decision_wheel)
        spinButton = view.findViewById(R.id.spin_button)
        resultText = view.findViewById(R.id.result_text)
        
        spinButton.setOnClickListener {
            spinWheel()
        }
        
        return view
    }
    
    private fun spinWheel() {
        try {
            // 选项列表
            val options = arrayOf("中餐", "西餐", "日料", "韩料", "火锅", "烧烤", "快餐", "甜品")
            
            // 每个扇区的角度
            val anglePerSection = 360f / options.size
            
            // 随机选择一个选项
            val randomIndex = Random.nextInt(options.size)
            
            // 计算旋转角度：
            // 箭头在转盘顶部（270度位置）
            // 第i个扇区的中心角度是：i * anglePerSection + anglePerSection / 2
            // 要让箭头指向第i个扇区，需要将转盘旋转，使得第i个扇区的中心位于顶部（270度）
            // 旋转角度 = 270 - (i * anglePerSection + anglePerSection / 2)
            // 使用负角度进行逆时针旋转，并加上多圈旋转
            val sectionCenterAngle = randomIndex * anglePerSection + anglePerSection / 2
            val targetAngle = 270f - sectionCenterAngle
            val angle = -(360 * 5 + targetAngle)
            
            // 创建旋转动画
            val animator = ObjectAnimator.ofFloat(decisionWheel, "rotation", 0f, angle)
            animator.duration = 4000
            animator.interpolator = DecelerateInterpolator()
            
            // 动画监听器
            animator.addListener(object : android.animation.Animator.AnimatorListener {
                override fun onAnimationStart(animation: android.animation.Animator) {}
                
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    try {
                        // 计算实际旋转角度（规范化到0-360度）
                        val actualRotation = (angle % 360 + 360) % 360
                        
                        // 计算箭头指向的扇区
                        // 箭头在270度位置，转盘旋转了actualRotation度
                        // 所以箭头指向的角度是：(270 - actualRotation + 360) % 360
                        val arrowAngle = (270f - actualRotation + 360f) % 360f
                        
                        // 计算对应的扇区索引
                        val selectedIndex = ((arrowAngle / anglePerSection).toInt() % options.size)
                        
                        // 显示结果
                        resultText.text = options[selectedIndex]
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                
                override fun onAnimationCancel(animation: android.animation.Animator) {}
                
                override fun onAnimationRepeat(animation: android.animation.Animator) {}
            })
            
            animator.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}