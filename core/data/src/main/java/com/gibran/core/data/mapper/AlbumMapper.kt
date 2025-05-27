package com.gibran.core.data.mapper

import com.gibran.core.data.TrackItemResponse
import com.gibran.core.domain.model.Track


fun TrackItemResponse.toDomain(): Track {
    return Track(
        id = id,
        title = title,
        previewUrl = preview,
        duration = duration,
        artist = artist.name,
        albumTitle = album.title,
        albumCoverUrl = album.cover,
        explicitLyrics = explicitLyrics,
        rank = rank
    )
}
