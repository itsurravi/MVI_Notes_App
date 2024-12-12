package com.ravikantsharma.mvinotesapp.data.repository

import com.ravikantsharma.mvinotesapp.data.db.dao.NoteDao
import com.ravikantsharma.mvinotesapp.data.db.model.Note
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val noteDao: NoteDao
) {
    suspend fun insertNote(note: Note) = noteDao.insertNote(note)
    suspend fun updateNote(note: Note) = noteDao.updateNote(note)
    suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)
    fun getNotes(): Flow<List<Note>> = noteDao.fetchAllNotes()
    fun getNote(noteId: Long): Flow<Note> = noteDao.getNote(noteId)
}