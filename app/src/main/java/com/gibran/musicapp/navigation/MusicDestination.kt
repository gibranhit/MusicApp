package com.gibran.musicapp.navigation


import com.gibran.core.domain.model.Track
import kotlinx.serialization.Serializable

@Serializable
object HomeDestination

@Serializable
data class TrackDetailDestination(
    val id: Long,
    val title: String,
    val previewUrl: String?,
    val duration: Int,
    val artist: String,
    val albumTitle: String,
    val albumCoverUrl: String,
    val explicitLyrics: Boolean,
    val rank: Int
) {
    fun toTrack() = Track(
        id = id,
        title = title,
        previewUrl = previewUrl,
        duration = duration,
        artist = artist,
        albumTitle = albumTitle,
        albumCoverUrl = albumCoverUrl,
        explicitLyrics = explicitLyrics,
        rank = rank
    )
}

@Serializable
object SearchDestination

