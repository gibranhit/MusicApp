package com.gibran.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val artist: String,
    val albumTitle: String,
    val albumCoverUrl: String,
    val previewUrl: String?,
    val duration: Int,
    val explicitLyrics: Boolean,
    val rank: Int,
)
