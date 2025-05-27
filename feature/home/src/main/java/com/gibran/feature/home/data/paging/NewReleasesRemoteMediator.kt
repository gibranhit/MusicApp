package com.gibran.feature.home.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.gibran.core.data.mapper.toDomain
import com.gibran.core.database.datasource.TrackDataSource
import com.gibran.core.database.entity.TrackEntity
import com.gibran.core.database.mappers.toEntity
import com.gibran.feature.home.data.api.HomeApi

@OptIn(ExperimentalPagingApi::class)
class DeezerRemoteMediator(
    private val api: HomeApi,
    private val dataSource: TrackDataSource
) : RemoteMediator<Int, TrackEntity>() {

    private var currentIndex = 1

    companion object {
        const val LIMIT = 25
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, TrackEntity>
    ): MediatorResult {
        val index = when (loadType) {
            LoadType.REFRESH -> {
                currentIndex = 1
                currentIndex
            }
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                currentIndex += 1
                currentIndex
            }
        }

        return try {
            val response = api.getTopTracks(index, LIMIT)
            val trackEntities = response.data.map { it.toDomain().toEntity() }

            if (loadType == LoadType.REFRESH) dataSource.clearAll()
            dataSource.insertAll(trackEntities)

            MediatorResult.Success(endOfPaginationReached = trackEntities.isEmpty())
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
