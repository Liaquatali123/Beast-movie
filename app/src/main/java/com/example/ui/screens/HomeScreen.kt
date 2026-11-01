package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.ui.components.BeastTopBar
import com.example.ui.components.ContinueWatchingCard
import com.example.ui.components.HeroFeaturedCard
import com.example.ui.components.MediaCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.BeastBackground
import com.example.ui.theme.BeastGoldBright
import com.example.ui.theme.BeastGoldPrimary
import com.example.ui.theme.BeastSurface
import com.example.ui.theme.BeastSurfaceVariant
import com.example.ui.theme.BeastTextPrimary
import com.example.ui.theme.BeastTextSecondary
import com.example.ui.viewmodel.HomeUiState
import com.example.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onMediaClick: (String) -> Unit,
    onWatchClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onSeeAllClick: (MediaType) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val continueWatchingList by viewModel.continueWatching.collectAsState()
    val showNotifications by viewModel.showNotifications.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BeastBackground)
            .testTag("home_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Brand Top App Bar
            BeastTopBar(
                onSearchClick = onSearchClick,
                onNotificationsClick = { viewModel.toggleNotifications(true) }
            )

            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = BeastGoldPrimary,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Loading Beast Movies Catalog...",
                                color = BeastTextSecondary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                is HomeUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Unable to load content",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Please check your network connection and tap Retry.",
                                color = BeastTextSecondary,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = { viewModel.loadHomeFeed() },
                                colors = ButtonDefaults.buttonColors(containerColor = BeastGoldPrimary)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Retry", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                is HomeUiState.Success -> {
                    val feed = state.data

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("home_content_list"),
                        contentPadding = PaddingValues(bottom = 90.dp)
                    ) {
                        // Featured Hero Banner
                        feed.featured?.let { feat ->
                            item {
                                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                                    HeroFeaturedCard(
                                        item = feat,
                                        onWatchClick = { onWatchClick(feat.id) },
                                        onDetailsClick = { onMediaClick(feat.id) }
                                    )
                                }
                            }
                        }

                        // Continue Watching Shelf
                        if (continueWatchingList.isNotEmpty()) {
                            item {
                                SectionHeader(
                                    title = "Continue Watching"
                                )
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                                    modifier = Modifier.padding(bottom = 12.dp)
                                ) {
                                    items(continueWatchingList, key = { it.id }) { item ->
                                        ContinueWatchingCard(
                                            item = item,
                                            onClick = { onWatchClick(item.mediaId) }
                                        )
                                    }
                                }
                            }
                        }

                        // Trending Movies Shelf
                        if (feed.trendingMovies.isNotEmpty()) {
                            item {
                                SectionHeader(
                                    title = "Trending Movies",
                                    onSeeAllClick = { onSeeAllClick(MediaType.MOVIE) }
                                )
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.padding(bottom = 16.dp)
                                ) {
                                    items(feed.trendingMovies, key = { it.id }) { movie ->
                                        MediaCard(
                                            item = movie,
                                            onClick = { onMediaClick(movie.id) }
                                        )
                                    }
                                }
                            }
                        }

                        // Popular TV Series Shelf
                        if (feed.popularSeries.isNotEmpty()) {
                            item {
                                SectionHeader(
                                    title = "Popular Series",
                                    onSeeAllClick = { onSeeAllClick(MediaType.SERIES) }
                                )
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.padding(bottom = 16.dp)
                                ) {
                                    items(feed.popularSeries, key = { it.id }) { series ->
                                        MediaCard(
                                            item = series,
                                            onClick = { onMediaClick(series.id) }
                                        )
                                    }
                                }
                            }
                        }

                        // Trending Anime Shelf
                        if (feed.trendingAnime.isNotEmpty()) {
                            item {
                                SectionHeader(
                                    title = "Trending Anime",
                                    onSeeAllClick = { onSeeAllClick(MediaType.ANIME) }
                                )
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.padding(bottom = 16.dp)
                                ) {
                                    items(feed.trendingAnime, key = { it.id }) { anime ->
                                        MediaCard(
                                            item = anime,
                                            onClick = { onMediaClick(anime.id) }
                                        )
                                    }
                                }
                            }
                        }

                        // Additional Dynamic Sections from API
                        val additionalSections = feed.sections.filter { sec ->
                            val t = sec.title.lowercase()
                            !t.contains("popular movie") && !t.contains("popular series") && !t.contains("animation") && sec.items.isNotEmpty()
                        }.take(4)

                        items(additionalSections) { section ->
                            SectionHeader(
                                title = section.title
                            )
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.padding(bottom = 16.dp)
                            ) {
                                items(section.items, key = { it.id }) { itm ->
                                    MediaCard(
                                        item = itm,
                                        onClick = { onMediaClick(itm.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Notification Dialog
        if (showNotifications) {
            AlertDialog(
                onDismissRequest = { viewModel.toggleNotifications(false) },
                containerColor = BeastSurface,
                icon = {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = BeastGoldBright,
                        modifier = Modifier.size(32.dp)
                    )
                },
                title = {
                    Text(
                        text = "Notifications",
                        color = BeastTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = BeastSurfaceVariant),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Beast Movies Connected",
                                    color = BeastGoldBright,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Connected to authorized API. Stream verified movies and TV series in HD.",
                                    color = BeastTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.toggleNotifications(false) }) {
                        Text("Dismiss", color = BeastGoldPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}
