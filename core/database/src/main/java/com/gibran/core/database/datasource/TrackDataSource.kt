package com.gibran.core.database.datasource

import androidx.paging.PagingSource
import com.gibran.core.database.dao.TrackDao
import com.gibran.core.database.entity.TrackEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackDataSource @Inject constructor(
    private val dao: TrackDao
) {

    suspend fun insertAll(tracks: List<TrackEntity>) {
        dao.insertAll(tracks)
    }

    suspend fun clearAll() {
        dao.clearAll()
    }

    fun getPagedTracks(): PagingSource<Int, TrackEntity> {
        return dao.getPagedTracks()
    }
}
