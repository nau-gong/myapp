package com.example.myapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.myapp.databinding.ActivityEditNoteBinding
import kotlinx.coroutines.launch


class EditNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditNoteBinding
    private lateinit var viewModel: NoteViewModel
    private var userId: Int = -1
    private var noteId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[NoteViewModel::class.java]
        userId = intent.getIntExtra("USER_ID", -1)
        noteId = intent.getIntExtra("NOTE_ID", 0)

        loadNoteData()
        setupButtons()
    }

    private fun loadNoteData() {
        if (noteId != 0) {
            binding.etTitle.setText(intent.getStringExtra("TITLE"))
            binding.etContent.setText(intent.getStringExtra("CONTENT"))
        }
    }

    private fun setupButtons() {
        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val content = binding.etContent.text.toString()

            lifecycleScope.launch {
                viewModel.saveNote(userId, noteId, title, content)
                setResult(RESULT_OK)
                finish()
            }
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }
}