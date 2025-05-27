package com.gibran.feature.search.domain.repository


import androidx.paging.PagingData
import com.gibran.core.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchTracks(query: String): Flow<PagingData<Track>>
}
