package com.gibran.feature.search.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.gibran.core.data.mapper.toDomain
import com.gibran.core.domain.model.Track
import com.gibran.feature.search.data.api.SearchApi

class SearchPagingSource(
    private val api: SearchApi,
    private val query: String
): PagingSource<Int, Track>() {

    companion object {
        const val LIMIT = 10
    }

    override fun getRefreshKey(state: PagingState<Int, Track>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Track> {
        val index = params.key ?: 0
        return try {
            val response = api.searchTracks(query, index, LIMIT)
            val tracks = response.data.map { it.toDomain() }

            LoadResult.Page(
                data = tracks,
                prevKey = if (index == 0) null else index - 1,
                nextKey = if (tracks.isEmpty()) null else index + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}