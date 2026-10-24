package com.hamraj37.laya.ui.screens

import android.media.AudioFormat
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.SignalCellularAlt
import androidx.compose.material.icons.rounded.SmartDisplay
import androidx.compose.material.icons.rounded.SurroundSound
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Waves
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hamraj37.laya.R
import com.hamraj37.laya.data.settings.AppSettings
import com.hamraj37.laya.data.settings.AudioQuality
import com.hamraj37.laya.data.settings.AutomixPerformanceMode
import com.hamraj37.laya.data.settings.OutputPcmMode
import com.hamraj37.laya.playback.AudioOutputStatus
import kotlin.math.roundToInt

@Composable
internal fun AudioQualitySettingsGroup(
    search: SettingsSearch,
    metered: Boolean?,
    wifiQuality: AudioQuality,
    cellularQuality: AudioQuality,
    dolbyAtmos: Boolean,
    dolbyAtmosSupported: Boolean,
    onSources: () -> Unit,
    onPickQuality: (QualityTarget) -> Unit,
) {
    SearchableSettingsGroup(search, header = stringResource(R.string.audio_quality)) {
        val sourceTitle = stringResource(R.string.source)
        val sourceSubtitle = stringResource(R.string.sources_subtitle)
        row(sourceTitle, sourceSubtitle, "addon", "lossless") {
            SettingsRow(
                icon = Icons.Rounded.Extension,
                title = sourceTitle,
                subtitle = sourceSubtitle,
                onClick = onSources,
            )
        }
        val onWifiTitle = stringResource(R.string.on_wifi)
        row(onWifiTitle, "wi-fi", "streaming quality") {
            SettingsRow(
                icon = Icons.Rounded.Wifi,
                title = onWifiTitle,
                badge = stringResource(R.string.in_use).takeIf { metered == false },
                value = wifiQuality.localizedLabel(),
                onClick = { onPickQuality(QualityTarget.WIFI) },
            )
        }
        val onMobileDataTitle = stringResource(R.string.on_mobile_data)
        row(onMobileDataTitle, "cellular", "streaming quality") {
            SettingsRow(
                icon = Icons.Rounded.SignalCellularAlt,
                title = onMobileDataTitle,
                badge = stringResource(R.string.in_use).takeIf { metered == true },
                value = cellularQuality.localizedLabel(),
                onClick = { onPickQuality(QualityTarget.CELLULAR) },
            )
        }
        val dolbyAtmosTitle = stringResource(R.string.dolby_atmos)
        row(dolbyAtmosTitle, "surround", "e-ac-3") {
            SettingsRow(
                iconPainter = painterResource(R.drawable.ic_dolby_atmos),
                title = dolbyAtmosTitle,
                subtitle = stringResource(
                    if (dolbyAtmosSupported) {
                        R.string.dolby_atmos_subtitle
                    } else {
                        R.string.dolby_atmos_unavailable
                    },
                ),
                enabled = dolbyAtmosSupported,
                trailing = {
                    Switch(
                        checked = dolbyAtmos && dolbyAtmosSupported,
                        onCheckedChange = AppSettings::setDolbyAtmos,
                        enabled = dolbyAtmosSupported,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedBorderColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                },
                onClick = { AppSettings.setDolbyAtmos(!dolbyAtmos) },
            )
        }
    }
}

@Composable
internal fun PlaybackSettingsGroup(
    search: SettingsSearch,
    preferMusicOnly: Boolean,
    outputStatus: AudioOutputStatus.Snapshot,
    outputPcmMode: OutputPcmMode,
    preferUsbDac: Boolean,
    loudnessNormalization: Boolean,
    loudnessOffOnSpeaker: Boolean,
    crossfade: Int,
    smartFade: Boolean,
    automixPerformance: AutomixPerformanceMode,
    skipSilence: Boolean,
    spatialAudio: Boolean,
    playingDolbyAtmos: Boolean,
    onEqualizer: () -> Unit,
    onPickAutomixPerformance: () -> Unit,
) {
    SearchableSettingsGroup(search, header = stringResource(R.string.playback)) {
        val preferMusicOnlyTitle = stringResource(R.string.prefer_music_only)
        row(preferMusicOnlyTitle, "video", "audio", "music video") {
            SettingsRow(
                icon = Icons.Rounded.SmartDisplay,
                title = preferMusicOnlyTitle,
                subtitle = stringResource(R.string.prefer_music_only_subtitle),
                trailing = {
                    Switch(
                        checked = preferMusicOnly,
                        onCheckedChange = AppSettings::setPreferMusicOnly,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedBorderColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                },
                onClick = { AppSettings.setPreferMusicOnly(!preferMusicOnly) },
            )
        }
        val outputPrecisionTitle = stringResource(R.string.output_precision)
        row(outputPrecisionTitle, "pcm", "bit depth", "sample rate", "dac") {
            SettingsRow(
                icon = Icons.Rounded.GraphicEq,
                title = outputPrecisionTitle,
                subtitle = buildString {
                    append(outputStatus.sink)
                    append(" · ")
                    append(outputStatus.deviceName)
                    (outputStatus.actualSampleRateHz ?: outputStatus.sampleRatesHz.firstOrNull())
                        ?.let { append(" · ${it / 1000.0} kHz") }
                    append(" · ")
                    append(AudioOutputStatus.encodingLabel(outputStatus))
                },
            )
            SegmentedControl(
                options = OutputPcmMode.entries.map(OutputPcmMode::label),
                selectedIndex = OutputPcmMode.entries.indexOf(outputPcmMode),
                onSelect = { AppSettings.setOutputPcmMode(OutputPcmMode.entries[it]) },
                modifier = Modifier.padding(start = TEXT_INSET, end = ROW_INSET, bottom = 14.dp),
            )
        }
        val preferUsbDacTitle = stringResource(R.string.prefer_usb_dac)
        row(preferUsbDacTitle, "headphone", "output") {
            SettingsSubRow(
                title = preferUsbDacTitle,
                checked = preferUsbDac,
                onCheckedChange = AppSettings::setPreferUsbDac,
                badge = stringResource(R.string.connected).takeIf { outputStatus.isUsb },
            )
        }
        val loudnessTitle = stringResource(R.string.loudness_normalization)
        row(loudnessTitle, "loudness", "volume", "normalize", "replaygain", "lufs") {
            SettingsRow(
                icon = Icons.Rounded.VolumeUp,
                title = loudnessTitle,
                subtitle = stringResource(R.string.loudness_normalization_subtitle),
                trailing = {
                    Switch(
                        checked = loudnessNormalization,
                        onCheckedChange = AppSettings::setLoudnessNormalization,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedBorderColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                },
                onClick = { AppSettings.setLoudnessNormalization(!loudnessNormalization) },
            )
        }
        val loudnessSpeakerTitle = stringResource(R.string.loudness_off_on_speaker)
        row(loudnessSpeakerTitle, "loudness", "speaker", "normalize") {
            SettingsSubRow(
                title = loudnessSpeakerTitle,
                checked = loudnessOffOnSpeaker,
                onCheckedChange = AppSettings::setLoudnessOffOnSpeaker,
            )
        }
        if (!smartFade) {
            val crossfadeTitle = stringResource(R.string.crossfade)
            row(crossfadeTitle, "fade", "gapless") {
                SliderRow(
                    icon = Icons.Rounded.Waves,
                    title = crossfadeTitle,
                    subtitle = stringResource(R.string.crossfade_subtitle),
                    value = if (crossfade == 0) stringResource(R.string.off) else "${crossfade}s",
                    sliderValue = crossfade.toFloat(),
                    onSliderValue = { AppSettings.setCrossfadeSeconds(it.roundToInt()) },
                    valueRange = 0f..12f,
                    steps = 11,
                )
            }
        }
        val automixTitle = stringResource(R.string.automix)
        row(automixTitle, "crossfade", "smart fade", "mix") {
            SettingsRow(
                icon = Icons.Rounded.AutoAwesome,
                title = automixTitle,
                subtitle = if (smartFade) {
                    stringResource(R.string.automix_enabled_subtitle)
                } else {
                    stringResource(R.string.automix_disabled_subtitle)
                },
                trailing = {
                    Switch(
                        checked = smartFade,
                        onCheckedChange = AppSettings::setSmartFadeEnabled,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedBorderColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                },
                onClick = { AppSettings.setSmartFadeEnabled(!smartFade) },
            )
        }
        val automixPerformanceTitle = stringResource(R.string.automix_performance)
        row(automixPerformanceTitle, "cpu", "battery") {
            SettingsRow(
                icon = Icons.Rounded.Tune,
                title = automixPerformanceTitle,
                subtitle = stringResource(R.string.automix_performance_subtitle),
                value = automixPerformance.localizedLabel(),
                onClick = onPickAutomixPerformance,
            )
        }
        val skipSilenceTitle = stringResource(R.string.skip_silence)
        row(skipSilenceTitle, "silence") {
            SettingsRow(
                icon = Icons.AutoMirrored.Rounded.VolumeOff,
                title = skipSilenceTitle,
                subtitle = if (outputStatus.actualEncoding == AudioFormat.ENCODING_PCM_FLOAT) {
                    stringResource(R.string.skip_silence_float_subtitle)
                } else {
                    stringResource(R.string.skip_silence_subtitle)
                },
                trailing = {
                    Switch(
                        checked = skipSilence,
                        onCheckedChange = AppSettings::setSkipSilence,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedBorderColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                },
                onClick = { AppSettings.setSkipSilence(!skipSilence) },
            )
        }
        val spatialAudioTitle = stringResource(R.string.spatial_audio)
        row(spatialAudioTitle, "surround", "3d") {
            SettingsRow(
                icon = Icons.Rounded.SurroundSound,
                title = spatialAudioTitle,
                subtitle = if (playingDolbyAtmos) {
                    stringResource(R.string.spatial_audio_atmos_subtitle)
                } else {
                    stringResource(R.string.spatial_audio_subtitle)
                },
                trailing = {
                    Switch(
                        checked = spatialAudio,
                        onCheckedChange = AppSettings::setSpatialAudio,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedBorderColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                },
                onClick = { AppSettings.setSpatialAudio(!spatialAudio) },
            )
        }
        val equalizerTitle = stringResource(R.string.equalizer)
        row(equalizerTitle, "eq", "bass", "treble") {
            SettingsRow(
                icon = Icons.Rounded.GraphicEq,
                title = equalizerTitle,
                subtitle = stringResource(R.string.equalizer_subtitle),
                onClick = onEqualizer,
            )
        }
    }
}
