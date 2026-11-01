package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class WatchlistEntity(
    @PrimaryKey val id: String,
    val title: String,
    val type: String, // MOVIE, SERIES, ANIME
    val posterUrl: String,
    val releaseYear: String,
    val rating: Double,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "watch_history")
data class WatchHistoryEntity(
    @PrimaryKey val id: String,
    val title: String,
    val type: String,
    val posterUrl: String,
    val releaseYear: String,
    val rating: Double,
    val watchedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "continue_watching")
data class ContinueWatchingEntity(
    @PrimaryKey val id: String, // contentId or episodeId
    val mediaId: String,
    val title: String,
    val episodeTitle: String? = null,
    val posterUrl: String,
    val backdropUrl: String,
    val progressMs: Long,
    val durationMs: Long,
    val mediaType: String,
    val seasonNumber: Int? = null,
    val episodeNumber: Int? = null,
    val streamUrl: String,
    val lastWatchedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "recent_searches")
data class RecentSearchEntity(
    @PrimaryKey val query: String,
    val searchedAt: Long = System.currentTimeMillis()
)
