package com.example.myapp

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface NoteDao {
    @Insert
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Query("SELECT * FROM notes WHERE userId = :userId ORDER BY timestamp DESC")
    fun getNotesByUser(userId: Int): LiveData<List<Note>>

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNote(noteId: Int)

    // 新增搜索方法
    @Query("""
        SELECT * FROM notes 
        WHERE userId = :userId 
        AND (title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%')
    """)
    fun searchNotes(userId: Int, query: String): LiveData<List<Note>>
}