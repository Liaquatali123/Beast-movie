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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
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
import com.example.ui.components.GoldButton
import com.example.ui.components.MediaCard
import com.example.ui.components.OutlinedActionPill
import com.example.ui.theme.BeastBackground
import com.example.ui.theme.BeastCardBorder
import com.example.ui.theme.BeastGoldBright
import com.example.ui.theme.BeastRatingGold
import com.example.ui.theme.BeastSurface
import com.example.ui.theme.BeastSurfaceVariant
import com.example.ui.theme.BeastTextPrimary
import com.example.ui.theme.BeastTextSecondary
import com.example.ui.theme.DetailsBackdropGradient
import com.example.ui.viewmodel.DetailsViewModel

@Composable
fun MovieDetailsScreen(
    viewModel: DetailsViewModel,
    onBackClick: () -> Unit,
    onPlayClick: (String) -> Unit,
    onMediaClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBackClick() }

    val context = LocalContext.current
    val itemState by viewModel.item.collectAsState()
    val similarItems by viewModel.similarItems.collectAsState()
    val isInWatchlist by viewModel.isInWatchlist.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val tabs = listOf("Similar", "Cast", "About")

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
                Text("Loading title...", color = BeastTextSecondary, fontSize = 14.sp)
            }
        }
        return
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BeastBackground)
            .testTag("movie_details_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Backdrop with gradient scrim
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    AsyncImage(
                        model = item.backdropUrl.ifBlank { item.posterUrl },
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DetailsBackdropGradient)
                    )

                    // Top navigation row
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
                                .background(Color.Black.copy(alpha = 0.5f))
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
                                .background(Color.Black.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            // Title and Metadata
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = item.title,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Meta chips row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (item.rating > 0.0) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(BeastSurfaceVariant)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = BeastRatingGold,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = String.format("%.1f", item.rating),
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        if (item.releaseYear.isNotBlank()) {
                            Text(
                                text = item.releaseYear,
                                color = BeastTextSecondary,
                                fontSize = 13.sp
                            )
                        }

                        Text(
                            text = "•",
                            color = BeastTextSecondary,
                            fontSize = 12.sp
                        )

                        Text(
                            text = item.runtime,
                            color = BeastTextSecondary,
                            fontSize = 13.sp
                        )

                        if (item.country.isNotBlank()) {
                            Text(
                                text = "•",
                                color = BeastTextSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = item.country,
                                color = BeastTextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Genre Pills
                    if (item.genres.isNotEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(bottom = 14.dp)
                        ) {
                            item.genres.take(4).forEach { g ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(BeastSurface)
                                        .border(1.dp, BeastCardBorder, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(text = g, color = BeastTextSecondary, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    // Action Buttons Row: Watch Now (Gold) & Add to List
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 18.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        GoldButton(
                            text = "Watch Now",
                            icon = Icons.Default.PlayArrow,
                            onClick = { onPlayClick(item.id) },
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedActionPill(
                            text = if (isInWatchlist) "Added" else "+ My List",
                            icon = if (isInWatchlist) Icons.Default.Check else Icons.Default.Add,
                            isSelected = isInWatchlist,
                            onClick = { viewModel.toggleWatchlist() },
                            modifier = Modifier.weight(0.7f)
                        )
                    }

                    // Synopsis / Overview
                    if (item.synopsis.isNotBlank()) {
                        Text(
                            text = "Overview",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Text(
                            text = item.synopsis,
                            color = BeastTextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(bottom = 20.dp)
                        )
                    }
                }
            }

            // Tabs Row
            item {
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
                "Similar" -> {
                    if (similarItems.isEmpty()) {
                        item {
                            Text(
                                text = "Discovering recommended titles...",
                                color = BeastTextSecondary,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    } else {
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(similarItems, key = { it.id }) { sim ->
                                    MediaCard(
                                        item = sim,
                                        onClick = { onMediaClick(sim.id) }
                                    )
                                }
                            }
                        }
                    }
                }

                "Cast" -> {
                    if (item.cast.isEmpty()) {
                        item {
                            Text(
                                text = "Cast information from verified source is being updated.",
                                color = BeastTextSecondary,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    } else {
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(item.cast, key = { it.id }) { actor ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.width(80.dp)
                                    ) {
                                        AsyncImage(
                                            model = actor.photoUrl.ifBlank { item.posterUrl },
                                            contentDescription = actor.name,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(64.dp)
                                                .clip(CircleShape)
                                                .background(BeastSurface)
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = actor.name,
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = actor.role,
                                            color = BeastTextSecondary,
                                            fontSize = 10.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                "About" -> {
                    item {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Text(
                                text = "Streaming Info",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Source: Authorized Media CDN (macdn.aoneroom.com)\nFormat: High-Definition MP4/HLS\nCategory: ${item.type.displayName}",
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
