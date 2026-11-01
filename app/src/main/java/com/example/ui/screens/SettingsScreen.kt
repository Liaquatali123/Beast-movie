package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BeastBackground
import com.example.ui.theme.BeastGoldBright
import com.example.ui.theme.BeastGoldPrimary
import com.example.ui.theme.BeastSurface
import com.example.ui.theme.BeastSurfaceVariant
import com.example.ui.theme.BeastTextPrimary
import com.example.ui.theme.BeastTextSecondary
import com.example.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val darkTheme by viewModel.darkTheme.collectAsState()
    val language by viewModel.language.collectAsState()
    val autoQuality by viewModel.autoQuality.collectAsState()
    val playbackSpeed by viewModel.playbackSpeed.collectAsState()
    val subtitleLang by viewModel.subtitleLang.collectAsState()

    val context = LocalContext.current

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showSpeedDialog by remember { mutableStateOf(false) }
    var showSubtitleDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showClearCacheDialog by remember { mutableStateOf(false) }

    val languages = listOf("English", "Urdu", "Hindi", "Arabic", "Spanish", "French")
    val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
    val subtitles = listOf("Off", "English", "Urdu", "Arabic", "Spanish", "French")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BeastBackground)
            .testTag("settings_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Header: Gear + Settings + Cast icon
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Settings",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(onClick = {
                        Toast.makeText(context, "Scanning for Chromecast / Android TV devices...", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Cast,
                            contentDescription = "Cast",
                            tint = Color.White
                        )
                    }
                }
            }

            // Section: Appearance
            item {
                SettingsSectionHeader("Appearance")
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = BeastSurface),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column {
                        // Dark Theme Toggle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Dark Theme",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Switch(
                                checked = darkTheme,
                                onCheckedChange = { viewModel.setDarkTheme(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = BeastGoldBright,
                                    uncheckedThumbColor = Color.LightGray,
                                    uncheckedTrackColor = Color(0xFF333333)
                                )
                            )
                        }

                        HorizontalDivider(color = BeastSurfaceVariant, thickness = 1.dp)

                        // Language
                        SettingsClickableRow(
                            title = "Language",
                            value = language,
                            onClick = { showLanguageDialog = true }
                        )
                    }
                }
            }

            // Section: Playback
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionHeader("Playback")
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = BeastSurface),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column {
                        // Auto Quality Toggle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Auto Quality",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Switch(
                                checked = autoQuality,
                                onCheckedChange = { viewModel.setAutoQuality(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = BeastGoldBright,
                                    uncheckedThumbColor = Color.LightGray,
                                    uncheckedTrackColor = Color(0xFF333333)
                                )
                            )
                        }

                        HorizontalDivider(color = BeastSurfaceVariant, thickness = 1.dp)

                        // Playback Speed
                        SettingsClickableRow(
                            title = "Playback Speed",
                            value = "${playbackSpeed}x",
                            onClick = { showSpeedDialog = true }
                        )

                        HorizontalDivider(color = BeastSurfaceVariant, thickness = 1.dp)

                        // Subtitles
                        SettingsClickableRow(
                            title = "Subtitles",
                            value = subtitleLang,
                            onClick = { showSubtitleDialog = true }
                        )
                    }
                }
            }

            // Section: About
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionHeader("About")
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = BeastSurface),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column {
                        // App Brand Card Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.beast_lion_head),
                                contentDescription = "Beast Movies",
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Beast Movies",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Version 1.0.0 (Native Android Build)",
                                    color = BeastTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        HorizontalDivider(color = BeastSurfaceVariant, thickness = 1.dp)

                        // Privacy Policy
                        SettingsClickableRow(
                            title = "Privacy Policy",
                            value = "",
                            onClick = { showPrivacyDialog = true }
                        )

                        HorizontalDivider(color = BeastSurfaceVariant, thickness = 1.dp)

                        // Terms of Service
                        SettingsClickableRow(
                            title = "Terms of Service",
                            value = "",
                            onClick = { showTermsDialog = true }
                        )

                        HorizontalDivider(color = BeastSurfaceVariant, thickness = 1.dp)

                        // Clear Cache
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showClearCacheDialog = true }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Clear Cache",
                                color = BeastGoldBright,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = BeastGoldBright,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Language Dialog
        if (showLanguageDialog) {
            AlertDialog(
                onDismissRequest = { showLanguageDialog = false },
                containerColor = BeastSurface,
                title = { Text("Select App Language", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        languages.forEach { lang ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.setLanguage(lang)
                                        showLanguageDialog = false
                                        Toast.makeText(context, "Language set to $lang", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = lang, color = if (lang == language) BeastGoldBright else Color.White, fontSize = 15.sp)
                                if (lang == language) {
                                    Text(text = "✓", color = BeastGoldBright, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showLanguageDialog = false }) {
                        Text("Cancel", color = BeastTextSecondary)
                    }
                }
            )
        }

        // Speed Dialog
        if (showSpeedDialog) {
            AlertDialog(
                onDismissRequest = { showSpeedDialog = false },
                containerColor = BeastSurface,
                title = { Text("Default Playback Speed", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        speeds.forEach { speed ->
                            val speedStr = "${speed}x"
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.setPlaybackSpeed(speed)
                                        showSpeedDialog = false
                                        Toast.makeText(context, "Default speed set to $speedStr", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = speedStr, color = if (speed == playbackSpeed) BeastGoldBright else Color.White, fontSize = 15.sp)
                                if (speed == playbackSpeed) {
                                    Text(text = "✓", color = BeastGoldBright, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSpeedDialog = false }) {
                        Text("Cancel", color = BeastTextSecondary)
                    }
                }
            )
        }

        // Subtitle Dialog
        if (showSubtitleDialog) {
            AlertDialog(
                onDismissRequest = { showSubtitleDialog = false },
                containerColor = BeastSurface,
                title = { Text("Default Subtitles", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        subtitles.forEach { sub ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.setSubtitleLang(sub)
                                        showSubtitleDialog = false
                                        Toast.makeText(context, "Subtitles set to $sub", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = sub, color = if (sub == subtitleLang) BeastGoldBright else Color.White, fontSize = 15.sp)
                                if (sub == subtitleLang) {
                                    Text(text = "✓", color = BeastGoldBright, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSubtitleDialog = false }) {
                        Text("Cancel", color = BeastTextSecondary)
                    }
                }
            )
        }

        // Privacy Policy Dialog
        if (showPrivacyDialog) {
            AlertDialog(
                onDismissRequest = { showPrivacyDialog = false },
                containerColor = BeastSurface,
                title = { Text("Privacy Policy", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                        Text(
                            text = "Beast Movies is committed to protecting your privacy.\n\n" +
                                "1. Information Collection: We store minimal preferences locally on your device (Watchlist, History, Continue Watching progress).\n\n" +
                                "2. Media Streaming: Video streams are requested directly from authorized content delivery servers over encrypted HTTPS.\n\n" +
                                "3. Security: We do not share personal data with unauthorized third parties.\n\n" +
                                "Version: 1.0.0 (November 2026)",
                            color = BeastTextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showPrivacyDialog = false }) {
                        Text("Close", color = BeastGoldBright)
                    }
                }
            )
        }

        // Terms of Service Dialog
        if (showTermsDialog) {
            AlertDialog(
                onDismissRequest = { showTermsDialog = false },
                containerColor = BeastSurface,
                title = { Text("Terms of Service", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                        Text(
                            text = "Welcome to Beast Movies.\n\n" +
                                "1. Authorized Content: All content displayed is provided via authorized APIs and licensed media distribution.\n\n" +
                                "2. Personal Use: This application is provided for private, non-commercial entertainment purposes.\n\n" +
                                "3. Technical Delivery: Adaptive streaming quality depends on your network speed and device decoding capability.\n\n" +
                                "Thank you for choosing Beast Movies!",
                            color = BeastTextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showTermsDialog = false }) {
                        Text("Accept", color = BeastGoldBright)
                    }
                }
            )
        }

        // Clear Cache Confirmation Dialog
        if (showClearCacheDialog) {
            AlertDialog(
                onDismissRequest = { showClearCacheDialog = false },
                containerColor = BeastSurface,
                title = { Text("Clear App Cache?", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        text = "This will clear temporary search logs and cached image metadata. Your saved My List titles will be preserved.",
                        color = BeastTextSecondary,
                        fontSize = 13.sp
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.clearCache()
                            showClearCacheDialog = false
                            Toast.makeText(context, "Cache successfully cleared", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("Clear", color = BeastGoldBright, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearCacheDialog = false }) {
                        Text("Cancel", color = BeastTextSecondary)
                    }
                }
            )
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        color = Color.White,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun SettingsClickableRow(
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (value.isNotEmpty()) {
                Text(
                    text = value,
                    color = BeastTextSecondary,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = BeastTextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
