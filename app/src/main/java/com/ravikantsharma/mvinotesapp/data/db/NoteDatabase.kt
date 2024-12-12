package com.ravikantsharma.mvinotesapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ravikantsharma.mvinotesapp.data.db.converter.AnnotatedStringConverter
import com.ravikantsharma.mvinotesapp.data.db.converter.TimestampConverter
import com.ravikantsharma.mvinotesapp.data.db.dao.NoteDao
import com.ravikantsharma.mvinotesapp.data.db.model.Note

@Database(entities = [Note::class], version = 1, exportSchema = false)
@TypeConverters(TimestampConverter::class, AnnotatedStringConverter::class)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}