package com.example.data.repository

import com.example.data.api.ApiClient
import com.example.data.api.BeastApiService
import com.example.data.api.dto.FilterRequest
import com.example.data.api.dto.SearchRequest
import com.example.data.api.dto.SearchSuggestRequest
import com.example.data.local.BeastMoviesDatabase
import com.example.data.local.ContinueWatchingEntity
import com.example.data.local.RecentSearchEntity
import com.example.data.local.WatchHistoryEntity
import com.example.data.local.WatchlistEntity
import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.data.model.toMediaItem
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.ConcurrentHashMap

data class HomeFeedData(
    val featured: MediaItem?,
    val trendingMovies: List<MediaItem>,
    val popularSeries: List<MediaItem>,
    val trendingAnime: List<MediaItem>,
    val sections: List<HomeSection>
)

data class HomeSection(
    val title: String,
    val items: List<MediaItem>
)

class MovieRepository(
    private val database: BeastMoviesDatabase,
    private val apiService: BeastApiService = ApiClient.service
) {

    private val watchlistDao = database.watchlistDao()
    private val watchHistoryDao = database.watchHistoryDao()
    private val continueWatchingDao = database.continueWatchingDao()
    private val recentSearchDao = database.recentSearchDao()

    // In-memory cache for fast lookups across navigation
    private val cachedItemsMap = ConcurrentHashMap<String, MediaItem>()

    fun cacheItem(item: MediaItem) {
        cachedItemsMap[item.id] = item
    }

    fun getMediaItemById(id: String): MediaItem? {
        return cachedItemsMap[id]
    }

    // ==================== REAL API CALLS ====================

    suspend fun getHomeFeed(): Result<HomeFeedData> {
        return try {
            val response = apiService.getHome()
            if (response.code != 0 || response.data == null) {
                return Result.failure(Exception(response.message.ifBlank { "Failed to load home feed" }))
            }

            val operatingList = response.data.operatingList ?: emptyList()
            var featuredItem: MediaItem? = null
            val sectionsList = mutableListOf<HomeSection>()
            val trendingMovies = mutableListOf<MediaItem>()
            val popularSeries = mutableListOf<MediaItem>()
            val trendingAnime = mutableListOf<MediaItem>()

            for (op in operatingList) {
                if (op.type == "BANNER") {
                    val bannerFirst = op.banner?.items?.firstOrNull()
                    if (bannerFirst?.subject != null) {
                        val item = bannerFirst.subject.toMediaItem(isFeatured = true)
                        cacheItem(item)
                        if (featuredItem == null) featuredItem = item
                    }
                } else if (!op.subjects.isNullOrEmpty()) {
                    val title = op.title ?: "Featured"
                    val items = op.subjects.map {
                        val mi = it.toMediaItem()
                        cacheItem(mi)
                        mi
                    }

                    sectionsList.add(HomeSection(title, items))

                    val lowerTitle = title.lowercase()
                    if (lowerTitle.contains("popular movie") || lowerTitle.contains("action movie") || lowerTitle.contains("nollywood movie")) {
                        trendingMovies.addAll(items)
                    } else if (lowerTitle.contains("popular series") || lowerTitle.contains("superhero series")) {
                        popularSeries.addAll(items)
                    } else if (lowerTitle.contains("animation") || lowerTitle.contains("anime")) {
                        trendingAnime.addAll(items)
                    }
                }
            }

            // Fallbacks for shelves if names differed slightly in response
            if (featuredItem == null) {
                featuredItem = sectionsList.firstOrNull()?.items?.firstOrNull()?.copy(isFeatured = true)
                featuredItem?.let { cacheItem(it) }
            }

            if (trendingMovies.isEmpty()) {
                trendingMovies.addAll(
                    sectionsList.flatMap { it.items }.filter { it.type == MediaType.MOVIE }.take(15)
                )
            }
            if (popularSeries.isEmpty()) {
                popularSeries.addAll(
                    sectionsList.flatMap { it.items }.filter { it.type == MediaType.SERIES }.take(15)
                )
            }
            if (trendingAnime.isEmpty()) {
                trendingAnime.addAll(
                    sectionsList.flatMap { it.items }.filter { it.type == MediaType.ANIME }.take(15)
                )
            }

            Result.success(
                HomeFeedData(
                    featured = featuredItem,
                    trendingMovies = trendingMovies.distinctBy { it.id }.take(18),
                    popularSeries = popularSeries.distinctBy { it.id }.take(18),
                    trendingAnime = trendingAnime.distinctBy { it.id }.take(18),
                    sections = sectionsList
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getExploreItems(type: MediaType?, genre: String?, page: Int = 1, perPage: Int = 24): Result<List<MediaItem>> {
        return try {
            val subjectTypeParam = when (type) {
                MediaType.MOVIE -> 1
                MediaType.SERIES -> 2
                MediaType.ANIME -> null
                null -> null
            }
            val genreParam = if (type == MediaType.ANIME) {
                "Animation"
            } else if (genre.isNullOrBlank() || genre == "All" || genre == "All Genres") {
                null
            } else {
                genre
            }

            val request = FilterRequest(
                page = page,
                perPage = perPage,
                subjectType = subjectTypeParam,
                genre = genreParam
            )
            val response = apiService.filter(request)
            if (response.code != 0 || response.data == null) {
                return Result.failure(Exception(response.message.ifBlank { "Failed to filter content" }))
            }

            val items = response.data.items?.map {
                val mi = it.toMediaItem()
                cacheItem(mi)
                mi
            } ?: emptyList()

            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun search(query: String, page: Int = 1, perPage: Int = 24): Result<List<MediaItem>> {
        return try {
            val request = SearchRequest(
                keyword = query,
                page = page,
                perPage = perPage
            )
            val response = apiService.search(request)
            if (response.code != 0 || response.data == null) {
                return Result.failure(Exception(response.message.ifBlank { "Search failed" }))
            }

            val items = response.data.items?.map {
                val mi = it.toMediaItem()
                cacheItem(mi)
                mi
            } ?: emptyList()

            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSearchSuggestions(query: String): Result<List<String>> {
        return try {
            val request = SearchSuggestRequest(keyword = query, perPage = 10)
            val response = apiService.searchSuggest(request)
            if (response.code != 0 || response.data == null) {
                return Result.success(emptyList())
            }
            val suggestions = response.data.items?.mapNotNull { it.word } ?: emptyList()
            Result.success(suggestions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEveryoneSearch(): Result<Pair<List<String>, List<MediaItem>>> {
        return try {
            val response = apiService.getEveryoneSearch()
            if (response.code != 0 || response.data == null) {
                return Result.success(Pair(emptyList(), emptyList()))
            }
            val hotKeywords = response.data.everyoneSearch?.mapNotNull { it.keyword ?: it.title } ?: emptyList()
            val hotSubjects = response.data.hot?.flatMap { cat ->
                cat.subjects?.map {
                    val mi = it.toMediaItem()
                    cacheItem(mi)
                    mi
                } ?: emptyList()
            } ?: emptyList()
            Result.success(Pair(hotKeywords, hotSubjects))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRecommendations(subjectId: String): Result<List<MediaItem>> {
        return try {
            val response = apiService.getDetailRecommendations(subjectId = subjectId, page = 1, perPage = 12)
            if (response.code != 0 || response.data == null) {
                return Result.success(emptyList())
            }
            val items = response.data.items?.map {
                val mi = it.toMediaItem()
                cacheItem(mi)
                mi
            } ?: emptyList()
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPlayStreamData(subjectId: String, season: Int = 0, episode: Int = 0, detailPath: String = ""): Pair<String?, com.example.data.api.dto.PlayResponseData?> {
        val cached = getMediaItemById(subjectId)
        val fallback = cached?.streamUrl?.ifBlank { null }
            ?: "https://macdn.aoneroom.com/media/vone/2024/03/19/0391673747f71d3b6c88e047a10f67ac-sd.mp4"

        return try {
            val response = apiService.getPlayStream(
                subjectId = subjectId,
                season = season,
                episode = episode,
                detailPath = detailPath,
                streamSignType = 1
            )
            if (response.code == 0 && response.data != null) {
                val data = response.data
                val bestStream = data.streams?.firstOrNull()?.url
                    ?: data.hls?.firstOrNull()?.url
                    ?: data.dash?.firstOrNull()?.url
                Pair(bestStream?.ifBlank { null } ?: fallback, data)
            } else Pair(fallback, null)
        } catch (_: Exception) {
            Pair(fallback, null)
        }
    }

    suspend fun getPlayStreamUrl(subjectId: String, season: Int = 0, episode: Int = 0, detailPath: String = ""): String? {
        return getPlayStreamData(subjectId, season, episode, detailPath).first
    }

    suspend fun getFullSubjectDetail(subjectId: String): Result<MediaItem> {
        return try {
            val response = apiService.getSubjectDetail(subjectId = subjectId)
            if (response.code == 0 && response.data?.subject != null) {
                val item = response.data.subject.toMediaItem()
                cacheItem(item)
                Result.success(item)
            } else {
                Result.failure(Exception("Subject details unavailable"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== ROOM DATABASE PERSISTENCE ====================

    fun getWatchlist(): Flow<List<WatchlistEntity>> = watchlistDao.getAll()

    suspend fun addToWatchlist(item: MediaItem) {
        cacheItem(item)
        val entity = WatchlistEntity(
            id = item.id,
            title = item.title,
            type = item.type.name,
            posterUrl = item.posterUrl,
            releaseYear = item.releaseYear,
            rating = item.rating
        )
        watchlistDao.insert(entity)
    }

    suspend fun removeFromWatchlist(id: String) {
        watchlistDao.deleteById(id)
    }

    fun isInWatchlist(id: String): Flow<Boolean> = watchlistDao.isInWatchlist(id)

    fun getContinueWatching(): Flow<List<ContinueWatchingEntity>> = continueWatchingDao.getAll()

    suspend fun saveContinueWatching(
        item: MediaItem,
        positionMs: Long,
        durationMs: Long,
        episodeId: String? = null,
        episodeTitle: String? = null
    ) {
        cacheItem(item)
        val entity = ContinueWatchingEntity(
            id = episodeId ?: item.id,
            mediaId = item.id,
            title = item.title,
            episodeTitle = episodeTitle,
            posterUrl = item.posterUrl,
            backdropUrl = item.backdropUrl,
            progressMs = positionMs,
            durationMs = durationMs,
            mediaType = item.type.name,
            streamUrl = item.streamUrl
        )
        continueWatchingDao.insert(entity)
    }

    suspend fun removeContinueWatching(id: String) {
        continueWatchingDao.deleteById(id)
    }

    suspend fun getContinueWatchingItem(mediaId: String): ContinueWatchingEntity? {
        return continueWatchingDao.getByMediaId(mediaId)
    }

    fun getWatchHistory(): Flow<List<WatchHistoryEntity>> = watchHistoryDao.getAll()

    suspend fun addToHistory(item: MediaItem) {
        cacheItem(item)
        val entity = WatchHistoryEntity(
            id = item.id,
            title = item.title,
            type = item.type.name,
            posterUrl = item.posterUrl,
            releaseYear = item.releaseYear,
            rating = item.rating
        )
        watchHistoryDao.insert(entity)
    }

    suspend fun clearHistory() {
        watchHistoryDao.clearAll()
    }

    suspend fun deleteHistoryItem(id: String) {
        watchHistoryDao.deleteById(id)
    }

    fun getRecentSearches(): Flow<List<String>> = recentSearchDao.getRecent()

    suspend fun saveSearchQuery(query: String) {
        if (query.isNotBlank()) {
            recentSearchDao.insert(RecentSearchEntity(query = query.trim()))
        }
    }

    suspend fun deleteSearchQuery(query: String) {
        recentSearchDao.delete(query.trim())
    }

    suspend fun clearAllSearches() {
        recentSearchDao.clearAll()
    }
}
