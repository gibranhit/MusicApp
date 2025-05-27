package com.gibran.feature.home.data.api


import com.gibran.core.data.TrackListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeApi {
    @GET("chart/0/tracks?")
    suspend fun getTopTracks(
        @Query("index") index: Int,
        @Query("limit") limit: Int
    ): TrackListResponse
}
