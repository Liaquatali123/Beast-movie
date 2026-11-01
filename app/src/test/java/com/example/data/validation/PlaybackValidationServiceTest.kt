package com.example.data.validation

import com.example.data.api.dto.PlayResponseData
import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaybackValidationServiceTest {

    @Test
    fun `validatePlayback returns Valid when streamUrl is provided`() {
        val result = PlaybackValidationService.validatePlayback(null, null, "https://example.com/video.mp4")
        assertTrue(result is ValidationResult.Valid)
        assertEquals("https://example.com/video.mp4", (result as ValidationResult.Valid).streamUrl)
    }

    @Test
    fun `validatePlayback allows short clips and trailers when requested`() {
        val item = MediaItem(
            id = "123",
            title = "The Witcher Official Trailer",
            type = MediaType.MOVIE,
            synopsis = "",
            posterUrl = "",
            backdropUrl = "",
            releaseYear = "2021",
            rating = 8.0,
            genres = emptyList(),
            runtime = "2m"
        )
        val result = PlaybackValidationService.validatePlayback(item, null, "https://example.com/trailer.mp4")
        assertTrue(result is ValidationResult.Valid)
        assertEquals("https://example.com/trailer.mp4", (result as ValidationResult.Valid).streamUrl)
    }

    @Test
    fun `validateDuration allows short clip durations`() {
        val durationMs = 150000L // 2.5 minutes
        val err = PlaybackValidationService.validateDuration(durationMs, isMovie = true)
        assertEquals(null, err)
    }
}
