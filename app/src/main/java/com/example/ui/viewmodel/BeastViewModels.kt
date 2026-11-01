package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.ContinueWatchingEntity
import com.example.data.local.PreferencesManager
import com.example.data.local.WatchHistoryEntity
import com.example.data.local.WatchlistEntity
import com.example.data.model.Episode
import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: MovieRepository) : ViewModel() {
    val featuredItem: StateFlow<MediaItem> = MutableStateFlow(repository.getFeatured()).asStateFlow()
    val trendingMovies: StateFlow<List<MediaItem>> = MutableStateFlow(repository.getTrendingMovies()).asStateFlow()
    val popularSeries: StateFlow<List<MediaItem>> = MutableStateFlow(repository.getPopularSeries()).asStateFlow()
    val trendingAnime: StateFlow<List<MediaItem>> = MutableStateFlow(repository.getTrendingAnime()).asStateFlow()

    val continueWatching: StateFlow<List<ContinueWatchingEntity>> = repository.getContinueWatching()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _showNotifications = MutableStateFlow(false)
    val showNotifications = _showNotifications.asStateFlow()

    fun toggleNotifications(show: Boolean) {
        _showNotifications.value = show
    }
}

class ExploreViewModel(private val repository: MovieRepository) : ViewModel() {
    private val _selectedType = MutableStateFlow(MediaType.MOVIE)
    val selectedType = _selectedType.asStateFlow()

    private val _selectedGenre = MutableStateFlow("All")
    val selectedGenre = _selectedGenre.asStateFlow()

    private val _items = MutableStateFlow<List<MediaItem>>(emptyList())
    val items = _items.asStateFlow()

    val genres = listOf("All", "Action", "Adventure", "Drama", "Comedy", "Sci-Fi", "Crime", "Thriller", "Fantasy", "Animation", "Horror")

    init {
        updateItems()
    }

    fun selectType(type: MediaType) {
        _selectedType.value = type
        updateItems()
    }

    fun selectGenre(genre: String) {
        _selectedGenre.value = genre
        updateItems()
    }

    private fun updateItems() {
        val type = _selectedType.value
        val genre = _selectedGenre.value
        _items.value = repository.search("", type = type, genre = genre)
    }
}

class SearchViewModel(private val repository: MovieRepository) : ViewModel() {
    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val _results = MutableStateFlow<List<MediaItem>>(emptyList())
    val results = _results.asStateFlow()

    val trendingKeywords = listOf("Dune", "John Wick", "Anime", "Marvel", "Breaking Bad", "Batman", "Spider-Man")

    val recentSearches: StateFlow<List<String>> = repository.getRecentSearches()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Initial popular results
        _results.value = repository.search("")
    }

    fun setQuery(newQuery: String) {
        _query.value = newQuery
        _results.value = repository.search(newQuery)
        if (newQuery.isNotBlank()) {
            viewModelScope.launch {
                repository.saveRecentSearch(newQuery)
            }
        }
    }

    fun clearSearch() {
        _query.value = ""
        _results.value = repository.search("")
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            repository.clearRecentSearches()
        }
    }
}

class DetailsViewModel(
    private val contentId: String,
    private val repository: MovieRepository
) : ViewModel() {
    val item: MediaItem? = repository.getById(contentId)
    val similarItems: List<MediaItem> = repository.getSimilar(contentId)

    val isInWatchlist: StateFlow<Boolean> = repository.isInWatchlist(contentId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _selectedTab = MutableStateFlow(if (item?.type == MediaType.MOVIE) "Similar" else "Seasons")
    val selectedTab = _selectedTab.asStateFlow()

    private val _selectedSeason = MutableStateFlow(item?.seasons?.firstOrNull()?.seasonNumber ?: 1)
    val selectedSeason = _selectedSeason.asStateFlow()

    fun selectTab(tab: String) {
        _selectedTab.value = tab
    }

    fun selectSeason(seasonNumber: Int) {
        _selectedSeason.value = seasonNumber
    }

    fun toggleWatchlist() {
        item ?: return
        viewModelScope.launch {
            if (isInWatchlist.value) {
                repository.removeFromWatchlist(contentId)
            } else {
                repository.toggleWatchlist(item)
            }
        }
    }
}

class MyListViewModel(private val repository: MovieRepository) : ViewModel() {
    private val _selectedTab = MutableStateFlow("My List") // "My List", "History", "Continue Watching"
    val selectedTab = _selectedTab.asStateFlow()

    val watchlist: StateFlow<List<WatchlistEntity>> = repository.getWatchlist()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val history: StateFlow<List<WatchHistoryEntity>> = repository.getWatchHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val continueWatching: StateFlow<List<ContinueWatchingEntity>> = repository.getContinueWatching()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(tab: String) {
        _selectedTab.value = tab
    }

    fun removeFromWatchlist(id: String) {
        viewModelScope.launch {
            repository.removeFromWatchlist(id)
        }
    }

    fun removeFromHistory(id: String) {
        viewModelScope.launch {
            repository.deleteHistoryItem(id)
        }
    }

    fun removeContinueWatching(id: String) {
        viewModelScope.launch {
            repository.removeContinueWatching(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}

class SettingsViewModel(
    private val preferencesManager: PreferencesManager,
    private val repository: MovieRepository
) : ViewModel() {
    val darkTheme = preferencesManager.darkTheme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val language = preferencesManager.appLanguage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "English")

    val autoQuality = preferencesManager.autoQuality
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val playbackSpeed = preferencesManager.playbackSpeed
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1.0f)

    val subtitleLang = preferencesManager.subtitleLanguage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Off")

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch { preferencesManager.setDarkTheme(enabled) }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch { preferencesManager.setLanguage(lang) }
    }

    fun setAutoQuality(enabled: Boolean) {
        viewModelScope.launch { preferencesManager.setAutoQuality(enabled) }
    }

    fun setPlaybackSpeed(speed: Float) {
        viewModelScope.launch { preferencesManager.setPlaybackSpeed(speed) }
    }

    fun setSubtitleLang(lang: String) {
        viewModelScope.launch { preferencesManager.setSubtitleLanguage(lang) }
    }

    fun clearCache() {
        viewModelScope.launch {
            repository.clearRecentSearches()
        }
    }
}

class ViewModelFactory(
    private val repository: MovieRepository,
    private val preferencesManager: PreferencesManager,
    private val contentId: String = ""
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(repository) as T
            modelClass.isAssignableFrom(ExploreViewModel::class.java) -> ExploreViewModel(repository) as T
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> SearchViewModel(repository) as T
            modelClass.isAssignableFrom(DetailsViewModel::class.java) -> DetailsViewModel(contentId, repository) as T
            modelClass.isAssignableFrom(MyListViewModel::class.java) -> MyListViewModel(repository) as T
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> SettingsViewModel(preferencesManager, repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class ${modelClass.name}")
        }
    }
}
