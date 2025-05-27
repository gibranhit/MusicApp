package com.gibran.core.database.mappers

import com.gibran.core.database.entity.TrackEntity
import com.gibran.core.domain.model.Track

fun Track.toEntity(): TrackEntity {
    return TrackEntity(
        id = id,
        title = title,
        artist = artist,
        albumTitle = albumTitle,
        albumCoverUrl = albumCoverUrl,
        previewUrl = previewUrl,
        duration = duration,
        explicitLyrics = explicitLyrics,
        rank = rank,
    )
}

fun TrackEntity.toDomain(isFavorite: Boolean = false): Track {
    return Track(
        id = id,
        title = title,
        artist = artist,
        albumTitle = albumTitle,
        albumCoverUrl = albumCoverUrl,
        previewUrl = previewUrl,
        duration = duration,
        explicitLyrics = explicitLyrics,
        rank = rank,
        isFavorite = isFavorite
    )
}