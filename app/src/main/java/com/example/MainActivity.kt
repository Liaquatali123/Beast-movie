package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.BeastMoviesDatabase
import com.example.data.local.PreferencesManager
import com.example.data.model.MediaType
import com.example.data.repository.MovieRepository
import com.example.ui.components.BeastBottomNav
import com.example.ui.components.NavigationTab
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MovieDetailsScreen
import com.example.ui.screens.MyListScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SeriesDetailsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.VideoPlayerScreen
import com.example.ui.theme.BeastBackground
import com.example.ui.theme.BeastMoviesTheme
import com.example.ui.viewmodel.DetailsViewModel
import com.example.ui.viewmodel.ExploreViewModel
import com.example.ui.viewmodel.HomeViewModel
import com.example.ui.viewmodel.MyListViewModel
import com.example.ui.viewmodel.SearchViewModel
import com.example.ui.viewmodel.SettingsViewModel
import com.example.ui.viewmodel.ViewModelFactory

sealed class Screen {
    data object Splash : Screen()
    data object Main : Screen()
    data class MovieDetails(val contentId: String) : Screen()
    data class SeriesDetails(val contentId: String) : Screen()
    data class VideoPlayer(val mediaId: String, val episodeId: String? = null) : Screen()
    data object Search : Screen()
    data object Settings : Screen()
}

class MainActivity : ComponentActivity() {

    private lateinit var database: BeastMoviesDatabase
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var repository: MovieRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        database = BeastMoviesDatabase.getInstance(this)
        preferencesManager = PreferencesManager(this)
        repository = MovieRepository(database)

        setContent {
            BeastMoviesTheme {
                BeastMoviesApp(
                    repository = repository,
                    preferencesManager = preferencesManager
                )
            }
        }
    }
}

@Composable
fun BeastMoviesApp(
    repository: MovieRepository,
    preferencesManager: PreferencesManager
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
    var selectedTab by remember { mutableStateOf(NavigationTab.HOME) }
    val screenStack = remember { mutableListOf<Screen>() }

    fun navigateTo(screen: Screen) {
        screenStack.add(currentScreen)
        currentScreen = screen
    }

    fun navigateBack() {
        if (screenStack.isNotEmpty()) {
            currentScreen = screenStack.removeAt(screenStack.size - 1)
        } else {
            if (currentScreen !is Screen.Main && currentScreen !is Screen.Splash) {
                currentScreen = Screen.Main
            }
        }
    }

    fun openDetails(contentId: String) {
        val item = repository.getMediaItemById(contentId)
        if (item?.type == MediaType.SERIES || item?.type == MediaType.ANIME) {
            navigateTo(Screen.SeriesDetails(contentId))
        } else {
            navigateTo(Screen.MovieDetails(contentId))
        }
    }

    fun startPlaying(mediaId: String, episodeId: String? = null) {
        navigateTo(Screen.VideoPlayer(mediaId, episodeId))
    }

    when (val screen = currentScreen) {
        is Screen.Splash -> {
            SplashScreen(
                onEnterApp = {
                    currentScreen = Screen.Main
                }
            )
        }

        is Screen.VideoPlayer -> {
            VideoPlayerScreen(
                mediaId = screen.mediaId,
                episodeId = screen.episodeId,
                repository = repository,
                onBackClick = { navigateBack() }
            )
        }

        is Screen.MovieDetails -> {
            val detailsViewModel: DetailsViewModel = viewModel(
                key = "details_${screen.contentId}",
                factory = ViewModelFactory(repository, preferencesManager, screen.contentId)
            )
            MovieDetailsScreen(
                viewModel = detailsViewModel,
                onBackClick = { navigateBack() },
                onPlayClick = { startPlaying(it) },
                onMediaClick = { openDetails(it) },
                modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
            )
        }

        is Screen.SeriesDetails -> {
            val seriesViewModel: DetailsViewModel = viewModel(
                key = "series_${screen.contentId}",
                factory = ViewModelFactory(repository, preferencesManager, screen.contentId)
            )
            SeriesDetailsScreen(
                viewModel = seriesViewModel,
                onBackClick = { navigateBack() },
                onPlayClick = { mediaId, epId -> startPlaying(mediaId, epId) },
                modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
            )
        }

        is Screen.Search -> {
            BackHandler { navigateBack() }
            val searchViewModel: SearchViewModel = viewModel(
                factory = ViewModelFactory(repository, preferencesManager)
            )
            SearchScreen(
                viewModel = searchViewModel,
                onMediaClick = { openDetails(it) },
                modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
            )
        }

        is Screen.Settings -> {
            BackHandler { navigateBack() }
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = ViewModelFactory(repository, preferencesManager)
            )
            SettingsScreen(
                viewModel = settingsViewModel,
                modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
            )
        }

        is Screen.Main -> {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BeastBackground)
                    .windowInsetsPadding(WindowInsets.safeDrawing),
                bottomBar = {
                    BeastBottomNav(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                },
                contentWindowInsets = WindowInsets(0, 0, 0, 0)
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    when (selectedTab) {
                        NavigationTab.HOME -> {
                            val homeViewModel: HomeViewModel = viewModel(
                                factory = ViewModelFactory(repository, preferencesManager)
                            )
                            HomeScreen(
                                viewModel = homeViewModel,
                                onMediaClick = { openDetails(it) },
                                onWatchClick = { startPlaying(it) },
                                onSearchClick = { navigateTo(Screen.Search) },
                                onSeeAllClick = { mediaType ->
                                    selectedTab = when (mediaType) {
                                        MediaType.MOVIE -> NavigationTab.MOVIES
                                        MediaType.SERIES -> NavigationTab.SERIES
                                        MediaType.ANIME -> NavigationTab.ANIME
                                    }
                                }
                            )
                        }

                        NavigationTab.MOVIES -> {
                            val exploreViewModel: ExploreViewModel = viewModel(
                                key = "explore_movies",
                                factory = ViewModelFactory(repository, preferencesManager)
                            )
                            exploreViewModel.selectType(MediaType.MOVIE)
                            ExploreScreen(
                                viewModel = exploreViewModel,
                                onMediaClick = { openDetails(it) },
                                onSearchClick = { navigateTo(Screen.Search) }
                            )
                        }

                        NavigationTab.SERIES -> {
                            val exploreViewModel: ExploreViewModel = viewModel(
                                key = "explore_series",
                                factory = ViewModelFactory(repository, preferencesManager)
                            )
                            exploreViewModel.selectType(MediaType.SERIES)
                            ExploreScreen(
                                viewModel = exploreViewModel,
                                onMediaClick = { openDetails(it) },
                                onSearchClick = { navigateTo(Screen.Search) }
                            )
                        }

                        NavigationTab.ANIME -> {
                            val exploreViewModel: ExploreViewModel = viewModel(
                                key = "explore_anime",
                                factory = ViewModelFactory(repository, preferencesManager)
                            )
                            exploreViewModel.selectType(MediaType.ANIME)
                            ExploreScreen(
                                viewModel = exploreViewModel,
                                onMediaClick = { openDetails(it) },
                                onSearchClick = { navigateTo(Screen.Search) }
                            )
                        }

                        NavigationTab.MY_LIST -> {
                            val myListViewModel: MyListViewModel = viewModel(
                                factory = ViewModelFactory(repository, preferencesManager)
                            )
                            MyListScreen(
                                viewModel = myListViewModel,
                                onMediaClick = { openDetails(it) },
                                onPlayClick = { startPlaying(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}
