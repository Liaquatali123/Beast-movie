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
    val runtime: String,
    val ageRating: String = "",
    val isFeatured: Boolean = false,
    val isTrending: Boolean = false,
    val isPopular: Boolean = false,
    val streamUrl: String = "",
    val detailPath: String = "",
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
    val seasonNumber: Int = 1,
    val episodeNumber: Int,
    val title: String,
    val synopsis: String,
    val duration: String,
    val thumbnailUrl: String,
    val streamUrl: String,
    val detailPath: String = ""
)

data class CastMember(val id: String, val name: String, val role: String, val photoUrl: String)
data class Review(val id: String, val author: String, val rating: Double, val date: String, val content: String)
data class SubtitleTrack(val id: String, val language: String, val label: String, val url: String = "")
data class VideoQualityOption(val label: String, val resolution: String, val bitrate: String = "")

private fun Any?.asMap(): Map<String, Any?> = (this as? Map<*, *>)?.entries
    ?.associate { (key, value) -> key.toString() to value } ?: emptyMap()

private fun Map<String, Any?>.text(key: String): String = this[key]?.toString().orEmpty()
private fun Map<String, Any?>.number(key: String): Double? = (this[key] as? Number)?.toDouble()
    ?: this[key]?.toString()?.toDoubleOrNull()
private fun Map<String, Any?>.stringList(key: String): List<String> =
    (this[key] as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()

/** Converts StreamX/Cinemeta metadata into the app's existing UI model. */
fun Map<String, Any?>.toMediaItem(defaultType: MediaType? = null): MediaItem {
    val images = this["images"].asMap()
    val posterSet = images["poster"].asMap()
    val backgroundSet = images["background"].asMap()
    val idValue = text("imdb_id").ifBlank { text("id") }.ifBlank { text("imdbId") }
    val titleValue = text("name").ifBlank { text("title") }.ifBlank { "Untitled" }
    val rawType = text("type").lowercase()
    val mediaType = when {
        rawType == "series" || rawType == "tv" || rawType == "show" -> MediaType.SERIES
        rawType == "anime" -> MediaType.ANIME
        defaultType != null -> defaultType
        else -> MediaType.MOVIE
    }
    val poster = posterSet["medium"]?.toString()
        ?: posterSet["large"]?.toString()
        ?: text("poster")
    val backdrop = backgroundSet["medium"]?.toString()
        ?: backgroundSet["large"]?.toString()
        ?: text("background")
    val rating = number("imdbRating") ?: number("rating") ?: 0.0
    val year = text("year").ifBlank { text("releaseInfo").take(4) }
    val duration = text("runtime").ifBlank {
        number("runtimeMinutes")?.toInt()?.let { "$it min" } ?: ""
    }
    val castList = stringList("cast")
    val castMembers = castList.mapIndexed { index, name ->
        CastMember("cast_$index", name, "Cast", "")
    }

    val videos = (this["videos"] as? List<*>)?.map { it.asMap() } ?: emptyList()
    val seasonNumbers = (this["seasons"] as? List<*>)?.mapNotNull { it?.toString()?.toIntOrNull() }
        ?: emptyList()
    val groupedEpisodes = videos.groupBy { it.number("season")?.toInt() ?: 1 }
    val allSeasonNumbers = (seasonNumbers + groupedEpisodes.keys).distinct().sorted()
    val seasons = if (mediaType == MediaType.SERIES || mediaType == MediaType.ANIME) {
        allSeasonNumbers.map { seasonNo ->
            val episodes = groupedEpisodes[seasonNo].orEmpty().mapIndexed { index, video ->
                val episodeNo = video.number("episode")?.toInt() ?: (index + 1)
                val episodeId = video.text("id").ifBlank { "${idValue}_s${seasonNo}_e${episodeNo}" }
                Episode(
                    id = episodeId,
                    seasonId = "${idValue}_s${seasonNo}",
                    seasonNumber = seasonNo,
                    episodeNumber = episodeNo,
                    title = video.text("name").ifBlank { video.text("title") }.ifBlank { "Episode $episodeNo" },
                    synopsis = video.text("overview"),
                    duration = video.text("runtime"),
                    thumbnailUrl = video.text("still").ifBlank { video.text("thumbnail") },
                    streamUrl = ""
                )
            }
            Season(
                id = "${idValue}_s${seasonNo}",
                seriesId = idValue,
                seasonNumber = seasonNo,
                title = "Season $seasonNo",
                episodeCount = episodes.size,
                thumbnailUrl = episodes.firstOrNull()?.thumbnailUrl ?: backdrop,
                episodes = episodes
            )
        }
    } else emptyList()

    return MediaItem(
        id = idValue,
        title = titleValue,
        type = mediaType,
        synopsis = text("description").ifBlank { text("overview") },
        posterUrl = poster,
        backdropUrl = backdrop,
        releaseYear = year,
        rating = rating,
        genres = stringList("genres"),
        runtime = duration,
        ageRating = text("certification"),
        streamUrl = "",
        seasons = seasons,
        cast = castMembers,
        country = stringList("country").joinToString(", ")
    )
}
