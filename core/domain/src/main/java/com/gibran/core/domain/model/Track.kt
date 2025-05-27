package com.gibran.core.domain.model

data class Track(
    val id: Long,
    val title: String,
    val previewUrl: String?,
    val duration: Int,
    val artist: String,
    val albumTitle: String,
    val albumCoverUrl: String,
    val explicitLyrics: Boolean,
    val rank: Int,
    val isFavorite: Boolean = false
)
