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
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material3.CircularProgressIndicator
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
    onPlayClick: (String, String?) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBackClick() }

    val context = LocalContext.current
    val itemState by viewModel.item.collectAsState()
    val isInWatchlist by viewModel.isInWatchlist.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()

    val item = itemState
    if (item == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(BeastBackground),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = BeastGoldBright, strokeWidth = 3.dp)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading series details...", color = BeastTextSecondary, fontSize = 14.sp)
            }
        }
        return
    }

    var expandedSeasonId by remember { mutableStateOf<String?>(item.seasons.firstOrNull()?.id) }
    val tabs = listOf("Episodes", "About")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BeastBackground)
            .testTag("series_details_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Header Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(BeastSurface)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = {
                            Toast.makeText(context, "Link copied: ${item.title}", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(BeastSurface)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.White
                        )
                    }
                }
            }

            // Poster & Details Top Card
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    AsyncImage(
                        model = item.posterUrl,
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(width = 110.dp, height = 165.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BeastSurfaceVariant)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (item.rating > 0.0) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = BeastRatingGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = String.format("%.1f", item.rating),
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            if (item.releaseYear.isNotBlank()) {
                                Text(
                                    text = item.releaseYear,
                                    color = BeastTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = item.runtime,
                            color = BeastGoldBright,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        if (item.genres.isNotEmpty()) {
                            Text(
                                text = item.genres.take(3).joinToString(" • "),
                                color = BeastTextSecondary,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Actions: Play Series & Bookmark
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val firstEp = item.seasons.firstOrNull()?.episodes?.firstOrNull()
                    GoldButton(
                        text = "Play Series",
                        icon = Icons.Default.PlayArrow,
                        onClick = { onPlayClick(item.id, firstEp?.id) },
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedActionPill(
                        text = if (isInWatchlist) "Saved" else "+ My List",
                        icon = if (isInWatchlist) Icons.Default.Check else Icons.Default.Add,
                        isSelected = isInWatchlist,
                        onClick = { viewModel.toggleWatchlist() },
                        modifier = Modifier.weight(0.7f)
                    )
                }
            }

            // Synopsis
            if (item.synopsis.isNotBlank()) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        Text(
                            text = "Synopsis",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.synopsis,
                            color = BeastTextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 19.sp
                        )
                    }
                }
            }

            // Tabs Row
            item {
                Spacer(modifier = Modifier.height(10.dp))
                TabRow(
                    selectedTabIndex = tabs.indexOf(selectedTab).coerceAtLeast(0),
                    containerColor = BeastBackground,
                    contentColor = BeastGoldBright,
                    indicator = { tabPositions ->
                        val index = tabs.indexOf(selectedTab).coerceAtLeast(0)
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                            color = BeastGoldBright,
                            height = 2.dp
                        )
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    tabs.forEach { tabTitle ->
                        Tab(
                            selected = selectedTab == tabTitle,
                            onClick = { viewModel.selectTab(tabTitle) },
                            text = {
                                Text(
                                    text = tabTitle,
                                    fontSize = 14.sp,
                                    fontWeight = if (selectedTab == tabTitle) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTab == tabTitle) Color.White else BeastTextSecondary
                                )
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Tab Content
            when (selectedTab) {
                "Episodes" -> {
                    if (item.seasons.isEmpty()) {
                        item {
                            Text(
                                text = "Episodes currently being synchronized from source.",
                                color = BeastTextSecondary,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                            )
                        }
                    } else {
                        items(item.seasons) { season ->
                            SeasonAccordionCard(
                                season = season,
                                isExpanded = expandedSeasonId == season.id,
                                onToggle = {
                                    expandedSeasonId = if (expandedSeasonId == season.id) null else season.id
                                },
                                onEpisodePlay = { ep ->
                                    onPlayClick(item.id, ep.id)
                                }
                            )
                        }
                    }
                }

                "About" -> {
                    item {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Text(
                                text = "Series Information",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Category: ${item.type.displayName}\nSeasons: ${item.seasons.size.coerceAtLeast(1)}\nSource: Verified Streaming Endpoint",
                                color = BeastTextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SeasonAccordionCard(
    season: Season,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onEpisodePlay: (Episode) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BeastSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BeastCardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = season.title,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${season.episodes.size} Episodes",
                        color = BeastTextSecondary,
                        fontSize = 12.sp
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = BeastGoldBright
                )
            }

            if (isExpanded) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                    season.episodes.forEach { episode ->
                        EpisodeItemRow(
                            episode = episode,
                            onPlay = { onEpisodePlay(episode) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun EpisodeItemRow(
    episode: Episode,
    onPlay: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(BeastSurfaceVariant)
            .clickable(onClick = onPlay)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = episode.thumbnailUrl,
            contentDescription = episode.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(width = 70.dp, height = 48.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.Black)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = episode.title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = episode.duration,
                color = BeastTextSecondary,
                fontSize = 11.sp
            )
        }

        IconButton(
            onClick = onPlay,
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(BeastGoldBright)
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play Episode",
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
