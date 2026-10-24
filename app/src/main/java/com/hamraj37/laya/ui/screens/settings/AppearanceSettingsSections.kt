package com.hamraj37.laya.ui.screens

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Notes
import androidx.compose.material.icons.rounded.Animation
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BlurOff
import androidx.compose.material.icons.rounded.BlurOn
import androidx.compose.material.icons.rounded.Brightness4
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.Gradient
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.MotionPhotosOff
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hamraj37.laya.R
import com.hamraj37.laya.data.lyrics.LyricsSource
import com.hamraj37.laya.data.lyrics.translationLanguageName
import com.hamraj37.laya.data.settings.AppSettings
import com.hamraj37.laya.data.settings.ThemeMode
import com.hamraj37.laya.ui.player.fullBleedArtworkAvailable
import java.util.Locale

@Composable
internal fun AppearanceSettingsGroup(
    search: SettingsSearch,
    theme: ThemeMode,
    reduceAnimation: Boolean,
    reduceDynamicBlur: Boolean,
    liquidGlass: Boolean,
    liquidGlassSupported: Boolean,
    windowWidth: Dp,
    fullBleedArtwork: Boolean,
    legacyMeshGradient: Boolean,
    animatedCanvas: Boolean,
    canvasOverCellular: Boolean,
    signatureApiEnabled: Boolean,
    signatureApiUrl: String,
    syncedLyrics: Boolean,
    lyricsBlur: Boolean,
    lyricsSources: Set<LyricsSource>,
    translationLanguage: String,
    onSpotifyCanvasAuth: () -> Unit,
    onEditSignatureApi: () -> Unit,
    onLyricsSources: () -> Unit,
    onTranslationLanguage: () -> Unit,
) {
    SearchableSettingsGroup(search, header = stringResource(R.string.appearance)) {
        val themeTitle = stringResource(R.string.theme)
        row(themeTitle, "dark mode", "light mode") {
            SettingsRow(icon = Icons.Rounded.Brightness4, title = themeTitle)
            SegmentedControl(
                options = ThemeMode.entries.map { it.localizedLabel() },
                selectedIndex = ThemeMode.entries.indexOf(theme),
                onSelect = { AppSettings.setThemeMode(ThemeMode.entries[it]) },
                modifier = Modifier.padding(start = ROW_INSET, end = ROW_INSET, bottom = 14.dp),
            )
        }
        val reduceAnimationTitle = stringResource(R.string.reduce_animation)
        row(reduceAnimationTitle, "motion") {
            SettingsRow(
                icon = Icons.Rounded.MotionPhotosOff,
                title = reduceAnimationTitle,
                subtitle = stringResource(R.string.reduce_animation_subtitle),
                trailing = {
                    Switch(
                        checked = reduceAnimation,
                        onCheckedChange = AppSettings::setReduceAnimation,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedBorderColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                },
                onClick = { AppSettings.setReduceAnimation(!reduceAnimation) },
            )
        }
        val reduceDynamicBlurTitle = stringResource(R.string.reduce_dynamic_blur)
        row(reduceDynamicBlurTitle, "blur", "performance") {
            SettingsRow(
                icon = Icons.Rounded.BlurOff,
                title = reduceDynamicBlurTitle,
                subtitle = stringResource(R.string.reduce_dynamic_blur_subtitle),
                trailing = {
                    Switch(
                        checked = reduceDynamicBlur,
                        onCheckedChange = AppSettings::setReduceDynamicBlur,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedBorderColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                },
                onClick = { AppSettings.setReduceDynamicBlur(!reduceDynamicBlur) },
            )
        }
        val liquidGlassTitle = stringResource(R.string.liquid_glass)
        row(liquidGlassTitle, "glass", "blur") {
            SettingsRow(
                icon = Icons.Rounded.AutoAwesome,
                title = liquidGlassTitle,
                subtitle = stringResource(
                    if (liquidGlassSupported) {
                        R.string.liquid_glass_subtitle
                    } else {
                        R.string.liquid_glass_unavailable
                    },
                ),
                enabled = liquidGlassSupported,
                trailing = {
                    Switch(
                        checked = liquidGlass,
                        onCheckedChange = AppSettings::setLiquidGlass,
                        enabled = liquidGlassSupported,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedBorderColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                },
                onClick = { AppSettings.setLiquidGlass(!liquidGlass) },
            )
        }
        if (fullBleedArtworkAvailable(windowWidth)) {
            val fullScreenCoverArtTitle = stringResource(R.string.full_screen_cover_art)
            row(fullScreenCoverArtTitle, "artwork", "player") {
                SettingsRow(
                    icon = Icons.Rounded.Fullscreen,
                    title = fullScreenCoverArtTitle,
                    subtitle = stringResource(R.string.full_screen_cover_art_subtitle),
                    trailing = {
                        Switch(
                            checked = fullBleedArtwork,
                            onCheckedChange = AppSettings::setFullBleedArtwork,
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = MaterialTheme.colorScheme.primary,
                                checkedBorderColor = MaterialTheme.colorScheme.primary,
                            ),
                        )
                    },
                    onClick = { AppSettings.setFullBleedArtwork(!fullBleedArtwork) },
                )
            }
        }
        val legacyMeshGradientTitle = stringResource(R.string.legacy_mesh_gradient)
        row(legacyMeshGradientTitle, "background", "player") {
            SettingsRow(
                icon = Icons.Rounded.Gradient,
                title = legacyMeshGradientTitle,
                subtitle = stringResource(R.string.legacy_mesh_gradient_subtitle),
                trailing = {
                    Switch(
                        checked = legacyMeshGradient,
                        onCheckedChange = AppSettings::setLegacyMeshGradient,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedBorderColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                },
                onClick = { AppSettings.setLegacyMeshGradient(!legacyMeshGradient) },
            )
        }
        val animatedCoverArtTitle = stringResource(R.string.animated_cover_art)
        row(animatedCoverArtTitle, "canvas", "video", "spotify") {
            SettingsRow(
                icon = Icons.Rounded.Animation,
                title = animatedCoverArtTitle,
                subtitle = stringResource(R.string.animated_cover_art_subtitle),
                trailing = {
                    Switch(
                        checked = animatedCanvas,
                        onCheckedChange = AppSettings::setAnimatedCanvas,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedBorderColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                },
                onClick = { AppSettings.setAnimatedCanvas(!animatedCanvas) },
            )
        }
        if (animatedCanvas) {
            val coverCellularTitle = stringResource(R.string.animated_cover_cellular)
            row(coverCellularTitle, "canvas", "cellular", divided = false) {
                SettingsSubRow(
                    title = coverCellularTitle,
                    checked = canvasOverCellular,
                    onCheckedChange = AppSettings::setCanvasOverCellular,
                )
            }
            val spotifyCanvasTitle = stringResource(R.string.integrate_spotify_canvas)
            row(spotifyCanvasTitle, "spotify", "canvas", divided = false) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onSpotifyCanvasAuth)
                        .padding(start = ROW_INSET, end = ROW_INSET, top = 4.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = spotifyCanvasTitle,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Chevron()
                }
            }
        }
        val artistSignaturesTitle = stringResource(R.string.artist_signatures)
        row(artistSignaturesTitle, "signature", "artist", "logo") {
            SettingsRow(
                icon = Icons.Rounded.Draw,
                title = artistSignaturesTitle,
                subtitle = stringResource(R.string.artist_signatures_subtitle),
                trailing = {
                    Switch(
                        checked = signatureApiEnabled,
                        onCheckedChange = AppSettings::setSignatureApiEnabled,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedBorderColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                },
                onClick = { AppSettings.setSignatureApiEnabled(!signatureApiEnabled) },
            )
        }
        if (signatureApiEnabled) {
            val signatureUrlTitle = stringResource(R.string.artist_signatures_url)
            row(signatureUrlTitle, "signature", "api", "url", divided = false) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onEditSignatureApi)
                        .padding(start = ROW_INSET, end = ROW_INSET, top = 4.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = signatureApiUrl.ifBlank {
                            stringResource(R.string.artist_signatures_unconfigured)
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Chevron()
                }
            }
        }
        val syncedLyricsTitle = stringResource(R.string.synced_lyrics)
        row(syncedLyricsTitle, "lyrics", "translation", "lrclib", "musixmatch") {
            SettingsRow(
                icon = Icons.AutoMirrored.Rounded.Notes,
                title = syncedLyricsTitle,
                subtitle = stringResource(R.string.synced_lyrics_subtitle),
                trailing = {
                    Switch(
                        checked = syncedLyrics,
                        onCheckedChange = AppSettings::setSyncedLyrics,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedBorderColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                },
                onClick = { AppSettings.setSyncedLyrics(!syncedLyrics) },
            )
        }
        if (syncedLyrics) {
            val lyricsBlurTitle = stringResource(R.string.blur_unfocused_lyrics)
            row(lyricsBlurTitle, "lyrics", "blur") {
                SettingsRow(
                    icon = Icons.Rounded.BlurOn,
                    title = lyricsBlurTitle,
                    subtitle = stringResource(R.string.blur_unfocused_lyrics_subtitle),
                    trailing = {
                        Switch(
                            checked = lyricsBlur,
                            onCheckedChange = AppSettings::setLyricsBlur,
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = MaterialTheme.colorScheme.primary,
                                checkedBorderColor = MaterialTheme.colorScheme.primary,
                            ),
                        )
                    },
                    onClick = { AppSettings.setLyricsBlur(!lyricsBlur) },
                )
            }
            val lyricsSourcesTitle = stringResource(R.string.lyrics_sources)
            row(lyricsSourcesTitle, "lyrics", "lrclib", "musixmatch") {
                SettingsRow(
                    icon = Icons.Rounded.Language,
                    title = lyricsSourcesTitle,
                    subtitle = lyricsSources
                        .sortedBy { it.ordinal }
                        .joinToString(", ") { it.label }
                        .ifEmpty { stringResource(R.string.no_lyrics_sources_enabled) },
                    trailing = { Chevron() },
                    onClick = onLyricsSources,
                )
            }
            val translationLanguageTitle = stringResource(R.string.translation_language)
            row(translationLanguageTitle, "lyrics", "translate") {
                SettingsRow(
                    icon = Icons.Rounded.Translate,
                    title = translationLanguageTitle,
                    subtitle = if (translationLanguage.isBlank()) {
                        stringResource(R.string.translation_language_subtitle)
                    } else {
                        translationLanguageName(
                            translationLanguage,
                            AppCompatDelegate.getApplicationLocales().get(0) ?: Locale.getDefault(),
                        )
                    },
                    trailing = { Chevron() },
                    onClick = onTranslationLanguage,
                )
            }
        }
    }
}
