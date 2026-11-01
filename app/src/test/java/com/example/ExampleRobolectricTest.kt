package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.api.dto.ApiResponse
import com.example.data.api.dto.HomeResponseData
import com.example.data.api.dto.ImageCoverDto
import com.example.data.api.dto.OperatingSectionDto
import com.example.data.api.dto.SubjectDto
import com.example.data.api.dto.TrailerDto
import com.example.data.api.dto.VideoAddressDto
import com.example.data.local.BeastMoviesDatabase
import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.data.model.toMediaItem
import com.example.data.repository.MovieRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context matches app name`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Beast Movies", appName)
    }

    @Test
    fun `room database watchlist operations test`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = BeastMoviesDatabase.getInstance(context)
        val repository = MovieRepository(db)

        val testItem = MediaItem(
            id = "test_sub_101",
            title = "The Terminal List",
            type = MediaType.SERIES,
            synopsis = "Navy SEAL James Reece investigates.",
            posterUrl = "https://pbcdnw.aoneroom.com/image/2026/10/19/226d9f8342160ea2a77368ecb6f4e4ae.jpg",
            backdropUrl = "https://pbcdnw.aoneroom.com/image/2026/10/19/226d9f8342160ea2a77368ecb6f4e4ae.jpg",
            releaseYear = "2022",
            rating = 7.9,
            genres = listOf("Action", "Drama", "Thriller"),
            runtime = "1 Season",
            streamUrl = "https://macdn.aoneroom.com/media/vone/2024/03/19/0391673747f71d3b6c88e047a10f67ac-sd.mp4"
        )

        // Initially not in watchlist
        assertFalse(repository.isInWatchlist(testItem.id).first())

        // Add to watchlist
        repository.addToWatchlist(testItem)
        assertTrue(repository.isInWatchlist(testItem.id).first())

        val list = repository.getWatchlist().first()
        assertTrue(list.any { it.id == testItem.id && it.title == "The Terminal List" })

        // Remove from watchlist
        repository.removeFromWatchlist(testItem.id)
        assertFalse(repository.isInWatchlist(testItem.id).first())
    }

    @Test
    fun `room database continue watching save and restore test`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = BeastMoviesDatabase.getInstance(context)
        val repository = MovieRepository(db)

        val testItem = MediaItem(
            id = "test_sub_cw_1",
            title = "Avatar: Seven Havens",
            type = MediaType.ANIME,
            synopsis = "Animated action epic.",
            posterUrl = "https://pbcdnw.aoneroom.com/image/test.jpg",
            backdropUrl = "https://pbcdnw.aoneroom.com/image/test.jpg",
            releaseYear = "2026",
            rating = 8.0,
            genres = listOf("Animation", "Action"),
            runtime = "24m",
            streamUrl = "https://macdn.aoneroom.com/media/vone/2026/09/21/8f4be6d103d1e0bd0be7c2a25b94911b-ld.mp4"
        )

        repository.saveContinueWatching(
            item = testItem,
            positionMs = 45000L,
            durationMs = 144000L,
            episodeId = "cw_ep_1",
            episodeTitle = "Episode 1"
        )

        val saved = repository.getContinueWatchingItem(testItem.id)
        assertNotNull(saved)
        assertEquals("cw_ep_1", saved?.id)
        assertEquals(testItem.id, saved?.mediaId)
        assertEquals(45000L, saved?.progressMs)
        assertEquals(144000L, saved?.durationMs)

        val allCw = repository.getContinueWatching().first()
        assertTrue(allCw.any { it.mediaId == testItem.id })

        repository.removeContinueWatching("cw_ep_1")
    }

    @Test
    fun `subject dto to media item conversion test`() {
        val dto = SubjectDto(
            subjectId = "6377932688309244960",
            subjectType = 2,
            title = "The Terminal List",
            description = "Action series",
            releaseDate = "2022-07-01",
            duration = 3600L,
            genre = "Action,Drama,Thriller",
            countryName = "United States",
            imdbRatingValue = "7.9",
            cover = ImageCoverDto(url = "https://pbcdnw.aoneroom.com/image/cover.jpg"),
            trailer = TrailerDto(
                videoAddress = VideoAddressDto(
                    videoId = "12345",
                    url = "https://macdn.aoneroom.com/media/vone/stream.mp4",
                    duration = 141L
                )
            )
        )

        val mediaItem = dto.toMediaItem()
        assertEquals("6377932688309244960", mediaItem.id)
        assertEquals("The Terminal List", mediaItem.title)
        assertEquals(MediaType.SERIES, mediaItem.type)
        assertEquals(7.9, mediaItem.rating, 0.01)
        assertEquals("2022", mediaItem.releaseYear)
        assertEquals("https://macdn.aoneroom.com/media/vone/stream.mp4", mediaItem.streamUrl)
        assertEquals(3, mediaItem.genres.size)
        assertTrue(mediaItem.genres.contains("Action"))
    }

    @Test
    fun `moshi json deserialization of home response test`() {
        val json = """
        {
            "code": 0,
            "message": "ok",
            "data": {
                "operatingList": [
                    {
                        "type": "SUBJECTS_MOVIE",
                        "title": "Popular Movie",
                        "subjects": [
                            {
                                "subjectId": "1436232475147128696",
                                "subjectType": 1,
                                "title": "The Dog Stars",
                                "releaseDate": "2026-08-28",
                                "genre": "Action,Sci-Fi",
                                "imdbRatingValue": "6.4",
                                "cover": { "url": "https://pbcdnw.aoneroom.com/image/cover.jpg" },
                                "trailer": {
                                    "videoAddress": {
                                        "videoId": "6591440071164319216",
                                        "url": "https://macdn.aoneroom.com/media/vone/trailer.mp4"
                                    }
                                }
                            }
                        ]
                    }
                ]
            }
        }
        """.trimIndent()

        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        val type = com.squareup.moshi.Types.newParameterizedType(ApiResponse::class.java, HomeResponseData::class.java)
        val adapter = moshi.adapter<ApiResponse<HomeResponseData>>(type)
        val response = adapter.fromJson(json)

        assertNotNull(response)
        assertEquals(0, response?.code)
        assertEquals("ok", response?.message)
        assertNotNull(response?.data?.operatingList)
        assertEquals("SUBJECTS_MOVIE", response?.data?.operatingList?.firstOrNull()?.type)
    }
}
