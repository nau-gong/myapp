package com.example.myapp

import androidx.lifecycle.LiveData
import java.security.MessageDigest

class NoteRepository(private val userDao: UserDao, private val noteDao: NoteDao) {
    // 用户操作
    suspend fun registerUser(username: String, password: String): Boolean {
        return if (userDao.getUserByUsername(username) == null) {
            userDao.insert(User(username = username, password = sha256(password)))
            true
        } else {
            false
        }
    }

    suspend fun loginUser(username: String, password: String): User? {
        return userDao.getUser(username, sha256(password))
    }

    fun getNotes(userId: Int): LiveData<List<Note>> {
        return noteDao.getNotesByUser(userId)
    }

    fun searchNotes(userId: Int, query: String): LiveData<List<Note>> {
        return noteDao.searchNotes(userId, query)
    }

    suspend fun saveNote(note: Note) {
        if (note.id == 0) {
            noteDao.insert(note)
        } else {
            noteDao.update(note)
        }
    }

    suspend fun deleteNote(noteId: Int) {
        noteDao.deleteNote(noteId)
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

}