package com.hamraj37.laya.ui.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cached
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import coil3.SingletonImageLoader
import com.hamraj37.laya.R
import com.hamraj37.laya.data.LocalMediaRepository
import com.hamraj37.laya.data.settings.AppSettings
import com.hamraj37.laya.data.settings.DownloadQuality
import kotlin.math.roundToInt

@Composable
internal fun DownloadsSettingsGroup(
    search: SettingsSearch,
    downloadQuality: DownloadQuality,
    wifiOnlyDownloads: Boolean,
    exportDownloads: Boolean,
    metered: Boolean?,
    onPickDownloadQuality: () -> Unit,
) {
    SearchableSettingsGroup(search, header = stringResource(R.string.downloads)) {
        val downloadQualityTitle = stringResource(R.string.download_quality)
        row(downloadQualityTitle, "offline", "lossless") {
            SettingsRow(
                icon = Icons.Rounded.Download,
                title = downloadQualityTitle,
                subtitle = stringResource(R.string.download_quality_subtitle, downloadQuality.perTrack),
                value = downloadQuality.localizedLabel(),
                onClick = onPickDownloadQuality,
            )
        }
        val downloadWifiOnlyTitle = stringResource(R.string.download_wifi_only)
        row(downloadWifiOnlyTitle, "wi-fi", "cellular", divided = false) {
            SettingsSubRow(
                title = downloadWifiOnlyTitle,
                checked = wifiOnlyDownloads,
                onCheckedChange = AppSettings::setWifiOnlyDownloads,
                badge = stringResource(R.string.blocking).takeIf { wifiOnlyDownloads && metered == true },
            )
        }
        val exportDownloadsTitle = stringResource(R.string.export_compatible_downloads)
        row(exportDownloadsTitle, "music folder", divided = false) {
            SettingsSubRow(
                title = exportDownloadsTitle,
                checked = exportDownloads,
                onCheckedChange = AppSettings::setExportDownloads,
                subtitle = "Music/Laya".takeIf { exportDownloads },
            )
        }
    }
}

@Composable
internal fun LocalMusicSettingsGroup(
    search: SettingsSearch,
    localMusicFolderUri: String,
    filterNonMusicAudio: Boolean,
    localMusicFolderPicker: ActivityResultLauncher<Uri?>,
) {
    SearchableSettingsGroup(search, header = stringResource(R.string.local_music)) {
        val localMusicFolderTitle = stringResource(R.string.local_music_folder)
        row(localMusicFolderTitle, "folder", "offline", "files") {
            SettingsRow(
                icon = Icons.Rounded.Folder,
                title = localMusicFolderTitle,
                subtitle = LocalMediaRepository.selectedFolderLabel(localMusicFolderUri)
                    ?: stringResource(R.string.all_audio_folders),
                onClick = { localMusicFolderPicker.launch(null) },
            )
        }
        if (localMusicFolderUri.isNotBlank()) {
            val useAllAudioFoldersTitle = stringResource(R.string.use_all_audio_folders)
            row(useAllAudioFoldersTitle, "folder") {
                SettingsRow(
                    icon = Icons.Rounded.LibraryMusic,
                    title = useAllAudioFoldersTitle,
                    subtitle = stringResource(R.string.use_all_audio_folders_subtitle),
                    onClick = { AppSettings.setLocalMusicFolderUri("") },
                )
            }
        }
        val filterNonMusicAudioTitle = stringResource(R.string.filter_non_music_audio)
        row(filterNonMusicAudioTitle, "podcast", "recording") {
            SettingsRow(
                icon = Icons.Rounded.FilterAlt,
                title = filterNonMusicAudioTitle,
                subtitle = stringResource(R.string.filter_non_music_audio_subtitle),
                trailing = {
                    Switch(
                        checked = filterNonMusicAudio,
                        onCheckedChange = AppSettings::setFilterNonMusicAudio,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedBorderColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                },
                onClick = { AppSettings.setFilterNonMusicAudio(!filterNonMusicAudio) },
            )
        }
    }
}

@Composable
internal fun StorageSettingsGroup(
    search: SettingsSearch,
    cacheLimitBytes: Long,
    showCacheFolder: Boolean,
    context: Context,
    onConfirmClearSongCache: () -> Unit,
) {
    val cacheUnlimited = cacheLimitBytes == AppSettings.UNLIMITED_CACHE_LIMIT_BYTES
    val cacheLimitMb = if (cacheUnlimited) CACHE_UNLIMITED_STOP_MB else (cacheLimitBytes / (1024 * 1024)).toInt()
    val unlimitedLabel = stringResource(R.string.song_cache_unlimited)
    val cacheLimitLabel = if (cacheUnlimited) unlimitedLabel else formatCacheSize(cacheLimitMb)
    SearchableSettingsGroup(search, header = stringResource(R.string.storage)) {
        val showCacheFolderTitle = stringResource(R.string.show_cache_folder)
        row(showCacheFolderTitle, "cache", "cached songs", "library", "folder") {
            SettingsRow(
                icon = Icons.Rounded.Cached,
                title = showCacheFolderTitle,
                subtitle = stringResource(R.string.show_cache_folder_subtitle),
                trailing = {
                    Switch(
                        checked = showCacheFolder,
                        onCheckedChange = AppSettings::setShowCacheFolder,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedBorderColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                },
                onClick = { AppSettings.setShowCacheFolder(!showCacheFolder) },
            )
        }
        val songCacheLimitTitle = stringResource(R.string.song_cache_limit)
        row(songCacheLimitTitle, "cache", "space", "unlimited") {
            SliderRow(
                icon = Icons.Rounded.Storage,
                title = songCacheLimitTitle,
                subtitle = when {
                    cacheUnlimited -> stringResource(R.string.song_cache_unlimited_subtitle)
                    cacheLimitMb > CACHE_WARNING_MB ->
                        stringResource(R.string.song_cache_large_subtitle, cacheLimitLabel)
                    else -> stringResource(R.string.song_cache_limit_subtitle)
                },
                value = cacheLimitLabel,
                sliderValue = cacheLimitMb.toFloat(),
                onSliderValue = {
                    AppSettings.setAudioCacheLimitBytes(it.roundToInt().toLong() * 1024 * 1024)
                },
                valueRange = (AppSettings.DEFAULT_CACHE_LIMIT_BYTES / (1024 * 1024)).toFloat()..
                    CACHE_UNLIMITED_STOP_MB.toFloat(),
                steps = 19,
            )
        }
        val clearSongCacheTitle = stringResource(R.string.clear_song_cache)
        row(clearSongCacheTitle, "cache", "free space") {
            SettingsRow(
                icon = Icons.Rounded.DeleteSweep,
                title = clearSongCacheTitle,
                subtitle = stringResource(R.string.clear_song_cache_subtitle),
                onClick = onConfirmClearSongCache,
            )
        }
        val clearImageCacheTitle = stringResource(R.string.clear_image_cache)
        row(clearImageCacheTitle, "cache", "artwork", "free space") {
            SettingsRow(
                icon = Icons.Rounded.DeleteSweep,
                title = clearImageCacheTitle,
                subtitle = stringResource(R.string.clear_image_cache_subtitle),
                onClick = {
                    val loader = SingletonImageLoader.get(context)
                    loader.memoryCache?.clear()
                    loader.diskCache?.clear()
                    Toast.makeText(context, context.getString(R.string.image_cache_cleared), Toast.LENGTH_SHORT).show()
                },
            )
        }
    }
}
