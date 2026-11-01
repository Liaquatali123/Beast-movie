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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MediaType
import com.example.ui.components.GenreChip
import com.example.ui.components.MediaCard
import com.example.ui.components.SegmentedTypeSelector
import com.example.ui.theme.BeastBackground
import com.example.ui.theme.BeastGoldPrimary
import com.example.ui.theme.BeastTextSecondary
import com.example.ui.viewmodel.ExploreUiState
import com.example.ui.viewmodel.ExploreViewModel

@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel,
    onMediaClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedType by viewModel.selectedType.collectAsState()
    val selectedGenre by viewModel.selectedGenre.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BeastBackground)
            .testTag("explore_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Row: "Explore" + Search Icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Explore",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier.size(44.dp).testTag("explore_search_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.White
                    )
                }
            }

            // Category Segmented Switcher: Movies | Series | Anime
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                SegmentedTypeSelector(
                    selectedType = selectedType ?: MediaType.MOVIE,
                    onTypeSelected = { viewModel.selectType(it) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Genre Chips Horizontal Row
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.genres) { genre ->
                    GenreChip(
                        text = genre,
                        isSelected = genre == selectedGenre,
                        onClick = { viewModel.selectGenre(genre) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (val state = uiState) {
                is ExploreUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = BeastGoldPrimary,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }

                is ExploreUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.loadItems() },
                                colors = ButtonDefaults.buttonColors(containerColor = BeastGoldPrimary)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Retry", color = Color.Black)
                            }
                        }
                    }
                }

                is ExploreUiState.Success -> {
                    val itemsList = state.items
                    if (itemsList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No titles found in this category",
                                color = BeastTextSecondary,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 110.dp),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(itemsList, key = { it.id }) { item ->
                                MediaCard(
                                    item = item,
                                    onClick = { onMediaClick(item.id) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
