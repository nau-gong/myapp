package com.example.myapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import java.util.Calendar


class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository

    private var currentLiveData: LiveData<List<Note>>? = null

    init {
        val db = AppDatabase.getDatabase(application)
        repository = NoteRepository(db.userDao(), db.noteDao())
    }

    // 加载笔记（带搜索功能）
    fun loadNotes(userId: Int, query: String = ""): LiveData<List<Note>>  {
        return if (query.isEmpty()) {
             repository.getNotes(userId)
        } else {
             repository.searchNotes(userId, query)
        }
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
    suspend fun saveNote(userId: Int, noteId: Int, title: String, content: String,category: String, isFavorite: Boolean, pic:String?) {
        val note = Note(id = noteId, userId = userId, title = title, content = content,category = category,isFavorite = isFavorite, pic = pic)
        repository.saveNote(note)
    }


}