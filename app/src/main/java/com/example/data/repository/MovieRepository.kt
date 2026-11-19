package com.example.data.repository

import com.example.data.api.ApiClient
import com.example.data.api.BeastApiService
import com.example.data.api.dto.PlayResponseData
import com.example.data.local.BeastMoviesDatabase
import com.example.data.local.ContinueWatchingEntity
import com.example.data.local.RecentSearchEntity
import com.example.data.local.WatchHistoryEntity
import com.example.data.local.WatchlistEntity
import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.data.model.toMediaItem
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.ConcurrentHashMap

data class HomeFeedData(
    val featured: MediaItem?,
    val trendingMovies: List<MediaItem>,
    val popularSeries: List<MediaItem>,
    val trendingAnime: List<MediaItem>,
    val sections: List<HomeSection>
)

data class HomeSection(val title: String, val items: List<MediaItem>)

class MovieRepository(
    private val database: BeastMoviesDatabase,
    private val apiService: BeastApiService = ApiClient.service
) {
    private val watchlistDao = database.watchlistDao()
    private val watchHistoryDao = database.watchHistoryDao()
    private val continueWatchingDao = database.continueWatchingDao()
    private val recentSearchDao = database.recentSearchDao()
    private val cachedItemsMap = ConcurrentHashMap<String, MediaItem>()

    fun cacheItem(item: MediaItem) { if (item.id.isNotBlank()) cachedItemsMap[item.id] = item }
    fun getMediaItemById(id: String): MediaItem? = cachedItemsMap[id]

    private fun mapItems(response: Map<String, Any?>, defaultType: MediaType? = null): List<MediaItem> {
        val raw = response["metas"] as? List<*> ?: response["results"] as? List<*> ?: emptyList<Any?>()
        return raw.mapNotNull { value ->
            val map = (value as? Map<*, *>)?.entries?.associate { it.key.toString() to it.value } ?: return@mapNotNull null
            val item = map.toMediaItem(defaultType)
            if (item.id.isNotBlank()) { cacheItem(item); item } else null
        }
    }

    private suspend fun loadCatalog(type: String, genre: String? = null, skip: Int = 0): List<MediaItem> {
        val response = apiService.catalog(type = type, catalog = "top", genre = genre, skip = skip)
        if (response["ok"] == false) throw IllegalStateException(response["error"]?.toString() ?: "StreamX request failed")
        return mapItems(response, if (type == "series") MediaType.SERIES else MediaType.MOVIE)
    }

    suspend fun getHomeFeed(): Result<HomeFeedData> = try {
        val (movies, series, anime) = coroutineScope {
            val movieCall = async { runCatching { apiService.trending("movie", 20) }.getOrElse { loadCatalog("movie") } }
            val seriesCall = async { runCatching { apiService.trending("series", 20) }.getOrElse { loadCatalog("series") } }
            val animeCall = async { runCatching { apiService.searchAnime("popular anime") }.getOrDefault(emptyMap()) }
            Triple(movieCall.await(), seriesCall.await(), animeCall.await())
        }
        val movieItems = mapItems(movies, MediaType.MOVIE)
        val seriesItems = mapItems(series, MediaType.SERIES)
        val animeItems = mapItems(anime, MediaType.ANIME)
        val sections = buildList {
            if (movieItems.isNotEmpty()) add(HomeSection("Trending Movies", movieItems))
            if (seriesItems.isNotEmpty()) add(HomeSection("Popular Series", seriesItems))
            if (animeItems.isNotEmpty()) add(HomeSection("Anime", animeItems))
        }
        if (sections.isEmpty()) Result.failure(Exception("StreamX API returned no catalogue data"))
        else Result.success(HomeFeedData(
            featured = movieItems.firstOrNull()?.copy(isFeatured = true) ?: seriesItems.firstOrNull()?.copy(isFeatured = true),
            trendingMovies = movieItems.take(18),
            popularSeries = seriesItems.take(18),
            trendingAnime = animeItems.take(18),
            sections = sections
        ))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getExploreItems(type: MediaType?, genre: String?, page: Int = 1, perPage: Int = 24): Result<List<MediaItem>> = try {
        val skip = ((page - 1).coerceAtLeast(0)) * perPage
        val genreValue = genre?.takeUnless { it.isBlank() || it.equals("all", true) || it == "All Genres" }
        val items = when (type) {
            MediaType.MOVIE -> loadCatalog("movie", genreValue, skip)
            MediaType.SERIES -> loadCatalog("series", genreValue, skip)
            MediaType.ANIME -> mapItems(apiService.searchAnime(genreValue ?: "anime"), MediaType.ANIME)
            null -> (loadCatalog("movie", genreValue, skip) + loadCatalog("series", genreValue, skip)).distinctBy { it.id }
        }
        Result.success(items)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun search(query: String, page: Int = 1, perPage: Int = 24): Result<List<MediaItem>> = try {
        val skip = ((page - 1).coerceAtLeast(0)) * perPage
        val results = coroutineScope {
            val movies = async { runCatching { apiService.search(query, "movie", skip) }.getOrDefault(emptyMap()) }
            val series = async { runCatching { apiService.search(query, "series", skip) }.getOrDefault(emptyMap()) }
            val anime = async { runCatching { apiService.searchAnime(query) }.getOrDefault(emptyMap()) }
            Triple(movies.await(), series.await(), anime.await())
        }
        val items = (mapItems(results.first, MediaType.MOVIE) +
            mapItems(results.second, MediaType.SERIES) +
            mapItems(results.third, MediaType.ANIME)).distinctBy { it.id }
        Result.success(items)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun getSearchSuggestions(query: String): Result<List<String>> = try {
        val response = apiService.searchImdb(query)
        val values = response["results"] as? List<*> ?: emptyList<Any?>()
        Result.success(values.mapNotNull { value ->
            val map = (value as? Map<*, *>) ?: return@mapNotNull null
            (map["title"] ?: map["name"])?.toString()
        }.distinct().take(10))
    } catch (e: Exception) { Result.success(emptyList()) }

    suspend fun getEveryoneSearch(): Result<Pair<List<String>, List<MediaItem>>> = try {
        val trending = apiService.trending("movie", 10)
        Result.success(Pair(emptyList(), mapItems(trending, MediaType.MOVIE)))
    } catch (e: Exception) { Result.success(Pair(emptyList(), emptyList())) }

    suspend fun getRecommendations(subjectId: String): Result<List<MediaItem>> = try {
        val mediaType = getMediaItemById(subjectId)?.type
        if (mediaType == MediaType.ANIME) {
            Result.success(emptyList())
        } else {
            val type = if (mediaType == MediaType.SERIES) "series" else "movie"
            Result.success(mapItems(apiService.similar(type, subjectId)))
        }
    } catch (e: Exception) { Result.success(emptyList()) }

    /** StreamX has no playback resolver. Never substitute a trailer or hard-coded sample video. */
    suspend fun getPlayStreamData(
        subjectId: String,
        season: Int = 0,
        episode: Int = 0,
        detailPath: String = ""
    ): Pair<String?, PlayResponseData?> = Pair(null, null)

    suspend fun getPlayStreamUrl(
        subjectId: String,
        season: Int = 0,
        episode: Int = 0,
        detailPath: String = ""
    ): String? = null

    suspend fun getFullSubjectDetail(subjectId: String): Result<MediaItem> = try {
        val cached = getMediaItemById(subjectId)
        // StreamX currently exposes anime search only, not a dedicated anime detail route.
        if (cached?.type == MediaType.ANIME) return Result.success(cached)
        val type = if (cached?.type == MediaType.SERIES) "series" else "movie"
        val response = apiService.metadata(type, subjectId)
        if (response["ok"] == false) throw IllegalStateException(response["error"]?.toString() ?: "Metadata unavailable")
        val meta = (response["meta"] as? Map<*, *>)?.entries?.associate { it.key.toString() to it.value }
            ?: throw IllegalStateException("StreamX returned no metadata")
        val item = meta.toMediaItem(cached?.type)
        if (item.id.isBlank()) throw IllegalStateException("Invalid media identifier")
        cacheItem(item)
        Result.success(item)
    } catch (e: Exception) { Result.failure(e) }

    // Local Room persistence — independent of the remote metadata provider.
    fun getWatchlist(): Flow<List<WatchlistEntity>> = watchlistDao.getAll()
    suspend fun addToWatchlist(item: MediaItem) {
        cacheItem(item)
        watchlistDao.insert(WatchlistEntity(id=item.id, title=item.title, type=item.type.name, posterUrl=item.posterUrl, releaseYear=item.releaseYear, rating=item.rating))
    }
    suspend fun removeFromWatchlist(id: String) = watchlistDao.deleteById(id)
    fun isInWatchlist(id: String): Flow<Boolean> = watchlistDao.isInWatchlist(id)
    fun getContinueWatching(): Flow<List<ContinueWatchingEntity>> = continueWatchingDao.getAll()
    suspend fun saveContinueWatching(item: MediaItem, positionMs: Long, durationMs: Long, episodeId: String? = null, episodeTitle: String? = null) {
        cacheItem(item)
        continueWatchingDao.insert(ContinueWatchingEntity(
            id=episodeId ?: item.id, mediaId=item.id, title=item.title, episodeTitle=episodeTitle,
            posterUrl=item.posterUrl, backdropUrl=item.backdropUrl, progressMs=positionMs,
            durationMs=durationMs, mediaType=item.type.name, streamUrl=""
        ))
    }
    suspend fun removeContinueWatching(id: String) = continueWatchingDao.deleteById(id)
    suspend fun getContinueWatchingItem(mediaId: String): ContinueWatchingEntity? = continueWatchingDao.getByMediaId(mediaId)
    fun getWatchHistory(): Flow<List<WatchHistoryEntity>> = watchHistoryDao.getAll()
    suspend fun addToHistory(item: MediaItem) {
        cacheItem(item)
        watchHistoryDao.insert(WatchHistoryEntity(id=item.id, title=item.title, type=item.type.name, posterUrl=item.posterUrl, releaseYear=item.releaseYear, rating=item.rating))
    }
    suspend fun clearHistory() = watchHistoryDao.clearAll()
    suspend fun deleteHistoryItem(id: String) = watchHistoryDao.deleteById(id)
    fun getRecentSearches(): Flow<List<String>> = recentSearchDao.getRecent()
    suspend fun saveSearchQuery(query: String) {
        if (query.isNotBlank()) recentSearchDao.insert(RecentSearchEntity(query=query.trim()))
    }
    suspend fun deleteSearchQuery(query: String) = recentSearchDao.delete(query.trim())
    suspend fun clearAllSearches() = recentSearchDao.clearAll()
}
