package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.ContinueWatchingEntity
import com.example.data.local.PreferencesManager
import com.example.data.local.RecentSearchEntity
import com.example.data.local.WatchHistoryEntity
import com.example.data.local.WatchlistEntity
import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.data.repository.HomeFeedData
import com.example.data.repository.MovieRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val data: HomeFeedData) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(private val repository: MovieRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val continueWatching: StateFlow<List<ContinueWatchingEntity>> = repository.getContinueWatching()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _showNotifications = MutableStateFlow(false)
    val showNotifications = _showNotifications.asStateFlow()

    init {
        loadHomeFeed()
    }

    fun loadHomeFeed() {
        _uiState.value = HomeUiState.Loading
        viewModelScope.launch {
            repository.getHomeFeed()
                .onSuccess { data ->
                    _uiState.value = HomeUiState.Success(data)
                }
                .onFailure { _ ->
                    _uiState.value = HomeUiState.Error("Unable to load content. Please check your connection.")
                }
        }
    }

    fun toggleNotifications(show: Boolean) {
        _showNotifications.value = show
    }
}

sealed interface ExploreUiState {
    data object Loading : ExploreUiState
    data class Success(val items: List<MediaItem>) : ExploreUiState
    data class Error(val message: String) : ExploreUiState
}

class ExploreViewModel(private val repository: MovieRepository) : ViewModel() {

    private val _selectedType = MutableStateFlow<MediaType?>(MediaType.MOVIE)
    val selectedType: StateFlow<MediaType?> = _selectedType.asStateFlow()

    private val _selectedGenre = MutableStateFlow("All")
    val selectedGenre: StateFlow<String> = _selectedGenre.asStateFlow()

    private val _uiState = MutableStateFlow<ExploreUiState>(ExploreUiState.Loading)
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    val genres = listOf("All", "Action", "Adventure", "Drama", "Comedy", "Sci-Fi", "Crime", "Thriller", "Fantasy", "Animation", "Romance")

    init {
        loadItems()
    }

    fun selectType(type: MediaType?) {
        _selectedType.value = type
        loadItems()
    }

    fun selectGenre(genre: String) {
        _selectedGenre.value = genre
        loadItems()
    }

    fun loadItems() {
        _uiState.value = ExploreUiState.Loading
        viewModelScope.launch {
            repository.getExploreItems(
                type = _selectedType.value,
                genre = _selectedGenre.value
            ).onSuccess { list ->
                _uiState.value = ExploreUiState.Success(list)
            }.onFailure { _ ->
                _uiState.value = ExploreUiState.Error("Unable to load content. Please check your connection.")
            }
        }
    }
}

sealed interface SearchUiState {
    data object Idle : SearchUiState
    data object Loading : SearchUiState
    data class Success(val results: List<MediaItem>) : SearchUiState
    data object Empty : SearchUiState
    data class Error(val message: String) : SearchUiState
}

class SearchViewModel(private val repository: MovieRepository) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _trendingKeywords = MutableStateFlow<List<String>>(listOf("Avatar", "Spider-Man", "Batman", "Lupin", "Horimiya", "War Machine"))
    val trendingKeywords: StateFlow<List<String>> = _trendingKeywords.asStateFlow()

    val recentSearches: StateFlow<List<String>> = repository.getRecentSearches()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var searchJob: Job? = null

    init {
        loadTrendingSearchTerms()
    }

    private fun loadTrendingSearchTerms() {
        viewModelScope.launch {
            repository.getEveryoneSearch().onSuccess { (keywords, _) ->
                if (keywords.isNotEmpty()) {
                    _trendingKeywords.value = keywords.take(10)
                }
            }
        }
    }

    fun setQuery(newQuery: String) {
        _query.value = newQuery
        searchJob?.cancel()

        if (newQuery.isBlank()) {
            _uiState.value = SearchUiState.Idle
            return
        }

        searchJob = viewModelScope.launch {
            delay(350) // Debounce typing
            performSearch(newQuery)
        }
    }

    fun performSearch(queryToSearch: String) {
        if (queryToSearch.isBlank()) return
        _query.value = queryToSearch
        _uiState.value = SearchUiState.Loading

        viewModelScope.launch {
            repository.saveSearchQuery(queryToSearch)
            repository.search(queryToSearch)
                .onSuccess { list ->
                    if (list.isEmpty()) {
                        _uiState.value = SearchUiState.Empty
                    } else {
                        _uiState.value = SearchUiState.Success(list)
                    }
                }
                .onFailure { _ ->
                    _uiState.value = SearchUiState.Error("Unable to complete search. Please try again.")
                }
        }
    }

    fun clearSearch() {
        searchJob?.cancel()
        _query.value = ""
        _uiState.value = SearchUiState.Idle
    }

    fun deleteRecentSearch(query: String) {
        viewModelScope.launch {
            repository.deleteSearchQuery(query)
        }
    }

    fun clearAllRecentSearches() {
        viewModelScope.launch {
            repository.clearAllSearches()
        }
    }
}

class DetailsViewModel(
    val contentId: String,
    private val repository: MovieRepository
) : ViewModel() {

    private val _item = MutableStateFlow<MediaItem?>(repository.getMediaItemById(contentId))
    val item: StateFlow<MediaItem?> = _item.asStateFlow()

    private val _similarItems = MutableStateFlow<List<MediaItem>>(emptyList())
    val similarItems: StateFlow<List<MediaItem>> = _similarItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val isInWatchlist: StateFlow<Boolean> = repository.isInWatchlist(contentId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _selectedTab = MutableStateFlow("Similar")
    val selectedTab: StateFlow<String> = _selectedTab.asStateFlow()

    private val _selectedSeason = MutableStateFlow(1)
    val selectedSeason: StateFlow<Int> = _selectedSeason.asStateFlow()

    init {
        loadRecommendations()
    }

    private fun loadRecommendations() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getRecommendations(contentId).onSuccess { list ->
                _similarItems.value = list
            }
            _isLoading.value = false
        }
    }

    fun selectTab(tab: String) {
        _selectedTab.value = tab
    }

    fun selectSeason(seasonNumber: Int) {
        _selectedSeason.value = seasonNumber
    }

    fun toggleWatchlist() {
        val currentItem = _item.value ?: return
        viewModelScope.launch {
            if (isInWatchlist.value) {
                repository.removeFromWatchlist(contentId)
            } else {
                repository.addToWatchlist(currentItem)
            }
        }
    }
}

class MyListViewModel(private val repository: MovieRepository) : ViewModel() {
    private val _selectedTab = MutableStateFlow("Watchlist") // "Watchlist", "History", "Continue Watching"
    val selectedTab: StateFlow<String> = _selectedTab.asStateFlow()

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
    val darkTheme: StateFlow<Boolean> = preferencesManager.darkTheme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val language: StateFlow<String> = preferencesManager.appLanguage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "English")

    val autoQuality: StateFlow<Boolean> = preferencesManager.autoQuality
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val playbackSpeed: StateFlow<Float> = preferencesManager.playbackSpeed
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1.0f)

    val subtitleLang: StateFlow<String> = preferencesManager.subtitleLanguage
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
            repository.clearAllSearches()
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
