package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Episode
import com.example.data.model.Season
import com.example.ui.components.GoldButton
import com.example.ui.components.OutlinedActionPill
import com.example.ui.theme.BeastBackground
import com.example.ui.theme.BeastCardBorder
import com.example.ui.theme.BeastGoldBright
import com.example.ui.theme.BeastRatingGold
import com.example.ui.theme.BeastSurface
import com.example.ui.theme.BeastSurfaceVariant
import com.example.ui.theme.BeastTextPrimary
import com.example.ui.theme.BeastTextSecondary
import com.example.ui.viewmodel.DetailsViewModel

@Composable
fun SeriesDetailsScreen(
    viewModel: DetailsViewModel,
    onBackClick: () -> Unit,
    onPlayClick: (String, String?) -> Unit, // mediaId, episodeId
    modifier: Modifier = Modifier
) {
    BackHandler { onBackClick() }

    val context = LocalContext.current
    val item = viewModel.item ?: run {
        Box(
            modifier = modifier.fillMaxSize().background(BeastBackground),
            contentAlignment = Alignment.Center
        ) {
            Text("Series details not found", color = Color.White)
        }
        return
    }

    val isInWatchlist by viewModel.isInWatchlist.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val selectedSeasonNumber by viewModel.selectedSeason.collectAsState()
    var expandedSeasonId by remember { mutableStateOf<String?>(item.seasons.firstOrNull()?.id) }

    val tabs = listOf("Seasons", "About")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BeastBackground)
            .testTag("series_details_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 50.dp)
        ) {
            // Top App Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(42.dp)
                            .testTag("series_details_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "Series Details",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = {
                            Toast.makeText(context, "Sharing ${item.title}", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(42.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.White
                        )
                    }
                }
            }

            // Series Poster & Meta Header Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    // Poster Card
                    AsyncImage(
                        model = item.posterUrl,
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(width = 115.dp, height = 165.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, BeastCardBorder, RoundedCornerShape(14.dp))
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Title & Details
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 26.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = BeastRatingGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "%.1f".format(item.rating),
                                color = BeastRatingGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = BeastRatingGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = " (${item.releaseYear}) • ${item.runtime}",
                                color = BeastTextSecondary,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Genre tags
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            item.genres.take(3).forEach { genre ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(BeastSurfaceVariant)
                                        .border(1.dp, BeastCardBorder, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = genre,
                                        color = BeastTextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Synopsis
            item {
                Text(
                    text = item.synopsis,
                    color = Color(0xFFD4D4D8),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }

            // Action Buttons
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    GoldButton(
                        text = "Play Series",
                        onClick = {
                            val firstEp = item.seasons.firstOrNull()?.episodes?.firstOrNull()
                            onPlayClick(item.id, firstEp?.id)
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedActionPill(
                        text = if (isInWatchlist) "In My List" else "+ Add to List",
                        icon = if (isInWatchlist) Icons.Default.Check else Icons.Default.Add,
                        isSelected = isInWatchlist,
                        onClick = {
                            viewModel.toggleWatchlist()
                            val msg = if (!isInWatchlist) "Added to My List" else "Removed from My List"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Tab Selector: Seasons | About
            item {
                Spacer(modifier = Modifier.height(10.dp))
                TabRow(
                    selectedTabIndex = tabs.indexOf(selectedTab).coerceAtLeast(0),
                    containerColor = Color.Transparent,
                    contentColor = BeastGoldBright,
                    divider = {},
                    indicator = { tabPositions ->
                        val index = tabs.indexOf(selectedTab).coerceAtLeast(0)
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[index]),
                            color = BeastGoldBright,
                            height = 3.dp
                        )
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    tabs.forEach { tabName ->
                        val isSelected = tabName == selectedTab
                        Tab(
                            selected = isSelected,
                            onClick = { viewModel.selectTab(tabName) },
                            text = {
                                Text(
                                    text = tabName,
                                    color = if (isSelected) Color.White else BeastTextSecondary,
                                    fontSize = 15.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Content of selected tab
            if (selectedTab == "Seasons") {
                if (item.seasons.isEmpty()) {
                    item {
                        Text(
                            text = "No season data available yet.",
                            color = BeastTextSecondary,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                } else {
                    items(item.seasons, key = { it.id }) { season ->
                        val isExpanded = expandedSeasonId == season.id
                        SeasonCard(
                            season = season,
                            isExpanded = isExpanded,
                            onToggleExpand = {
                                expandedSeasonId = if (isExpanded) null else season.id
                            },
                            onEpisodePlay = { ep ->
                                onPlayClick(item.id, ep.id)
                            }
                        )
                    }
                }
            } else {
                // About Tab
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(text = "About ${item.title}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = item.synopsis, color = BeastTextSecondary, fontSize = 13.sp, lineHeight = 20.sp)

                        Text(text = "Cast & Crew", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        item.cast.forEach { actor ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = actor.photoUrl,
                                    contentDescription = actor.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(36.dp).clip(CircleShape).background(BeastSurfaceVariant)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = actor.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Text(text = actor.role, color = BeastTextSecondary, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SeasonCard(
    season: Season,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onEpisodePlay: (Episode) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("season_card_${season.id}"),
        colors = CardDefaults.cardColors(containerColor = BeastSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() }
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Season Thumbnail
                AsyncImage(
                    model = season.thumbnailUrl,
                    contentDescription = season.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(width = 80.dp, height = 55.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BeastSurfaceVariant)
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = season.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${season.episodeCount} Episodes",
                        color = BeastTextSecondary,
                        fontSize = 12.sp
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = BeastGoldBright,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Expanded Episodes List
            if (isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (season.episodes.isEmpty()) {
                        Text(
                            text = "Episodes loading soon...",
                            color = BeastTextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    } else {
                        season.episodes.forEach { episode ->
                            EpisodeRow(
                                episode = episode,
                                onPlay = { onEpisodePlay(episode) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EpisodeRow(
    episode: Episode,
    onPlay: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(BeastSurfaceVariant)
            .clickable { onPlay() }
            .padding(8.dp)
            .testTag("episode_${episode.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(width = 72.dp, height = 48.dp)
                .clip(RoundedCornerShape(6.dp))
        ) {
            AsyncImage(
                model = episode.thumbnailUrl,
                contentDescription = episode.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x50000000)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = BeastGoldBright,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${episode.episodeNumber}. ${episode.title}",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = episode.duration,
                color = BeastGoldBright,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
