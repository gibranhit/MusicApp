package com.gibran.feature.search.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.gibran.core.domain.model.Track
import com.gibran.feature.search.data.api.SearchApi
import com.gibran.feature.search.data.paging.SearchPagingSource
import com.gibran.feature.search.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val api: SearchApi
) : SearchRepository {

    override fun searchTracks(query: String): Flow<PagingData<Track>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { SearchPagingSource(api, query) }
        ).flow
    }
}