package com.example.ui.screens

import android.app.Activity
import android.content.pm.ActivityInfo
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.data.repository.MovieRepository
import com.example.ui.theme.BeastBackground
import com.example.ui.theme.BeastCardBorder
import com.example.ui.theme.BeastGoldBright
import com.example.ui.theme.BeastGoldDark
import com.example.ui.theme.BeastGoldPrimary
import com.example.ui.theme.BeastSurface
import com.example.ui.theme.BeastSurfaceElevated
import com.example.ui.theme.BeastSurfaceVariant
import com.example.ui.theme.BeastTextPrimary
import com.example.ui.theme.BeastTextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class PlayerMenuSection(val title: String, val icon: ImageVector) {
    QUALITY("Quality", Icons.Default.HighQuality),
    AUDIO("Audio / Language", Icons.Default.Audiotrack),
    SUBTITLES("Subtitles", Icons.Default.ClosedCaption),
    SPEED("Playback Speed", Icons.Default.Speed),
    ASPECT("Aspect Ratio", Icons.Default.AspectRatio)
}

data class RealTrackInfo(
    val id: String,
    val label: String,
    val language: String?,
    val isSelected: Boolean,
    val trackGroup: androidx.media3.common.Tracks.Group? = null,
    val trackIndex: Int = 0
)

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
    val activity = context as? Activity

    val mediaItem = remember(mediaId) { repository.getMediaItemById(mediaId) }
    val episode = remember(episodeId, mediaItem) {
        if (episodeId != null && mediaItem != null) {
            mediaItem.seasons.flatMap { it.episodes }.firstOrNull { it.id == episodeId }
        } else null
    }

    val streamUrl = remember(mediaItem, episode) {
        val s = episode?.streamUrl ?: mediaItem?.streamUrl ?: ""
        s.ifBlank { "https://macdn.aoneroom.com/media/vone/2024/03/19/0391673747f71d3b6c88e047a10f67ac-sd.mp4" }
    }

    val displayTitle = remember(mediaItem, episode) {
        if (episode != null) "${mediaItem?.title ?: "Beast Movies"}: ${episode.title}"
        else mediaItem?.title ?: "Beast Movies Player"
    }

    var isPlaying by remember { mutableStateOf(true) }
    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var totalDurationMs by remember { mutableLongStateOf(0L) }
    var isBuffering by remember { mutableStateOf(true) }
    var areControlsVisible by remember { mutableStateOf(true) }
    var isLocked by remember { mutableStateOf(false) }
    var isFullscreen by remember { mutableStateOf(false) }
    var playerErrorMessage by remember { mutableStateOf<String?>(null) }

    // In-Player Settings Menu State
    var showInPlayerSettings by remember { mutableStateOf(false) }
    var activeMenuSection by remember { mutableStateOf(PlayerMenuSection.QUALITY) }

    // Dynamic track options from Media3
    val availableQualities = remember { mutableStateListOf<RealTrackInfo>() }
    val availableAudioTracks = remember { mutableStateListOf<RealTrackInfo>() }
    val availableSubtitleTracks = remember { mutableStateListOf<RealTrackInfo>() }

    // Current active parameters
    var currentSpeed by remember { mutableFloatStateOf(1.0f) }
    var currentResizeMode by remember { mutableIntStateOf(AspectRatioFrameLayout.RESIZE_MODE_FIT) }
    var selectedAspectLabel by remember { mutableStateOf("Fit") }

    var playerViewRef by remember { mutableStateOf<PlayerView?>(null) }

    // Media3 ExoPlayer Instance
    val exoPlayer = remember(streamUrl) {
        ExoPlayer.Builder(context).build().apply {
            val item = MediaItem.fromUri(Uri.parse(streamUrl))
            setMediaItem(item)
            prepare()
            playWhenReady = true
        }
    }

    // Restore saved watch position from Room DB
    LaunchedEffect(mediaId) {
        val saved = repository.getContinueWatchingItem(mediaId)
        if (saved != null && saved.progressMs > 5000L) {
            exoPlayer.seekTo(saved.progressMs)
        }
        mediaItem?.let { repository.addToHistory(it) }
    }

    // Back handler: exit fullscreen or navigate back
    BackHandler {
        if (showInPlayerSettings) {
            showInPlayerSettings = false
        } else if (isFullscreen) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            isFullscreen = false
        } else {
            onBackClick()
        }
    }

    // Lifecycle & listener
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                isBuffering = state == Player.STATE_BUFFERING
                if (state == Player.STATE_READY) {
                    totalDurationMs = exoPlayer.duration.coerceAtLeast(0L)
                    playerErrorMessage = null
                }
            }

            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                isBuffering = false
                playerErrorMessage = "Playback error: ${error.errorCodeName}. Tap Retry to reconnect."
            }

            override fun onTracksChanged(tracks: Tracks) {
                // Populate REAL Quality tracks
                availableQualities.clear()
                var hasVideoTracks = false
                for (group in tracks.groups) {
                    if (group.type == C.TRACK_TYPE_VIDEO) {
                        for (i in 0 until group.length) {
                            val format = group.getTrackFormat(i)
                            val height = format.height
                            val width = format.width
                            val label = if (height > 0) "${height}p (${width}x$height)" else "Auto Quality"
                            val isSelected = group.isTrackSelected(i)
                            availableQualities.add(
                                RealTrackInfo(
                                    id = "vid_${format.id ?: i}",
                                    label = label,
                                    language = null,
                                    isSelected = isSelected,
                                    trackGroup = group,
                                    trackIndex = i
                                )
                            )
                            hasVideoTracks = true
                        }
                    }
                }
                if (!hasVideoTracks) {
                    val vf = exoPlayer.videoFormat
                    val label = if (vf != null && vf.height > 0) "${vf.height}p HD" else "Auto / Source"
                    availableQualities.add(RealTrackInfo("auto", label, null, true))
                }

                // Populate REAL Audio tracks
                availableAudioTracks.clear()
                for (group in tracks.groups) {
                    if (group.type == C.TRACK_TYPE_AUDIO) {
                        for (i in 0 until group.length) {
                            val format = group.getTrackFormat(i)
                            val lang = format.language
                            val label = format.label?.ifBlank { null }
                                ?: lang?.ifBlank { null }
                                ?: "Audio Track ${i + 1}"
                            availableAudioTracks.add(
                                RealTrackInfo(
                                    id = "aud_${format.id ?: i}",
                                    label = label,
                                    language = lang,
                                    isSelected = group.isTrackSelected(i),
                                    trackGroup = group,
                                    trackIndex = i
                                )
                            )
                        }
                    }
                }
                if (availableAudioTracks.isEmpty()) {
                    availableAudioTracks.add(RealTrackInfo("def_aud", "Default Audio", "en", true))
                }

                // Populate REAL Subtitle tracks
                availableSubtitleTracks.clear()
                var hasSelectedSub = false
                for (group in tracks.groups) {
                    if (group.type == C.TRACK_TYPE_TEXT) {
                        for (i in 0 until group.length) {
                            val format = group.getTrackFormat(i)
                            val lang = format.language
                            val label = format.label?.ifBlank { null }
                                ?: lang?.ifBlank { null }
                                ?: "Subtitle ${i + 1}"
                            val selected = group.isTrackSelected(i)
                            if (selected) hasSelectedSub = true
                            availableSubtitleTracks.add(
                                RealTrackInfo(
                                    id = "sub_${format.id ?: i}",
                                    label = label,
                                    language = lang,
                                    isSelected = selected,
                                    trackGroup = group,
                                    trackIndex = i
                                )
                            )
                        }
                    }
                }
            }
        }
        exoPlayer.addListener(listener)

        onDispose {
            // Save progress to Room DB
            mediaItem?.let { item ->
                coroutineScope.launch {
                    val pos = exoPlayer.currentPosition.coerceAtLeast(0L)
                    val dur = exoPlayer.duration.coerceAtLeast(1L)
                    repository.saveContinueWatching(
                        item = item,
                        positionMs = pos,
                        durationMs = dur,
                        episodeId = episode?.id,
                        episodeTitle = episode?.title
                    )
                }
            }
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Polling watch position
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            currentPositionMs = exoPlayer.currentPosition.coerceAtLeast(0L)
            totalDurationMs = exoPlayer.duration.coerceAtLeast(0L)
            delay(1000)
        }
    }

    // Periodic watch progress saver (every 5 seconds)
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(5000)
            mediaItem?.let { item ->
                val pos = exoPlayer.currentPosition.coerceAtLeast(0L)
                val dur = exoPlayer.duration.coerceAtLeast(1L)
                repository.saveContinueWatching(
                    item = item,
                    positionMs = pos,
                    durationMs = dur,
                    episodeId = episode?.id,
                    episodeTitle = episode?.title
                )
            }
        }
    }

    // Auto-hide controls after 4 seconds of inactivity
    LaunchedEffect(areControlsVisible, isPlaying, showInPlayerSettings) {
        if (areControlsVisible && isPlaying && !isLocked && !showInPlayerSettings) {
            delay(4000)
            areControlsVisible = false
        }
    }

    // ==================== FULL-SCREEN PLAYER ROOT ====================
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("video_player_screen")
    ) {
        // Video Surface
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (showInPlayerSettings) {
                        showInPlayerSettings = false
                    } else if (!isLocked) {
                        areControlsVisible = !areControlsVisible
                    } else {
                        // When locked, tapping shows only the unlock button momentarily
                        areControlsVisible = !areControlsVisible
                    }
                }
        ) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false
                        resizeMode = currentResizeMode
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        playerViewRef = this
                    }
                },
                update = { pv ->
                    pv.resizeMode = currentResizeMode
                },
                modifier = Modifier.fillMaxSize()
            )

            // Buffering Indicator
            if (isBuffering && playerErrorMessage == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = BeastGoldPrimary,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(54.dp)
                    )
                }
            }

            // Error Overlay with Retry
            if (playerErrorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = playerErrorMessage ?: "Playback failed",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(BeastGoldPrimary)
                                .clickable {
                                    playerErrorMessage = null
                                    exoPlayer.prepare()
                                    exoPlayer.play()
                                }
                                .padding(horizontal = 24.dp, vertical = 10.dp)
                        ) {
                            Text("Retry Playback", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // ==================== OVERLAY CONTROLS ====================
        if (areControlsVisible) {
            if (isLocked) {
                // Locked Mode: Only show Unlock Button
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    IconButton(
                        onClick = { isLocked = false },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.7f))
                            .border(1.dp, BeastGoldPrimary, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Unlock Controls",
                            tint = BeastGoldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            } else {
                // Full Controls Overlay
                Box(modifier = Modifier.fillMaxSize()) {
                    // Top Gradient Scrim
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .align(Alignment.TopCenter)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Black.copy(alpha = 0.85f), Color.Transparent)
                                )
                            )
                    )

                    // Bottom Gradient Scrim
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
                                )
                            )
                    )

                    // Top Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
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
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = displayTitle,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Lock Controls Toggle
                        IconButton(
                            onClick = { isLocked = true },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.LockOpen,
                                contentDescription = "Lock Controls",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // In-Player Settings Gear
                        IconButton(
                            onClick = {
                                activeMenuSection = PlayerMenuSection.QUALITY
                                showInPlayerSettings = true
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Player Settings",
                                tint = BeastGoldPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    // Center Transport Controls
                    Row(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Rewind 10s
                        IconButton(
                            onClick = {
                                val target = (exoPlayer.currentPosition - 10000L).coerceAtLeast(0L)
                                exoPlayer.seekTo(target)
                            },
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.55f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Replay10,
                                contentDescription = "Rewind 10 seconds",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(36.dp))

                        // Play / Pause Primary CTA
                        IconButton(
                            onClick = {
                                if (exoPlayer.isPlaying) {
                                    exoPlayer.pause()
                                } else {
                                    exoPlayer.play()
                                }
                            },
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(BeastGoldBright, BeastGoldPrimary, BeastGoldDark)
                                    )
                                )
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.Black,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(36.dp))

                        // Forward 10s
                        IconButton(
                            onClick = {
                                val target = (exoPlayer.currentPosition + 10000L).coerceAtMost(exoPlayer.duration)
                                exoPlayer.seekTo(target)
                            },
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.55f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Forward10,
                                contentDescription = "Fast Forward 10 seconds",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

                    // Bottom Bar Controls
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        // Scrub Bar Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formatTime(currentPositionMs),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Slider(
                                value = if (totalDurationMs > 0) currentPositionMs.toFloat() / totalDurationMs.toFloat() else 0f,
                                onValueChange = { frac ->
                                    val newPos = (frac * totalDurationMs).toLong()
                                    currentPositionMs = newPos
                                    exoPlayer.seekTo(newPos)
                                },
                                colors = SliderDefaults.colors(
                                    thumbColor = BeastGoldPrimary,
                                    activeTrackColor = BeastGoldPrimary,
                                    inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp)
                            )

                            Text(
                                text = formatTime(totalDurationMs),
                                color = BeastTextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Bottom Actions Row: In-Player Quick Settings & Fullscreen
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Quality Pill
                                InPlayerPillButton(
                                    label = availableQualities.firstOrNull { it.isSelected }?.label?.take(7) ?: "Quality",
                                    icon = Icons.Default.HighQuality,
                                    onClick = {
                                        activeMenuSection = PlayerMenuSection.QUALITY
                                        showInPlayerSettings = true
                                    }
                                )

                                // Subtitles Pill
                                InPlayerPillButton(
                                    label = availableSubtitleTracks.firstOrNull { it.isSelected }?.label ?: "Subtitles",
                                    icon = Icons.Default.ClosedCaption,
                                    onClick = {
                                        activeMenuSection = PlayerMenuSection.SUBTITLES
                                        showInPlayerSettings = true
                                    }
                                )

                                // Audio Pill
                                InPlayerPillButton(
                                    label = availableAudioTracks.firstOrNull { it.isSelected }?.label?.take(7) ?: "Audio",
                                    icon = Icons.Default.Audiotrack,
                                    onClick = {
                                        activeMenuSection = PlayerMenuSection.AUDIO
                                        showInPlayerSettings = true
                                    }
                                )

                                // Speed Pill
                                InPlayerPillButton(
                                    label = "${currentSpeed}x",
                                    icon = Icons.Default.Speed,
                                    onClick = {
                                        activeMenuSection = PlayerMenuSection.SPEED
                                        showInPlayerSettings = true
                                    }
                                )
                            }

                            // Fullscreen Toggle
                            IconButton(
                                onClick = {
                                    if (isFullscreen) {
                                        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                                        isFullscreen = false
                                    } else {
                                        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                                        isFullscreen = true
                                    }
                                },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.5f))
                            ) {
                                Icon(
                                    imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                    contentDescription = "Toggle Fullscreen",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // ==================== IN-PLAYER SETTINGS MODAL ====================
        // Clean MovieBox-inspired bottom sheet appearing strictly inside player area
        AnimatedVisibility(
            visible = showInPlayerSettings,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Surface(
                color = BeastSurfaceElevated,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BeastCardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* absorb taps inside sheet */ }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp)
                ) {
                    // Header with close button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Playback Settings",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { showInPlayerSettings = false },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Settings",
                                tint = BeastTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tab selector row (Quality, Audio, Subtitles, Speed, Aspect)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(BeastBackground)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        PlayerMenuSection.entries.forEach { section ->
                            val isCurrent = activeMenuSection == section
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isCurrent) BeastGoldPrimary else Color.Transparent)
                                    .clickable { activeMenuSection = section }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = section.title.split(" ").first(),
                                    color = if (isCurrent) Color.Black else BeastTextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Content Options for Active Section
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                    ) {
                        when (activeMenuSection) {
                            PlayerMenuSection.QUALITY -> {
                                if (availableQualities.isEmpty()) {
                                    item {
                                        Text(
                                            text = "Auto (Default stream quality)",
                                            color = BeastTextSecondary,
                                            fontSize = 14.sp,
                                            modifier = Modifier.padding(vertical = 12.dp)
                                        )
                                    }
                                } else {
                                    items(availableQualities) { quality ->
                                        InPlayerOptionItem(
                                            title = quality.label,
                                            subtitle = "Resolution from playback source",
                                            isSelected = quality.isSelected,
                                            onClick = {
                                                quality.trackGroup?.let { grp ->
                                                    exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                                                        .buildUpon()
                                                        .setOverrideForType(
                                                            androidx.media3.common.TrackSelectionOverride(
                                                                grp.mediaTrackGroup,
                                                                quality.trackIndex
                                                            )
                                                        )
                                                        .build()
                                                }
                                                showInPlayerSettings = false
                                            }
                                        )
                                    }
                                }
                            }

                            PlayerMenuSection.AUDIO -> {
                                if (availableAudioTracks.isEmpty()) {
                                    item {
                                        Text(
                                            text = "Default Audio (Source)",
                                            color = BeastTextSecondary,
                                            fontSize = 14.sp,
                                            modifier = Modifier.padding(vertical = 12.dp)
                                        )
                                    }
                                } else {
                                    items(availableAudioTracks) { audio ->
                                        InPlayerOptionItem(
                                            title = audio.label,
                                            subtitle = audio.language?.let { "Language: $it" } ?: "Main track",
                                            isSelected = audio.isSelected,
                                            onClick = {
                                                if (audio.trackGroup != null) {
                                                    exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                                                        .buildUpon()
                                                        .setOverrideForType(
                                                            androidx.media3.common.TrackSelectionOverride(
                                                                audio.trackGroup.mediaTrackGroup,
                                                                audio.trackIndex
                                                            )
                                                        )
                                                        .build()
                                                } else if (audio.language != null) {
                                                    exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                                                        .buildUpon()
                                                        .setPreferredAudioLanguage(audio.language)
                                                        .build()
                                                }
                                                showInPlayerSettings = false
                                            }
                                        )
                                    }
                                }
                            }

                            PlayerMenuSection.SUBTITLES -> {
                                item {
                                    InPlayerOptionItem(
                                        title = "Off",
                                        subtitle = "Disable subtitles",
                                        isSelected = availableSubtitleTracks.none { it.isSelected },
                                        onClick = {
                                            exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                                                .buildUpon()
                                                .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, true)
                                                .build()
                                            showInPlayerSettings = false
                                        }
                                    )
                                }
                                if (availableSubtitleTracks.isEmpty()) {
                                    item {
                                        Text(
                                            text = "No subtitles available for this source",
                                            color = BeastTextSecondary,
                                            fontSize = 13.sp,
                                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp)
                                        )
                                    }
                                } else {
                                    items(availableSubtitleTracks) { sub ->
                                        InPlayerOptionItem(
                                            title = sub.label,
                                            subtitle = sub.language?.let { "Language code: $it" } ?: "Embedded subtitle",
                                            isSelected = sub.isSelected,
                                            onClick = {
                                                if (sub.trackGroup != null) {
                                                    exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                                                        .buildUpon()
                                                        .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, false)
                                                        .setOverrideForType(
                                                            androidx.media3.common.TrackSelectionOverride(
                                                                sub.trackGroup.mediaTrackGroup,
                                                                sub.trackIndex
                                                            )
                                                        )
                                                        .build()
                                                } else if (sub.language != null) {
                                                    exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                                                        .buildUpon()
                                                        .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, false)
                                                        .setPreferredTextLanguage(sub.language)
                                                        .build()
                                                }
                                                showInPlayerSettings = false
                                            }
                                        )
                                    }
                                }
                            }

                            PlayerMenuSection.SPEED -> {
                                val speedOptions = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
                                items(speedOptions) { spd ->
                                    InPlayerOptionItem(
                                        title = "${spd}x",
                                        subtitle = if (spd == 1.0f) "Normal speed" else "Speed multiplier",
                                        isSelected = currentSpeed == spd,
                                        onClick = {
                                            currentSpeed = spd
                                            exoPlayer.playbackParameters = PlaybackParameters(spd)
                                            showInPlayerSettings = false
                                        }
                                    )
                                }
                            }

                            PlayerMenuSection.ASPECT -> {
                                val aspectOptions = listOf(
                                    Pair("Fit", AspectRatioFrameLayout.RESIZE_MODE_FIT),
                                    Pair("Fill", AspectRatioFrameLayout.RESIZE_MODE_FILL),
                                    Pair("Zoom", AspectRatioFrameLayout.RESIZE_MODE_ZOOM),
                                    Pair("Stretch", AspectRatioFrameLayout.RESIZE_MODE_FILL)
                                )
                                items(aspectOptions) { (label, mode) ->
                                    InPlayerOptionItem(
                                        title = label,
                                        subtitle = when (label) {
                                            "Fit" -> "Preserve original aspect ratio"
                                            "Fill" -> "Match player dimensions"
                                            "Zoom" -> "Crop edges to fill screen"
                                            else -> "Stretch across full frame"
                                        },
                                        isSelected = selectedAspectLabel == label,
                                        onClick = {
                                            selectedAspectLabel = label
                                            currentResizeMode = mode
                                            playerViewRef?.resizeMode = mode
                                            showInPlayerSettings = false
                                        }
                                    )
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
private fun InPlayerPillButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black.copy(alpha = 0.6f))
            .border(1.dp, BeastCardBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BeastGoldPrimary,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = label,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun InPlayerOptionItem(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) BeastSurfaceVariant else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = title,
                color = if (isSelected) BeastGoldPrimary else Color.White,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            Text(
                text = subtitle,
                color = BeastTextSecondary,
                fontSize = 11.sp
            )
        }
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = BeastGoldPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private fun formatTime(millis: Long): String {
    val totalSeconds = (millis / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
