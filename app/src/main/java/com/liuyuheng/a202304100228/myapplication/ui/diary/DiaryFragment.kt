package com.liuyuheng.a202304100228.myapplication.ui.diary

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liuyuheng.a202304100228.myapplication.R
import com.liuyuheng.a202304100228.myapplication.database.AppDatabase
import com.liuyuheng.a202304100228.myapplication.model.Diary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DiaryFragment : Fragment() {
    companion object {
        private const val TAG = "DiaryFragment"
    }
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DiaryAdapter
    private lateinit var addDiaryButton: Button
    private var diaries: MutableList<Diary> = mutableListOf()
    private lateinit var database: AppDatabase
    
    private var selectedImageUri: Uri? = null
    private var currentImageFile: File? = null
    
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            showAddDiaryDialog()
        }
    }
    
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && currentImageFile != null) {
            selectedImageUri = Uri.fromFile(currentImageFile)
            showAddDiaryDialog()
        }
    }
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showImagePickerDialog()
        } else {
            Toast.makeText(requireContext(), "需要相机权限才能拍照", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: 开始创建DiaryFragment")
        val view = inflater.inflate(R.layout.fragment_diary, container, false)
        
        database = AppDatabase.getInstance(requireContext())
        
        recyclerView = view.findViewById(R.id.diary_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        addDiaryButton = view.findViewById(R.id.add_diary_button)
        addDiaryButton.setOnClickListener {
            showImagePickerDialog()
        }
        
        return view
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: 开始加载日记数据")
        loadDiaries()
    }
    
    private fun showImagePickerDialog() {
        Log.d(TAG, "showImagePickerDialog: 显示图片选择对话框")
        val options = arrayOf("从相册选择", "拍照")
        AlertDialog.Builder(requireContext())
            .setTitle("选择图片来源")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> pickImageLauncher.launch("image/*")
                    1 -> {
                        if (ContextCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            takePhoto()
                        } else {
                            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                }
            }
            .show()
    }
    
    private fun takePhoto() {
        try {
            Log.d(TAG, "takePhoto: 开始拍照")
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "DIARY_$timeStamp.jpg"
            val storageDir = File(requireContext().getExternalFilesDir(null), "diary_images")
            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }
            currentImageFile = File(storageDir, imageFileName)
            val photoUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                currentImageFile!!
            )
            cameraLauncher.launch(photoUri)
        } catch (e: Exception) {
            Log.e(TAG, "takePhoto: 拍照失败", e)
            Toast.makeText(requireContext(), "无法创建图片文件", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun showAddDiaryDialog() {
        Log.d(TAG, "showAddDiaryDialog: 显示添加日记对话框")
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_diary, null)
        val restaurantNameInput = dialogView.findViewById<EditText>(R.id.restaurant_name_input)
        val contentInput = dialogView.findViewById<EditText>(R.id.content_input)
        val ratingBar = dialogView.findViewById<RatingBar>(R.id.rating_bar)
        val previewImageView = dialogView.findViewById<ImageView>(R.id.image_preview)
        
        if (selectedImageUri != null) {
            previewImageView.setImageURI(selectedImageUri)
        }
        
        AlertDialog.Builder(requireContext())
            .setTitle("添加新日记")
            .setView(dialogView)
            .setPositiveButton("保存") { _, _ ->
                val restaurantName = restaurantNameInput.text.toString()
                val content = contentInput.text.toString()
                val rating = ratingBar.rating
                
                if (restaurantName.isNotEmpty() && content.isNotEmpty()) {
                    saveDiary(restaurantName, content, rating)
                } else {
                    Toast.makeText(requireContext(), "请填写完整信息", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消") { _, _ ->
                selectedImageUri = null
                currentImageFile = null
            }
            .show()
    }
    
    private fun saveDiary(restaurantName: String, content: String, rating: Float) {
        Log.d(TAG, "saveDiary: 保存日记 - $restaurantName")
        lifecycleScope.launch {
            try {
                val imagePath = selectedImageUri?.let { saveImageToInternalStorage(it) } ?: ""
                
                val newDiary = Diary(
                    id = 0,
                    restaurantName = restaurantName,
                    content = content,
                    imagePath = imagePath,
                    rating = rating,
                    createTime = Date()
                )
                
                withContext(Dispatchers.IO) {
                    database.diaryDao().insertDiary(newDiary)
                }
                
                loadDiaries()
                
                selectedImageUri = null
                currentImageFile = null
                
                Toast.makeText(requireContext(), "日记已保存", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "saveDiary: 日志保存成功")
            } catch (e: Exception) {
                Log.e(TAG, "saveDiary: 保存失败", e)
                Toast.makeText(requireContext(), "保存失败：${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun saveImageToInternalStorage(uri: Uri): String {
        Log.d(TAG, "saveImageToInternalStorage: 保存图片到内部存储")
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "diary_$timeStamp.jpg"
        val outputFile = File(requireContext().filesDir, "diary_images")
        if (!outputFile.exists()) {
            outputFile.mkdirs()
        }
        val imageFile = File(outputFile, fileName)
        
        inputStream?.use { input ->
            FileOutputStream(imageFile).use { output ->
                input.copyTo(output)
            }
        }
        
        return imageFile.absolutePath
    }
    
    private fun loadDiaries() {
        Log.d(TAG, "loadDiaries: 开始加载日记")
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    diaries.clear()
                    diaries.addAll(database.diaryDao().getAllDiaries().first())
                }
                
                adapter = DiaryAdapter(diaries)
                recyclerView.adapter = adapter
                Log.d(TAG, "loadDiaries: 加载成功，共 ${diaries.size} 条日记")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "loadDiaries: 加载失败", e)
                Toast.makeText(requireContext(), "加载失败：${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
