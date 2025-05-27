package com.gibran.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gibran.core.database.entity.TrackEntity

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tracks: List<TrackEntity>)

    @Query("DELETE FROM tracks")
    suspend fun clearAll()

    @Query("SELECT * FROM tracks ORDER BY title ASC")
    fun getPagedTracks(): PagingSource<Int, TrackEntity>
}
