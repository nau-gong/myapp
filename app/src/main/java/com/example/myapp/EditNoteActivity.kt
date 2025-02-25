package com.example.myapp

import android.R
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.myapp.databinding.ActivityEditNoteBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException


class EditNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditNoteBinding
    private lateinit var viewModel: NoteViewModel
    private var userId: Int = -1
    private var noteId: Int = 0

    // 新增字段用于存储分类
    private var category: String? = null
    private var isFavorite: Boolean = false
    private var picBase64: String? = null

    private val categories = listOf("工作", "重要", "生活", "其他")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[NoteViewModel::class.java]
        userId = intent.getIntExtra("USER_ID", -1)
        noteId = intent.getIntExtra("NOTE_ID", 0)
        category = intent.getStringExtra("CATEGORY")
        isFavorite = intent.getBooleanExtra("IS_FAVORITE", false)
        picBase64 = intent.getStringExtra("PIC")

        setupCategorySpinner()
        setupButtons()
        loadNoteData()
    }

    private fun loadNoteData() {
        if (noteId != 0) {
            binding.etTitle.setText(intent.getStringExtra("TITLE"))
            binding.etContent.setText(intent.getStringExtra("CONTENT"))

            binding.cbFavorite.isChecked = isFavorite
            category?.let {
                binding.spinnerCategory.setSelection(categories.indexOf(it).takeIf { it >= 0 } ?: 0)
            }

            picBase64?.let {
                val bitmap: Bitmap? = decodeBase64ToBitmap(it)
                binding.ivPic.setImageBitmap(bitmap)
                binding.ivPic.visibility = View.VISIBLE
            }

        }
    }

    // 设置分类选择器（Spinner）
    private fun setupCategorySpinner() {
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter

    }

    private fun setupButtons() {
        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val content = binding.etContent.text.toString()
            val category = binding.spinnerCategory.selectedItem.toString()
            isFavorite = binding.cbFavorite.isChecked

            lifecycleScope.launch {
                viewModel.saveNote(userId, noteId, title, content, category, isFavorite, picBase64)
                setResult(RESULT_OK)
                finish()
            }
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnPic.setOnClickListener {
            ImagePicker.with(this)
                .cropSquare()
                .compress(1024)         //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    512,
                    512
                )  //Final image resolution will be less than 1080 x 1080(Optional)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!
                try {
                    // Uri 转换为 Bitmap
                    val inputStream = contentResolver.openInputStream(fileUri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)

                    if (bitmap != null) {
                        binding.ivPic.setImageBitmap(bitmap)
                        binding.ivPic.visibility = View.VISIBLE
                        picBase64 = encodeToBase64(bitmap) // 转为 Base64 并保存
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    private fun encodeToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val bytes = outputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    fun decodeBase64ToBitmap(base64String: String): Bitmap? {
        var base64String = base64String
        try {
            // 去掉前缀（如果 Base64 包含如 `data:image/png;base64,` 的头部信息）
            if (base64String.contains(",")) {
                base64String = base64String.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()[1]
            }

            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            return null
        }
    }
}