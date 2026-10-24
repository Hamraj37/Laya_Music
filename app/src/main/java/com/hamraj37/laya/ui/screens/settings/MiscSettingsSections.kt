package com.hamraj37.laya.ui.screens

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.FileUpload
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LocalOffer
import androidx.compose.material.icons.rounded.MusicOff
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlaylistPlay
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.WebAssetOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.hamraj37.laya.AppIcon
import com.hamraj37.laya.R
import com.hamraj37.laya.data.listentogether.ListenTogether
import com.hamraj37.laya.data.model.Account
import com.hamraj37.laya.data.settings.AppSettings
import com.hamraj37.laya.data.stats.Backup
import com.hamraj37.laya.ui.components.languageDisplayNameRes
import com.hamraj37.laya.ui.icons.LayaIcons
import com.hamraj37.laya.wear.WatchInstaller
import java.util.Locale

@Composable
internal fun AccountIntegrationsGroup(
    search: SettingsSearch,
    signedIn: Boolean,
    account: Account?,
    party: ListenTogether.State,
    onAccountScrobbling: () -> Unit,
    onListenTogether: () -> Unit,
) {
    SearchableSettingsGroup(search) {
        val accountTitle = stringResource(R.string.account_integrations)
        row(accountTitle, "scrobbling", "last.fm", "listenbrainz") {
            SettingsRow(
                icon = Icons.Rounded.Person,
                title = accountTitle,
                subtitle = account?.email?.takeIf { it.isNotBlank() }
                    ?: stringResource(if (signedIn) R.string.signed_in else R.string.not_signed_in),
                onClick = onAccountScrobbling,
            )
        }
        val listenTogetherTitle = stringResource(R.string.listen_together)
        row(listenTogetherTitle, "jam", "party", "sync", "friends") {
            SettingsRow(
                icon = Icons.Rounded.Groups,
                title = listenTogetherTitle,
                subtitle = party.code?.let {
                    stringResource(R.string.listen_together_in_party, it)
                } ?: stringResource(R.string.listen_together_subtitle),
                badge = party.members.size.takeIf { party.inParty && it > 1 }?.toString(),
                onClick = onListenTogether,
            )
        }
    }
}

@Composable
internal fun PerformanceSettingsGroup(
    search: SettingsSearch,
    highPerformanceMode: Boolean,
    selectedPerformanceRefreshRate: Int,
    supportedRefreshRates: List<Int>,
    onShowPerformanceWarning: () -> Unit,
) {
    SearchableSettingsGroup(search, header = stringResource(R.string.performance)) {
        val highPerformanceModeTitle = stringResource(R.string.high_performance_mode)
        row(highPerformanceModeTitle, "frame rate", "refresh rate", "hz", "battery", "smooth") {
            SettingsRow(
                icon = LayaIcons.Performance,
                title = highPerformanceModeTitle,
                subtitle = if (highPerformanceMode) {
                    stringResource(R.string.high_performance_active, selectedPerformanceRefreshRate)
                } else {
                    stringResource(R.string.high_performance_subtitle)
                },
                trailing = {
                    Switch(
                        checked = highPerformanceMode,
                        onCheckedChange = { enabled ->
                            if (enabled) {
                                onShowPerformanceWarning()
                            } else {
                                AppSettings.setHighPerformanceMode(false)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedBorderColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                },
                onClick = {
                    if (highPerformanceMode) {
                        AppSettings.setHighPerformanceMode(false)
                    } else {
                        onShowPerformanceWarning()
                    }
                },
            )
        }
        if (highPerformanceMode) {
            val refreshRateTitle = stringResource(R.string.refresh_rate)
            row(refreshRateTitle, "hz", "frame rate") {
                SettingsRow(
                    icon = LayaIcons.FrameRate,
                    title = refreshRateTitle,
                )
                SegmentedControl(
                    options = supportedRefreshRates.map { "$it Hz" },
                    selectedIndex = supportedRefreshRates.indexOf(selectedPerformanceRefreshRate),
                    onSelect = { index ->
                        AppSettings.setPerformanceRefreshRate(supportedRefreshRates[index])
                    },
                    modifier = Modifier.padding(
                        start = TEXT_INSET,
                        end = ROW_INSET,
                        bottom = 14.dp,
                    ),
                )
            }
        }
    }
}

@Composable
internal fun WearOsSettingsGroup(
    search: SettingsSearch,
    context: Context,
    onWearOs: () -> Unit,
) {
    if (remember { WatchInstaller.available(context) }) {
        SearchableSettingsGroup(search) {
            row(stringResource(R.string.wear_os_section), "watch", "wear os", "galaxy watch", "pixel watch", "install") {
                WearOsSettingsRow(onClick = onWearOs)
            }
        }
    }
}

@Composable
internal fun YourDataSettingsGroup(
    search: SettingsSearch,
    replayGenres: Boolean,
    exportStatus: String?,
    importStatus: String?,
    exportPicker: ActivityResultLauncher<String>,
    onOpenReplay: () -> Unit,
    onConfirmImport: () -> Unit,
) {
    SearchableSettingsGroup(search, header = stringResource(R.string.your_data)) {
        val replayTitle = stringResource(R.string.replay)
        row(replayTitle, "stats", "history", "wrapped") {
            SettingsRow(
                icon = Icons.Rounded.BarChart,
                title = replayTitle,
                subtitle = stringResource(R.string.replay_subtitle),
                onClick = onOpenReplay,
            )
        }
        val workOutGenresTitle = stringResource(R.string.work_out_genres)
        row(workOutGenresTitle, "genre", "replay") {
            SettingsRow(
                icon = Icons.Rounded.LocalOffer,
                title = workOutGenresTitle,
                subtitle = if (replayGenres) {
                    stringResource(R.string.replay_genres_enabled_subtitle)
                } else {
                    stringResource(R.string.replay_genres_disabled_subtitle)
                },
                trailing = {
                    Switch(
                        checked = replayGenres,
                        onCheckedChange = AppSettings::setReplayGenres,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedBorderColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                },
                onClick = { AppSettings.setReplayGenres(!replayGenres) },
            )
        }
        val exportDataTitle = stringResource(R.string.export_data)
        row(exportDataTitle, "backup") {
            SettingsRow(
                icon = Icons.Rounded.FileUpload,
                title = exportDataTitle,
                subtitle = exportStatus ?: stringResource(R.string.export_data_subtitle),
                onClick = { exportPicker.launch(Backup.suggestedName()) },
            )
        }
        val importDataTitle = stringResource(R.string.import_data)
        row(importDataTitle, "backup", "restore") {
            SettingsRow(
                icon = Icons.Rounded.FileDownload,
                title = importDataTitle,
                subtitle = importStatus ?: stringResource(R.string.import_data_subtitle),
                onClick = onConfirmImport,
            )
        }
    }
}

@Composable
internal fun MiscellaneousSettingsGroup(
    search: SettingsSearch,
    swipeToPlayNext: Boolean,
    dontRepeatSuggestions: Boolean,
    stopOnTaskRemoved: Boolean,
    hideVolumeBar: Boolean,
    hideStatusBarInPlayer: Boolean,
    showQueueHistory: Boolean,
    hideSongStatus: Boolean,
) {
    SearchableSettingsGroup(search, header = stringResource(R.string.miscellaneous)) {
        val playNextOnSwipeTitle = stringResource(R.string.play_next_on_swipe)
        row(playNextOnSwipeTitle, "swipe", "queue") {
            SettingsRow(
                icon = Icons.Rounded.PlaylistPlay,
                title = playNextOnSwipeTitle,
                subtitle = if (swipeToPlayNext) {
                    stringResource(R.string.swipe_plays_next)
                } else {
                    stringResource(R.string.swipe_adds_to_queue)
                },
                trailing = {
                    Switch(
                        checked = swipeToPlayNext,
                        onCheckedChange = AppSettings::setSwipeToPlayNext,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedBorderColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                },
                onClick = { AppSettings.setSwipeToPlayNext(!swipeToPlayNext) },
            )
        }
        val dontRepeatSongsTitle = stringResource(R.string.dont_repeat_songs)
        row(dontRepeatSongsTitle, "radio", "suggestions") {
            SettingsRow(
                icon = Icons.Rounded.History,
                title = dontRepeatSongsTitle,
                subtitle = stringResource(R.string.dont_repeat_songs_subtitle),
                trailing = {
                    Switch(
                        checked = dontRepeatSuggestions,
                        onCheckedChange = AppSettings::setDontRepeatSuggestions,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedBorderColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                },
                onClick = { AppSettings.setDontRepeatSuggestions(!dontRepeatSuggestions) },
            )
        }
        val stopMusicOnCloseTitle = stringResource(R.string.stop_music_on_close)
        row(stopMusicOnCloseTitle, "notification", "background") {
            SettingsRow(
                icon = Icons.Rounded.MusicOff,
                title = stopMusicOnCloseTitle,
                subtitle = stringResource(R.string.stop_music_on_close_subtitle),
                trailing = {
                    Switch(
                        checked = stopOnTaskRemoved,
                        onCheckedChange = AppSettings::setStopOnTaskRemoved,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedBorderColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                },
                onClick = { AppSettings.setStopOnTaskRemoved(!stopOnTaskRemoved) },
            )
        }
        val hideVolumeBarTitle = stringResource(R.string.hide_volume_bar)
        row(hideVolumeBarTitle, "volume", "player") {
            SettingsRow(
                icon = Icons.Rounded.VolumeOff,
                title = hideVolumeBarTitle,
                subtitle = stringResource(R.string.hide_volume_bar_subtitle),
                trailing = {
                    Switch(
                        checked = hideVolumeBar,
                        onCheckedChange = AppSettings::setHideVolumeBar,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedBorderColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                },
                onClick = { AppSettings.setHideVolumeBar(!hideVolumeBar) },
            )
        }
        val hideStatusBarTitle = stringResource(R.string.hide_status_bar_in_player)
        row(hideStatusBarTitle, "status bar", "immersive", "fullscreen", "player") {
            SettingsRow(
                icon = Icons.Rounded.WebAssetOff,
                title = hideStatusBarTitle,
                subtitle = stringResource(R.string.hide_status_bar_in_player_subtitle),
                trailing = {
                    Switch(
                        checked = hideStatusBarInPlayer,
                        onCheckedChange = AppSettings::setHideStatusBarInPlayer,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedBorderColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                },
                onClick = { AppSettings.setHideStatusBarInPlayer(!hideStatusBarInPlayer) },
            )
        }
        val showQueueHistoryTitle = stringResource(R.string.show_queue_history)
        row(showQueueHistoryTitle, "player", "queue", "history", "previously played") {
            SettingsRow(
                icon = Icons.Rounded.History,
                title = showQueueHistoryTitle,
                subtitle = stringResource(R.string.show_queue_history_subtitle),
                trailing = {
                    Switch(
                        checked = showQueueHistory,
                        onCheckedChange = AppSettings::setShowQueueHistory,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedBorderColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                },
                onClick = { AppSettings.setShowQueueHistory(!showQueueHistory) },
            )
        }
        val hideSongStatusTitle = stringResource(R.string.hide_song_status)
        row(hideSongStatusTitle, "player", "playing from", "played by") {
            SettingsRow(
                icon = Icons.Rounded.VisibilityOff,
                title = hideSongStatusTitle,
                subtitle = stringResource(R.string.hide_song_status_subtitle),
                trailing = {
                    Switch(
                        checked = hideSongStatus,
                        onCheckedChange = AppSettings::setHideSongStatus,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedBorderColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                },
                onClick = { AppSettings.setHideSongStatus(!hideSongStatus) },
            )
        }
    }
}

@Composable
internal fun LanguageSettingsGroup(
    search: SettingsSearch,
    onAppLanguage: () -> Unit,
) {
    SearchableSettingsGroup(search, header = stringResource(R.string.language)) {
        val appLanguageTitle = stringResource(R.string.app_language)
        row(appLanguageTitle, "locale", "translate") {
            val selectedLanguage = AppCompatDelegate.getApplicationLocales().get(0)?.toLanguageTag()
                ?: Locale.getDefault().toLanguageTag()
            SettingsRow(
                icon = Icons.Rounded.Language,
                title = appLanguageTitle,
                subtitle = stringResource(languageDisplayNameRes(selectedLanguage)),
                onClick = onAppLanguage,
            )
        }
    }
}

@Composable
internal fun AdvancedSettingsGroup(
    search: SettingsSearch,
    smartVersionAlignment: Boolean,
    nerdStats: Boolean,
    context: Context,
) {
    SearchableSettingsGroup(search, header = stringResource(R.string.advanced_options)) {
        val smartAlignmentTitle = stringResource(R.string.smart_version_alignment)
        row(smartAlignmentTitle, "alignment", "sync", "waveform", "video", "audio", "skit", "intro") {
            SettingsRow(
                icon = Icons.Rounded.GraphicEq,
                title = smartAlignmentTitle,
                subtitle = stringResource(R.string.smart_version_alignment_subtitle),
                trailing = {
                    Switch(
                        checked = smartVersionAlignment,
                        onCheckedChange = AppSettings::setSmartVersionAlignment,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedBorderColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                },
                onClick = { AppSettings.setSmartVersionAlignment(!smartVersionAlignment) },
            )
        }
        val showNerdStatsTitle = stringResource(R.string.show_nerd_stats)
        row(showNerdStatsTitle, "debug", "bitrate", "codec") {
            SettingsRow(
                icon = Icons.Rounded.BarChart,
                title = showNerdStatsTitle,
                subtitle = stringResource(R.string.show_nerd_stats_subtitle),
                trailing = {
                    Switch(
                        checked = nerdStats,
                        onCheckedChange = AppSettings::setShowNerdStats,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedBorderColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                },
                onClick = { AppSettings.setShowNerdStats(!nerdStats) },
            )
        }
        val appIconTitle = stringResource(R.string.app_icon)
        row(appIconTitle, "icon", "launcher", "logo") {
            var selectedIcon by remember { mutableStateOf(AppIcon.current(context)) }
            SettingsRow(icon = Icons.Rounded.Apps, title = appIconTitle)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(start = ROW_INSET, end = ROW_INSET, bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AppIcon.entries.forEach { option ->
                    val selected = option == selectedIcon
                    Column(
                        modifier = Modifier
                            .width(84.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                selectedIcon = option
                                AppIcon.apply(context, option)
                            }
                            .padding(vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        val preview = option.previewRes
                        if (preview == null) {
                            AndroidView(
                                factory = { ctx ->
                                    android.widget.ImageView(ctx).apply { setImageResource(option.iconRes) }
                                },
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(RoundedCornerShape(22.dp)),
                            )
                        } else {
                            Image(
                                painter = painterResource(preview),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(68.dp)
                                    .scale(1.5f),
                            )
                        }
                        Text(
                            text = stringResource(option.labelRes),
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(top = 6.dp),
                        )
                    }
                }
            }
        }
    }
}
