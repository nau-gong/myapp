package com.example.myapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = NoteRepository(db.userDao(), db.noteDao())
    }

    // 用户登录
    suspend fun login(username: String, password: String): User? {
        return repository.loginUser(username, password)
    }

    // 用户注册
    suspend fun register(username: String, password: String): Boolean {
        return repository.registerUser(username, password)
    }

    // 获取笔记列表
    fun getNotes(userId: Int): LiveData<List<Note>> = repository.getNotes(userId)

    // 保存笔记（新建或更新）
    suspend fun saveNote(userId: Int, noteId: Int, title: String, content: String) {
        val note = Note(id = noteId, userId = userId, title = title, content = content)
        repository.saveNote(note)
    }
}