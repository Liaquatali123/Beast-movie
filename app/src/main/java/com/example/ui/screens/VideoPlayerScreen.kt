package com.example.ui.screens

import android.app.Activity
import android.content.pm.ActivityInfo
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.data.model.Episode
import com.example.data.repository.MovieRepository
import com.example.ui.theme.BeastBackground
import com.example.ui.theme.BeastCardBorder
import com.example.ui.theme.BeastGoldBright
import com.example.ui.theme.BeastGoldDark
import com.example.ui.theme.BeastGoldPrimary
import com.example.ui.theme.BeastSurface
import com.example.ui.theme.BeastSurfaceVariant
import com.example.ui.theme.BeastTextPrimary
import com.example.ui.theme.BeastTextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerScreen(
    mediaId: String,
    episodeId: String? = null,
    repository: MovieRepository,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val mediaItem = remember(mediaId) { repository.getById(mediaId) }
    val episode = remember(episodeId, mediaItem) {
        if (episodeId != null && mediaItem != null) {
            mediaItem.seasons.flatMap { it.episodes }.firstOrNull { it.id == episodeId }
        } else null
    }

    val streamUrl = remember(mediaItem, episode) {
        episode?.streamUrl ?: mediaItem?.streamUrl ?: "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"
    }

    val displayTitle = remember(mediaItem, episode) {
        if (episode != null) "${mediaItem?.title}: ${episode.title}" else mediaItem?.title ?: "Video Player"
    }

    var isPlaying by remember { mutableStateOf(true) }
    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var totalDurationMs by remember { mutableLongStateOf(0L) }
    var isBuffering by remember { mutableStateOf(true) }
    var areControlsVisible by remember { mutableStateOf(true) }
    var isLocked by remember { mutableStateOf(false) }
    var isFullscreen by remember { mutableStateOf(false) }

    // Settings selections matching reference UI
    var selectedQuality by remember { mutableStateOf("Auto") }
    var selectedSubtitle by remember { mutableStateOf("Off") }
    var selectedSpeed by remember { mutableStateOf("1.0x") }

    val qualityOptions = listOf("Auto", "1080p", "720p", "480p", "360p")
    val subtitleOptions = listOf("Off", "English", "Urdu", "Arabic", "Spanish")
    val speedOptions = listOf("0.5x", "0.75x", "1.0x", "1.25x", "1.5x", "2.0x")

    // ExoPlayer creation & lifecycle
    val exoPlayer = remember(streamUrl) {
        ExoPlayer.Builder(context).build().apply {
            val item = MediaItem.fromUri(Uri.parse(streamUrl))
            setMediaItem(item)
            prepare()
            playWhenReady = true
        }
    }

    // Handle orientation changes
    val activity = context as? Activity
    BackHandler {
        if (isFullscreen) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            isFullscreen = false
        } else {
            onBackClick()
        }
    }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                isBuffering = state == Player.STATE_BUFFERING
                if (state == Player.STATE_READY) {
                    totalDurationMs = exoPlayer.duration.coerceAtLeast(0L)
                }
            }

            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }
        }
        exoPlayer.addListener(listener)

        // Record history
        mediaItem?.let {
            coroutineScope.launch {
                repository.recordWatch(it)
            }
        }

        onDispose {
            // Save progress on exit
            mediaItem?.let { item ->
                coroutineScope.launch {
                    repository.saveContinueWatching(
                        mediaItem = item,
                        episode = episode,
                        seasonNumber = episode?.let { ep ->
                            item.seasons.firstOrNull { it.episodes.any { e -> e.id == ep.id } }?.seasonNumber
                        },
                        progressMs = exoPlayer.currentPosition,
                        durationMs = exoPlayer.duration.coerceAtLeast(1L)
                    )
                }
            }
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Polling progress
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            currentPositionMs = exoPlayer.currentPosition.coerceAtLeast(0L)
            totalDurationMs = exoPlayer.duration.coerceAtLeast(0L)
            delay(1000)
        }
    }

    // Auto hide controls after 4 seconds
    LaunchedEffect(areControlsVisible, isPlaying) {
        if (areControlsVisible && isPlaying && !isLocked) {
            delay(4000)
            areControlsVisible = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("video_player_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Video Surface
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(if (isFullscreen) 1f else 0.52f)
                    .background(Color.Black)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        areControlsVisible = !areControlsVisible
                    }
            ) {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = false
                            layoutParams = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Buffering indicator
                if (isBuffering) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = BeastGoldBright
                    )
                }

                // Controls Overlay
                if (areControlsVisible) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xCC000000),
                                        Color.Transparent,
                                        Color(0xEE000000)
                                    )
                                )
                            )
                    ) {
                        // Top Bar: Back, Title, CC, Settings
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                IconButton(
                                    onClick = {
                                        if (isFullscreen) {
                                            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                                            isFullscreen = false
                                        } else {
                                            onBackClick()
                                        }
                                    },
                                    modifier = Modifier.testTag("player_back_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = displayTitle,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            if (!isLocked) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            Toast.makeText(context, "Subtitles: $selectedSubtitle", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ClosedCaption,
                                            contentDescription = "Closed Captions",
                                            tint = if (selectedSubtitle != "Off") BeastGoldBright else Color.White
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            Toast.makeText(context, "Playback settings", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Settings,
                                            contentDescription = "Settings",
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        // Bottom Scrubbing & Playback Controls
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            if (!isLocked) {
                                // Time Labels
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${formatDuration(currentPositionMs)} / ${formatDuration(totalDurationMs)}",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Icon(
                                        imageVector = Icons.Default.AspectRatio,
                                        contentDescription = "Aspect Ratio",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clickable {
                                                Toast.makeText(context, "Aspect ratio adjusted", Toast.LENGTH_SHORT).show()
                                            }
                                    )
                                }

                                // Gold Scrubber Slider
                                Slider(
                                    value = if (totalDurationMs > 0) currentPositionMs.toFloat() / totalDurationMs else 0f,
                                    onValueChange = { fraction ->
                                        val newPos = (fraction * totalDurationMs).toLong()
                                        currentPositionMs = newPos
                                        exoPlayer.seekTo(newPos)
                                    },
                                    colors = SliderDefaults.colors(
                                        thumbColor = Color.White,
                                        activeTrackColor = BeastGoldBright,
                                        inactiveTrackColor = Color(0x66FFFFFF)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("player_scrubber")
                                )
                            }

                            // Control Buttons: Lock, -10s, Play/Pause, +10s, Fullscreen
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Lock Icon
                                IconButton(
                                    onClick = { isLocked = !isLocked },
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                        contentDescription = "Lock",
                                        tint = if (isLocked) BeastGoldBright else Color.White
                                    )
                                }

                                if (!isLocked) {
                                    // -10s Rewind
                                    IconButton(
                                        onClick = {
                                            exoPlayer.seekTo((exoPlayer.currentPosition - 10000).coerceAtLeast(0L))
                                        },
                                        modifier = Modifier.size(44.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Replay10,
                                            contentDescription = "Rewind 10s",
                                            tint = Color.White,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }

                                    // Play/Pause Big Button
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(CircleShape)
                                            .background(Color(0x33FFFFFF))
                                            .clickable {
                                                if (exoPlayer.isPlaying) {
                                                    exoPlayer.pause()
                                                } else {
                                                    exoPlayer.play()
                                                }
                                            }
                                            .testTag("player_play_pause_button"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = if (isPlaying) "Pause" else "Play",
                                            tint = Color.White,
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }

                                    // +10s Forward
                                    IconButton(
                                        onClick = {
                                            exoPlayer.seekTo((exoPlayer.currentPosition + 10000).coerceAtMost(exoPlayer.duration))
                                        },
                                        modifier = Modifier.size(44.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Forward10,
                                            contentDescription = "Forward 10s",
                                            tint = Color.White,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }

                                    // Fullscreen Toggle
                                    IconButton(
                                        onClick = {
                                            isFullscreen = !isFullscreen
                                            if (isFullscreen) {
                                                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                                            } else {
                                                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                                            }
                                        },
                                        modifier = Modifier.size(42.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                            contentDescription = "Fullscreen",
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Lower Section: Exactly matching reference UI with Quality, Subtitles, Speed panel!
            if (!isFullscreen) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.48f)
                        .background(BeastBackground)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Quality Column
                        PlayerOptionColumn(
                            title = "Quality",
                            options = qualityOptions,
                            selectedOption = selectedQuality,
                            onOptionSelected = {
                                selectedQuality = it
                                Toast.makeText(context, "Quality set to $it", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1.1f)
                        )

                        // Subtitles Column
                        PlayerOptionColumn(
                            title = "Subtitles",
                            options = subtitleOptions,
                            selectedOption = selectedSubtitle,
                            onOptionSelected = {
                                selectedSubtitle = it
                                Toast.makeText(context, "Subtitles: $it", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1.1f)
                        )

                        // Speed Column
                        PlayerOptionColumn(
                            title = "Speed",
                            options = speedOptions,
                            selectedOption = selectedSpeed,
                            onOptionSelected = {
                                selectedSpeed = it
                                val rate = it.replace("x", "").toFloatOrNull() ?: 1.0f
                                exoPlayer.playbackParameters = PlaybackParameters(rate)
                                Toast.makeText(context, "Playback speed: $it", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerOptionColumn(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BeastSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BeastCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 10.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
            )

            options.forEach { option ->
                val isSelected = option == selectedOption
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Color(0xFF232530) else Color.Transparent)
                        .clickable { onOptionSelected(option) }
                        .padding(horizontal = 8.dp, vertical = 7.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = option,
                            color = if (isSelected) Color.White else BeastTextSecondary,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = BeastGoldBright,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatDuration(millis: Long): String {
    val totalSeconds = (millis / 1000).coerceAtLeast(0)
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        "%d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%02d:%02d".format(minutes, seconds)
    }
}
