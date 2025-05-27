package com.gibran.core.data

import com.google.gson.annotations.SerializedName

data class TrackListResponse(
    @SerializedName("data") val data: List<TrackItemResponse>,
    @SerializedName("next") val next: String?,
    @SerializedName("total") val total: Int?
)

data class TrackItemResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("preview") val preview: String?,
    @SerializedName("duration") val duration: Int,
    @SerializedName("explicit_lyrics") val explicitLyrics: Boolean,
    @SerializedName("rank") val rank: Int,
    @SerializedName("artist") val artist: ArtistResponse,
    @SerializedName("album") val album: AlbumResponse
)

data class ArtistResponse(
    @SerializedName("name") val name: String
)

data class AlbumResponse(
    @SerializedName("title") val title: String,
    @SerializedName("cover_medium") val cover: String
)
