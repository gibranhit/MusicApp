package com.gibran.feature.search.data.api

import com.gibran.core.data.TrackListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {
    @GET("search")
    suspend fun searchTracks(
        @Query("q") query: String,
        @Query("index") index: Int,
        @Query("limit") limit: Int
    ): TrackListResponse
}