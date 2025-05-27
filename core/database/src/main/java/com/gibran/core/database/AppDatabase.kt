package com.gibran.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gibran.core.database.dao.TrackDao
import com.gibran.core.database.entity.TrackEntity

@Database(entities = [TrackEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteTrackDao(): TrackDao
}
