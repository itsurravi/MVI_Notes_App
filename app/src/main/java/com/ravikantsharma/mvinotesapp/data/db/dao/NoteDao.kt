package com.ravikantsharma.mvinotesapp.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ravikantsharma.mvinotesapp.data.db.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM Note")
    fun fetchAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM Note WHERE id =:noteId")
    fun getNote(noteId: Long): Flow<Note>
}