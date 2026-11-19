package com.example.data.api.dto

/**
 * Kept only as an empty compatibility type for the player UI.
 * StreamX supplies metadata and legal watch-provider links, not playable stream URLs.
 */
data class PlayResponseData(
    val streams: List<VideoStreamItemDto>? = null,
    val hls: List<VideoStreamItemDto>? = null,
    val dash: List<VideoStreamItemDto>? = null
)

data class VideoStreamItemDto(
    val id: String? = null,
    val url: String = ""
)
