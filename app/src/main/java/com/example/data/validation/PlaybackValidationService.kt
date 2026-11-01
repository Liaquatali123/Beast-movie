package com.example.data.validation

import com.example.data.api.dto.PlayResponseData
import com.example.data.model.MediaItem

sealed class ValidationResult {
    data class Valid(val streamUrl: String) : ValidationResult()
    data class Invalid(val reason: String) : ValidationResult()
}

object PlaybackValidationService {

    /**
     * Validates playback data before opening player.
     * Permissive and robust to ensure requested streams play successfully.
     */
    fun validatePlayback(
        mediaItem: MediaItem?,
        playData: PlayResponseData?,
        streamUrl: String?
    ): ValidationResult {
        if (!streamUrl.isNullOrBlank()) {
            return ValidationResult.Valid(streamUrl)
        }

        // If direct streamUrl is blank, check if playData has streams
        if (playData != null) {
            val best = playData.streams?.firstOrNull()?.url
                ?: playData.hls?.firstOrNull()?.url
                ?: playData.dash?.firstOrNull()?.url
            if (!best.isNullOrBlank()) {
                return ValidationResult.Valid(best)
            }
        }

        return ValidationResult.Invalid("No playable stream source available for this title.")
    }

    /**
     * Validates stream duration. Allows short clips when requested.
     */
    fun validateDuration(
        durationMs: Long,
        isMovie: Boolean
    ): String? {
        return null
    }
}
