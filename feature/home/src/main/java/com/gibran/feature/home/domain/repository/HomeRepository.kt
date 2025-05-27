package com.gibran.feature.home.domain.repository

import androidx.paging.PagingData
import com.gibran.core.domain.model.Track
import kotlinx.coroutines.flow.Flow


interface HomeRepository {
    fun getNewReleases(): Flow<PagingData<Track>>
}
