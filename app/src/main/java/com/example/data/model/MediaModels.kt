package com.example.data.model

import com.example.data.api.dto.SubjectDto

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
    val runtime: String,
    val ageRating: String = "PG-13",
    val isFeatured: Boolean = false,
    val isTrending: Boolean = false,
    val isPopular: Boolean = false,
    val streamUrl: String = "",
    val country: String = "",
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
    val bitrate: String = ""
)

fun SubjectDto.toMediaItem(isFeatured: Boolean = false, isTrending: Boolean = false, isPopular: Boolean = false): MediaItem {
    val genreList = genre?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
    val isAnime = genreList.any { it.contains("anime", ignoreCase = true) || it.contains("animation", ignoreCase = true) }
    val detectedType = when {
        isAnime -> MediaType.ANIME
        subjectType == 2 -> MediaType.SERIES
        else -> MediaType.MOVIE
    }

    val stream = trailer?.videoAddress?.url ?: ""
    val durationSec = duration ?: trailer?.videoAddress?.duration ?: 0L
    val formattedDuration = if (durationSec > 0) {
        val hours = durationSec / 3600
        val mins = (durationSec % 3600) / 60
        if (hours > 0) "${hours}h ${mins}m" else "${mins}m"
    } else if (subjectType == 2) {
        val sCount = season ?: 1
        if (sCount > 1) "$sCount Seasons" else "1 Season"
    } else {
        "Full Length"
    }

    val parsedRating = imdbRatingValue?.toDoubleOrNull() ?: 0.0
    val coverUrl = cover?.url ?: ""
    val stillUrl = stills?.firstOrNull()?.url ?: coverUrl

    val castMembers = staffList?.map {
        CastMember(
            id = it.staffId,
            name = it.name,
            role = it.role.ifBlank { "Cast" },
            photoUrl = it.avatar?.url ?: ""
        )
    } ?: emptyList()

    val seasonsList = if (detectedType == MediaType.SERIES || detectedType == MediaType.ANIME) {
        val sCount = (season ?: 1).coerceAtLeast(1)
        (1..sCount).map { sNum ->
            Season(
                id = "${subjectId}_s$sNum",
                seriesId = subjectId,
                seasonNumber = sNum,
                title = "Season $sNum",
                episodeCount = 1,
                thumbnailUrl = stillUrl,
                episodes = if (stream.isNotBlank()) {
                    listOf(
                        Episode(
                            id = "${subjectId}_s${sNum}_ep1",
                            seasonId = "${subjectId}_s$sNum",
                            episodeNumber = 1,
                            title = "Episode 1: Premiere",
                            synopsis = description ?: "Official stream episode for $title",
                            duration = formattedDuration,
                            thumbnailUrl = stillUrl,
                            streamUrl = stream
                        )
                    )
                } else emptyList()
            )
        }
    } else emptyList()

    return MediaItem(
        id = subjectId,
        title = title ?: "Untitled",
        type = detectedType,
        synopsis = description ?: "",
        posterUrl = coverUrl,
        backdropUrl = stillUrl,
        releaseYear = releaseDate?.take(4) ?: "",
        rating = parsedRating,
        genres = genreList,
        runtime = formattedDuration,
        isFeatured = isFeatured,
        isTrending = isTrending,
        isPopular = isPopular,
        streamUrl = stream,
        country = countryName ?: "",
        seasons = seasonsList,
        cast = castMembers,
        reviews = emptyList()
    )
}
