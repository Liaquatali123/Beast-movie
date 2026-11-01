package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.BeastMoviesDatabase
import com.example.data.model.MediaType
import com.example.data.repository.MovieRepository
import org.junit.Assert.assertEquals
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
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Beast Movies", appName)
    }

    @Test
    fun `repository returns movies and series data`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = BeastMoviesDatabase.getInstance(context)
        val repository = MovieRepository(db)

        val featured = repository.getFeatured()
        assertNotNull(featured)

        val movies = repository.getAllMovies()
        assertTrue(movies.isNotEmpty())
        assertTrue(movies.any { it.title.contains("Dune") })

        val series = repository.getAllSeries()
        assertTrue(series.isNotEmpty())
        assertTrue(series.any { it.title.contains("Breaking Bad") })

        val anime = repository.getAllAnime()
        assertTrue(anime.isNotEmpty())
        assertTrue(anime.any { it.title.contains("Attack on Titan") })

        val searchResults = repository.search("John Wick")
        assertTrue(searchResults.isNotEmpty())
        assertEquals("John Wick: Chapter 4", searchResults.first().title)
    }
}
