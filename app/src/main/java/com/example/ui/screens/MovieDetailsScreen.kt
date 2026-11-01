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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
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
    val item = viewModel.item ?: run {
        Box(
            modifier = modifier.fillMaxSize().background(BeastBackground),
            contentAlignment = Alignment.Center
        ) {
            Text("Movie details not found", color = Color.White)
        }
        return
    }

    val isInWatchlist by viewModel.isInWatchlist.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val tabs = listOf("Similar", "Cast", "Reviews")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BeastBackground)
            .testTag("movie_details_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            // Backdrop with gradient
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    AsyncImage(
                        model = item.backdropUrl.ifEmpty { item.posterUrl },
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DetailsBackdropGradient)
                    )

                    // Top Floating Buttons: Back and Share
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 36.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0x80000000))
                                .border(1.dp, Color(0x33FFFFFF), CircleShape)
                                .testTag("details_back_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        IconButton(
                            onClick = {
                                Toast.makeText(context, "Sharing link to ${item.title}", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0x80000000))
                                .border(1.dp, Color(0x33FFFFFF), CircleShape)
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

            // Title, Rating, Meta
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = item.title,
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Meta: ★ rating ★ (year) • runtime • age rating
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
                            text = "  (${item.releaseYear}) • ${item.runtime} • ${item.ageRating}",
                            color = BeastTextSecondary,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Genre Pills
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item.genres.take(4).forEach { genre ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(BeastSurfaceVariant)
                                    .border(1.dp, BeastCardBorder, RoundedCornerShape(16.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = genre,
                                    color = BeastTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Synopsis
                    Text(
                        text = item.synopsis,
                        color = Color(0xFFD4D4D8),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Primary Gold "Watch Now" Button
                    GoldButton(
                        text = "Watch Now",
                        onClick = { onPlayClick(item.id) }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Secondary Action Buttons Row: Add to List, Download
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedActionPill(
                            text = if (isInWatchlist) "In My List" else "Add to List",
                            icon = if (isInWatchlist) Icons.Default.Check else Icons.Default.Add,
                            isSelected = isInWatchlist,
                            onClick = {
                                viewModel.toggleWatchlist()
                                val msg = if (!isInWatchlist) "Added to My List" else "Removed from My List"
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedActionPill(
                            text = "Download",
                            icon = Icons.Default.Download,
                            onClick = {
                                Toast.makeText(context, "Downloading ${item.title} (1080p)", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Tabs Row: Similar, Cast, Reviews
            item {
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

            // Tab Content
            when (selectedTab) {
                "Similar" -> {
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(viewModel.similarItems, key = { it.id }) { similar ->
                                MediaCard(
                                    item = similar,
                                    onClick = { onMediaClick(similar.id) },
                                    width = 115,
                                    height = 165
                                )
                            }
                        }
                    }
                }
                "Cast" -> {
                    if (item.cast.isEmpty()) {
                        item {
                            Text(
                                text = "Cast information unavailable",
                                color = BeastTextSecondary,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    } else {
                        items(item.cast, key = { it.id }) { actor ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = actor.photoUrl,
                                    contentDescription = actor.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(BeastSurfaceVariant)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = actor.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = actor.role, color = BeastTextSecondary, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
                "Reviews" -> {
                    if (item.reviews.isEmpty()) {
                        item {
                            Text(
                                text = "No reviews yet. Be the first to review!",
                                color = BeastTextSecondary,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    } else {
                        items(item.reviews, key = { it.id }) { review ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                colors = CardDefaults.cardColors(containerColor = BeastSurface),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = review.author, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = BeastRatingGold, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(text = "%.1f".format(review.rating), color = BeastRatingGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Text(text = review.date, color = BeastTextSecondary, fontSize = 11.sp, modifier = Modifier.padding(vertical = 2.dp))
                                    Text(text = review.content, color = Color(0xFFD4D4D8), fontSize = 13.sp, lineHeight = 18.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
