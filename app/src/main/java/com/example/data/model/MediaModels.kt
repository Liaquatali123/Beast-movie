package com.example.data.model

enum class MediaType(val displayName: String) {
    MOVIE("Movie"),
    SERIES("Series"),
    ANIME("Anime")
}

data class MediaItem(
    val id: String,
    val title: String,
    val type: MediaType,
    val synopsis: String,
    val posterUrl: String,
    val backdropUrl: String,
    val releaseYear: String,
    val rating: Double,
    val genres: List<String>,
    val runtime: String, // e.g. "2h 46m" or "5 Seasons"
    val ageRating: String = "PG-13",
    val isFeatured: Boolean = false,
    val isTrending: Boolean = false,
    val isPopular: Boolean = false,
    val streamUrl: String = "",
    val seasons: List<Season> = emptyList(),
    val cast: List<CastMember> = emptyList(),
    val reviews: List<Review> = emptyList()
)

data class Season(
    val id: String,
    val seriesId: String,
    val seasonNumber: Int,
    val title: String,
    val episodeCount: Int,
    val thumbnailUrl: String,
    val episodes: List<Episode> = emptyList()
)

data class Episode(
    val id: String,
    val seasonId: String,
    val episodeNumber: Int,
    val title: String,
    val synopsis: String,
    val duration: String,
    val thumbnailUrl: String,
    val streamUrl: String
)

data class CastMember(
    val id: String,
    val name: String,
    val role: String,
    val photoUrl: String
)

data class Review(
    val id: String,
    val author: String,
    val rating: Double,
    val date: String,
    val content: String
)

data class SubtitleTrack(
    val id: String,
    val language: String,
    val label: String,
    val url: String = ""
)

data class VideoQualityOption(
    val label: String,
    val resolution: String,
    val bitrate: String
)
