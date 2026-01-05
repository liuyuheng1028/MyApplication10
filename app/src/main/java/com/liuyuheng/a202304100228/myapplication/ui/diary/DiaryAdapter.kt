package com.liuyuheng.a202304100228.myapplication.ui.diary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.liuyuheng.a202304100228.myapplication.R
import com.liuyuheng.a202304100228.myapplication.model.Diary
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DiaryAdapter(
    private val diaries: List<Diary>,
    private val onItemClick: (Diary) -> Unit = {},
    private val onItemLongClick: (Diary) -> Unit = {}
) : RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder>() {

    class DiaryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val restaurantNameTextView: TextView = view.findViewById(R.id.diary_restaurant_name)
        val dateTextView: TextView = view.findViewById(R.id.diary_date)
        val ratingBar: RatingBar = view.findViewById(R.id.diary_rating)
        val contentTextView: TextView = view.findViewById(R.id.diary_content)
        val imageView: ImageView = view.findViewById(R.id.diary_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_diary, parent, false)
        return DiaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        try {
            val diary = diaries[position]
            holder.restaurantNameTextView.text = diary.restaurantName
            holder.contentTextView.text = diary.content
            holder.ratingBar.rating = diary.rating
            
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            holder.dateTextView.text = dateFormat.format(diary.createTime)
            
            holder.itemView.setOnClickListener {
                onItemClick(diary)
            }
            
            holder.itemView.setOnLongClickListener {
                onItemLongClick(diary)
                true
            }
            
            if (diary.imagePath.isNotEmpty()) {
                val imageFile = File(diary.imagePath)
                if (imageFile.exists()) {
                    try {
                        holder.imageView.setImageURI(android.net.Uri.fromFile(imageFile))
                        holder.imageView.visibility = View.VISIBLE
                    } catch (e: Exception) {
                        holder.imageView.setImageResource(R.drawable.ic_launcher_background)
                        holder.imageView.visibility = View.VISIBLE
                    }
                } else {
                    holder.imageView.visibility = View.GONE
                }
            } else {
                holder.imageView.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount() = diaries.size
}
