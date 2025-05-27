package com.gibran.feature.home.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.gibran.core.database.datasource.TrackDataSource
import com.gibran.core.database.mappers.toDomain
import com.gibran.core.domain.model.Track
import com.gibran.feature.home.data.api.HomeApi
import com.gibran.feature.home.data.paging.DeezerRemoteMediator
import com.gibran.feature.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val api: HomeApi,
    private val trackDataSource: TrackDataSource
) : HomeRepository {

    companion object {
        const val MAX_ITEMS = 10
        const val PREFETCH_ITEMS = 5
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getNewReleases(): Flow<PagingData<Track>> {
        return Pager(
            config = PagingConfig(
                pageSize = MAX_ITEMS,
                prefetchDistance = PREFETCH_ITEMS
            ),
            remoteMediator = DeezerRemoteMediator(api, trackDataSource),
            pagingSourceFactory = { trackDataSource.getPagedTracks() }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

}
