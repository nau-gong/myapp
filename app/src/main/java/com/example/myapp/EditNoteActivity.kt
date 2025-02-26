package com.example.myapp

import android.R
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
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
        setupRichEditor()
        loadNoteData()
    }

    private fun loadNoteData() {
        if (noteId != 0) {
            binding.etTitle.setText(intent.getStringExtra("TITLE"))
            binding.editor.html = intent.getStringExtra("CONTENT")
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
            val content = binding.editor.html
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

    private fun setupRichEditor(){
        binding.editor.setEditorFontSize(22)
        binding.editor.setEditorFontColor(Color.BLACK)
        binding.editor.setEditorBackgroundColor(Color.WHITE)
        binding.editor.setEditorHeight(200)
        binding.editor.setPadding(10, 10, 10, 10)
        binding.editor.setInputEnabled(true)
        binding.editor.setPlaceholder("Type something...")
        
        

        binding.actionUndo.setOnClickListener { binding.editor.undo() }

        binding.actionRedo.setOnClickListener { binding.editor.redo() }

        binding.actionBold.setOnClickListener { binding.editor.setBold() }

        binding.actionItalic.setOnClickListener { binding.editor.setItalic() }

        binding.actionSubscript.setOnClickListener { binding.editor.setSubscript() }

        binding.actionSuperscript.setOnClickListener { binding.editor.setSuperscript() }

        binding.actionStrikethrough.setOnClickListener { binding.editor.setStrikeThrough() }

        binding.actionUnderline.setOnClickListener { binding.editor.setUnderline() }

        binding.actionHeading1.setOnClickListener {
            binding.editor.setHeading(
                1
            )
        }

        binding.actionHeading2.setOnClickListener {
            binding.editor.setHeading(
                2
            )
        }

        binding.actionHeading3.setOnClickListener {
            binding.editor.setHeading(
                3
            )
        }

        binding.actionHeading4.setOnClickListener {
            binding.editor.setHeading(
                4
            )
        }

        binding.actionHeading5.setOnClickListener {
            binding.editor.setHeading(
                5
            )
        }

        binding.actionHeading6.setOnClickListener {
            binding.editor.setHeading(
                6
            )
        }

        binding.actionTxtColor.setOnClickListener(object : View.OnClickListener {
            private var isChanged = false

            override fun onClick(v: View) {
                binding.editor.setTextColor(if (isChanged) Color.BLACK else Color.RED)
                isChanged = !isChanged
            }
        })

        binding.actionBgColor.setOnClickListener(object : View.OnClickListener {
            private var isChanged = false

            override fun onClick(v: View) {
                binding.editor.setTextBackgroundColor(if (isChanged) Color.TRANSPARENT else Color.YELLOW)
                isChanged = !isChanged
            }
        })

        binding.actionIndent.setOnClickListener { binding.editor.setIndent() }

        binding.actionOutdent.setOnClickListener { binding.editor.setOutdent() }

        binding.actionAlignLeft.setOnClickListener { binding.editor.setAlignLeft() }

        binding.actionAlignCenter.setOnClickListener { binding.editor.setAlignCenter() }

        binding.actionAlignRight.setOnClickListener { binding.editor.setAlignRight() }

        binding.actionBlockquote.setOnClickListener { binding.editor.setBlockquote() }

        binding.actionInsertBullets.setOnClickListener { binding.editor.setBullets() }

        binding.actionInsertNumbers.setOnClickListener { binding.editor.setNumbers() }

        binding.actionInsertImage.setOnClickListener {
            binding.editor.insertImage(
                "https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg",
                "dachshund", 320
            )
        }

        binding.actionInsertYoutube.setOnClickListener {
            binding.editor.insertYoutubeVideo(
                "https://www.youtube.com/embed/pS5peqApgUA"
            )
        }

        binding.actionInsertAudio.setOnClickListener {
            binding.editor.insertAudio(
                "https://file-examples-com.github.io/uploads/2017/11/file_example_MP3_5MG.mp3"
            )
        }

        binding.actionInsertVideo.setOnClickListener {
            binding.editor.insertVideo(
                "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/1080/Big_Buck_Bunny_1080_10s_10MB.mp4",
                360
            )
        }

        binding.actionInsertLink.setOnClickListener {
            binding.editor.insertLink(
                "https://github.com/wasabeef",
                "wasabeef"
            )
        }
        binding.actionInsertCheckbox.setOnClickListener { binding.editor.insertTodo() }
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