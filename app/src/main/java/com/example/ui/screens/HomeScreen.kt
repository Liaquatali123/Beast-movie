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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
    val featured by viewModel.featuredItem.collectAsState()
    val trendingMovies by viewModel.trendingMovies.collectAsState()
    val popularSeries by viewModel.popularSeries.collectAsState()
    val trendingAnime by viewModel.trendingAnime.collectAsState()
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

            // Scrollable Content Feed
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("home_content_list"),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                // Featured Hero Banner
                item {
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        HeroFeaturedCard(
                            item = featured,
                            onWatchClick = { onWatchClick(featured.id) },
                            onDetailsClick = { onMediaClick(featured.id) }
                        )
                    }
                }

                // Continue Watching (if available)
                if (continueWatchingList.isNotEmpty()) {
                    item {
                        SectionHeader(title = "Continue Watching")
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(continueWatchingList, key = { it.id }) { item ->
                                ContinueWatchingCard(
                                    item = item,
                                    onClick = { onWatchClick(item.mediaId) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // Trending Movies Section
                item {
                    SectionHeader(
                        title = "Trending Movies",
                        onSeeAllClick = { onSeeAllClick(MediaType.MOVIE) }
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(trendingMovies, key = { it.id }) { movie ->
                            MediaCard(
                                item = movie,
                                onClick = { onMediaClick(movie.id) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Popular Series Section
                item {
                    SectionHeader(
                        title = "Popular Series",
                        onSeeAllClick = { onSeeAllClick(MediaType.SERIES) }
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(popularSeries, key = { it.id }) { series ->
                            MediaCard(
                                item = series,
                                onClick = { onMediaClick(series.id) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Trending Anime Section
                item {
                    SectionHeader(
                        title = "Trending Anime",
                        onSeeAllClick = { onSeeAllClick(MediaType.ANIME) }
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(trendingAnime, key = { it.id }) { anime ->
                            MediaCard(
                                item = anime,
                                onClick = { onMediaClick(anime.id) }
                            )
                        }
                    }
                }
            }
        }

        // Notifications Modal Dialog
        if (showNotifications) {
            AlertDialog(
                onDismissRequest = { viewModel.toggleNotifications(false) },
                containerColor = BeastSurface,
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = BeastGoldBright,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Notifications",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        IconButton(onClick = { viewModel.toggleNotifications(false) }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = BeastTextSecondary
                            )
                        }
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        NotificationItem(
                            title = "Dune: Part Two Available",
                            subtitle = "Watch in 4K UHD with HDR and multi-language audio.",
                            time = "Just now"
                        )
                        NotificationItem(
                            title = "Breaking Bad Complete Series",
                            subtitle = "All 5 seasons and 62 episodes ready to binge in HD.",
                            time = "2 hours ago"
                        )
                        NotificationItem(
                            title = "Welcome to Beast Movies!",
                            subtitle = "Enjoy premium streaming, customize quality, and save titles to My List.",
                            time = "1 day ago"
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.toggleNotifications(false) }) {
                        Text("Done", color = BeastGoldBright, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

@Composable
private fun NotificationItem(
    title: String,
    subtitle: String,
    time: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BeastSurfaceVariant),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = time,
                    color = BeastGoldPrimary,
                    fontSize = 10.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = BeastTextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}
