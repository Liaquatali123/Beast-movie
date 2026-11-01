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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import com.example.ui.theme.BeastTextSecondary
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
    val items by viewModel.items.collectAsState()

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
                    selectedType = selectedType,
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

            Spacer(modifier = Modifier.height(16.dp))

            // 3-Column Grid of Media Posters
            if (items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
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
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .testTag("explore_grid"),
                    contentPadding = PaddingValues(bottom = 90.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(items, key = { it.id }) { item ->
                        MediaCard(
                            item = item,
                            onClick = { onMediaClick(item.id) },
                            modifier = Modifier.fillMaxWidth(),
                            width = 105,
                            height = 150
                        )
                    }
                }
            }
        }
    }
}
