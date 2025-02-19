package com.example.myapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: NoteViewModel
    private lateinit var adapter: NoteAdapter
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userId = intent.getIntExtra("USER_ID", -1)
        viewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        setupRecyclerView()
        observeNotes()
        setupButtons()
    }

    private fun setupRecyclerView() {
        adapter = NoteAdapter { note ->
            val intent = Intent(this, EditNoteActivity::class.java).apply {
                putExtra("USER_ID", userId)
                putExtra("NOTE_ID", note.id)
                putExtra("TITLE", note.title)
                putExtra("CONTENT", note.content)
            }
            startActivityForResult(intent, REQUEST_EDIT_NOTE)
        }
        binding.rvNotes.layoutManager = LinearLayoutManager(this)
        binding.rvNotes.adapter = adapter
    }

    private fun observeNotes() {
        viewModel.getNotes(userId).observe(this) { notes ->
            adapter.submitList(notes)
        }
    }

    private fun setupButtons() {
        binding.btnNewNote.setOnClickListener {
            startActivityForResult(
                Intent(this, EditNoteActivity::class.java).apply {
                    putExtra("USER_ID", userId)
                },
                REQUEST_NEW_NOTE
            )
        }

        binding.btnLogout.setOnClickListener {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            observeNotes() // 刷新列表
        }
    }

    companion object {
        const val REQUEST_NEW_NOTE = 1
        const val REQUEST_EDIT_NOTE = 2
    }
}
