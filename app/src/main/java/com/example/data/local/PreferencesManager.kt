package com.example.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "beast_movies_prefs")

class PreferencesManager(private val context: Context) {
    companion object {
        val KEY_DARK_THEME = booleanPreferencesKey("dark_theme")
        val KEY_LANGUAGE = stringPreferencesKey("language")
        val KEY_AUTO_QUALITY = booleanPreferencesKey("auto_quality")
        val KEY_PLAYBACK_SPEED = floatPreferencesKey("playback_speed")
        val KEY_SUBTITLE_LANG = stringPreferencesKey("subtitle_lang")
        val KEY_AUTOPLAY_NEXT = booleanPreferencesKey("autoplay_next")
    }

    val darkTheme: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_DARK_THEME] ?: true
    }

    val appLanguage: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_LANGUAGE] ?: "English"
    }

    val autoQuality: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_AUTO_QUALITY] ?: true
    }

    val playbackSpeed: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[KEY_PLAYBACK_SPEED] ?: 1.0f
    }

    val subtitleLanguage: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_SUBTITLE_LANG] ?: "Off"
    }

    val autoplayNext: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_AUTOPLAY_NEXT] ?: true
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { it[KEY_DARK_THEME] = enabled }
    }

    suspend fun setLanguage(language: String) {
        context.dataStore.edit { it[KEY_LANGUAGE] = language }
    }

    suspend fun setAutoQuality(enabled: Boolean) {
        context.dataStore.edit { it[KEY_AUTO_QUALITY] = enabled }
    }

    suspend fun setPlaybackSpeed(speed: Float) {
        context.dataStore.edit { it[KEY_PLAYBACK_SPEED] = speed }
    }

    suspend fun setSubtitleLanguage(lang: String) {
        context.dataStore.edit { it[KEY_SUBTITLE_LANG] = lang }
    }

    suspend fun setAutoplayNext(enabled: Boolean) {
        context.dataStore.edit { it[KEY_AUTOPLAY_NEXT] = enabled }
    }
}
