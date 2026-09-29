/*
 * Zalith Launcher 2
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

package com.movtery.zalithlauncher.ui.screens.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.game.version.installed.PlayTimeRepository
import com.movtery.zalithlauncher.game.version.installed.VersionsManager
import com.movtery.zalithlauncher.setting.AllSettings
import com.movtery.zalithlauncher.ui.AndroidStringText
import com.movtery.zalithlauncher.ui.androidText
import com.movtery.zalithlauncher.ui.base.BaseScreen
import com.movtery.zalithlauncher.ui.components.BackgroundCard
import com.movtery.zalithlauncher.ui.components.PageHeader
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.theme.KroxDisplay
import com.movtery.zalithlauncher.ui.theme.KroxSpacing
import com.movtery.zalithlauncher.ui.theme.kroxGutter
import com.movtery.zalithlauncher.utils.PlayTimeUtils
import com.movtery.zalithlauncher.viewmodel.ScreenBackStackViewModel

@Composable
fun PlayTimeStatsScreen(
    backStackViewModel: ScreenBackStackViewModel
) {
    BaseScreen(
        screenKey = NormalNavKey.PlayTimeStats,
        currentKey = backStackViewModel.mainScreen.currentKey
    ) {
        val versions = remember { VersionsManager.versions.value }
        val versionNames = remember(versions) { versions.map { it.getVersionName() } }

        val todayMs = remember(versionNames) {
            PlayTimeRepository.getDailyTotalPlayTime(PlayTimeRepository.today(), versionNames)
        }
        val monthMs = remember(versionNames) {
            PlayTimeRepository.getLastNDaysTotal(versionNames, 30)
        }
        val allTimeMs = remember {
            AllSettings.playTime.getValue()
        }
        val mostPlayed = remember(versionNames) {
            PlayTimeRepository.getMostPlayedVersion(versionNames)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                // §11.1：外边距由断点决定，< 900dp 仍是 space-6（24dp）
                .padding(kroxGutter()),
            verticalArrangement = Arrangement.spacedBy(KroxSpacing.md)
        ) {
            PageHeader(
                title = NormalNavKey.PlayTimeStats.title!!
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(KroxSpacing.md)
            ) {
                StatCard(
                    label = stringResource(R.string.stats_today),
                    timeMs = todayMs,
                    iconRes = R.drawable.ic_schedule_outlined,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                StatCard(
                    label = stringResource(R.string.stats_month),
                    timeMs = monthMs,
                    iconRes = R.drawable.ic_dashboard_outlined,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(KroxSpacing.md)
            ) {
                StatCard(
                    label = stringResource(R.string.stats_all_time),
                    timeMs = allTimeMs,
                    iconRes = R.drawable.ic_assignment_filled,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                StatCard(
                    label = stringResource(R.string.stats_most_played),
                    timeMs = mostPlayed?.second ?: 0L,
                    subtitle = mostPlayed?.first,
                    iconRes = R.drawable.ic_sports_esports_filled,
                    // D-08：GameStats 的唯一入口。复用既有卡片，不新建统计中枢页（§1.2）。
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable {
                            backStackViewModel.mainScreen.navigateTo(
                                screenKey = NormalNavKey.GameStats
                            )
                        }
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    timeMs: Long,
    iconRes: Int,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    val hours = PlayTimeUtils.getPlayHours(timeMs)
    val formattedTime = "%.1f h".format(hours)

    BackgroundCard(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(KroxSpacing.lg),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(KroxSpacing.sm))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.alpha(0.7f)
            )
            Spacer(Modifier.height(KroxSpacing.xs))
            Text(
                text = formattedTime,
                style = KroxDisplay
            )
            if (subtitle != null) {
                Spacer(Modifier.height(KroxSpacing.xs))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.alpha(0.6f)
                )
            }
        }
    }
}
