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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.game.version.installed.PlayTimeRepository
import com.movtery.zalithlauncher.game.version.installed.VersionsManager
import com.movtery.zalithlauncher.ui.base.BaseScreen
import com.movtery.zalithlauncher.ui.components.BackgroundCard
import com.movtery.zalithlauncher.ui.components.PageHeader
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.screens.content.elements.VersionIconImage
import com.movtery.zalithlauncher.ui.theme.KroxSpacing
import com.movtery.zalithlauncher.ui.theme.kroxGutter
import com.movtery.zalithlauncher.utils.PlayTimeUtils
import com.movtery.zalithlauncher.viewmodel.ScreenBackStackViewModel

@Composable
fun GameStatsScreen(
    backStackViewModel: ScreenBackStackViewModel
) {
    BaseScreen(
        screenKey = NormalNavKey.GameStats,
        currentKey = backStackViewModel.mainScreen.currentKey
    ) {
        val context = LocalContext.current
        val versions = remember { VersionsManager.versions.value }

        data class VersionStat(val name: String, val version: com.movtery.zalithlauncher.game.version.installed.Version, val totalMs: Long)

        val stats = remember(versions) {
            versions
                .map { v -> VersionStat(v.getVersionName(), v, PlayTimeRepository.getTotalPlayTime(v.getVersionName())) }
                .sortedByDescending { it.totalMs }
        }
        val maxMs = stats.firstOrNull()?.totalMs?.takeIf { it > 0 } ?: 1L

        BackgroundCard(
            modifier = Modifier
                .fillMaxSize()
                // §11.1：外边距由断点决定，< 900dp 仍是 space-6（24dp）
                .padding(kroxGutter()),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(KroxSpacing.lg)
            ) {
                PageHeader(
                    // 不设 overline：本页没有比标题更上层的类别，重复标题等于同一行字出现两次。
                    // 与同级的 PlayTimeStatsScreen 保持一致。
                    title = NormalNavKey.GameStats.title!!
                )

                if (stats.all { it.totalMs == 0L }) {
                    // §8.15 空状态：图标 + 标题 + 说明。与文件管理器的空文件夹同一套模式。
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            // §11.1：限制文字宽度，说明文案窄屏换行而不是被裁掉
                            modifier = Modifier.widthIn(max = 320.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(KroxSpacing.sm)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_dashboard_outlined),
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                            )
                            Text(
                                text = stringResource(R.string.stats_no_data),
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = stringResource(R.string.stats_no_data_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = KroxSpacing.lg, vertical = KroxSpacing.sm),
                        verticalArrangement = Arrangement.spacedBy(KroxSpacing.md)
                    ) {
                        itemsIndexed(stats, key = { _, s -> s.name }) { _, stat ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(KroxSpacing.md)
                            ) {
                                VersionIconImage(
                                    version = stat.version,
                                    modifier = Modifier.size(32.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = stat.name,
                                            style = MaterialTheme.typography.labelMedium,
                                            maxLines = 1,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(modifier = Modifier.width(KroxSpacing.sm))
                                        Text(
                                            text = PlayTimeUtils.formatPlayTime(context, stat.totalMs),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(KroxSpacing.xs))
                                    LinearProgressIndicator(
                                        progress = { stat.totalMs.toFloat() / maxMs },
                                        modifier = Modifier.fillMaxWidth(),
                                        color = MaterialTheme.colorScheme.primary
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
