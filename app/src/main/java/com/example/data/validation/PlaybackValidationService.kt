package com.example.data.validation

import com.example.data.api.dto.PlayResponseData
import com.example.data.model.MediaItem

sealed class ValidationResult {
    data class Valid(val streamUrl: String) : ValidationResult()
    data class Invalid(val reason: String) : ValidationResult()
}

object PlaybackValidationService {
    fun validatePlayback(
        mediaItem: MediaItem?,
        playData: PlayResponseData?,
        streamUrl: String?
    ): ValidationResult {
        val candidate = streamUrl?.takeIf { it.isNotBlank() }
            ?: playData?.streams?.firstOrNull()?.url?.takeIf { it.isNotBlank() }
            ?: playData?.hls?.firstOrNull()?.url?.takeIf { it.isNotBlank() }
            ?: playData?.dash?.firstOrNull()?.url?.takeIf { it.isNotBlank() }

        return if (candidate == null) {
            ValidationResult.Invalid("StreamX provides movie information and legal watch links, not video files. Connect an authorized video source to play this title.")
        } else {
            ValidationResult.Valid(candidate)
        }
    }

    fun validateDuration(durationMs: Long, isMovie: Boolean): String? = null
}
