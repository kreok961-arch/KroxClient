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

package com.movtery.zalithlauncher.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.movtery.zalithlauncher.setting.enums.isLauncherInDarkTheme
import com.movtery.zalithlauncher.ui.components.influencedByBackgroundColor

/**
 * KROX 间距梯度（dp）。仅用于新增/重写的调用点，既有 dp 字面量不批量重写。
 * 详见 docs/spec/tokens.md §3。
 */
object KroxSpacing {
    val none: Dp = 0.dp
    val xs: Dp = 4.dp
    val sm: Dp = 8.dp
    val md: Dp = 12.dp
    val lg: Dp = 16.dp
    val xl: Dp = 20.dp
    val xxl: Dp = 24.dp
    val xxxl: Dp = 32.dp
    val huge: Dp = 40.dp
    val giant: Dp = 48.dp
    val max: Dp = 64.dp
}

/**
 * KROX 悬停覆盖色。§6.4 悬停态为 rgba(255,255,255,0.04)，
 * 独立于任何容器色，用于所有可交互表面的 hover 层。
 */
val KroxHoverOverlay: Color = Color(0x0AF9FAFB)

/**
 * 把容器色按 §9.2 提亮/压暗（悬停 +8% / 按下 -8%）。
 * 逐通道线性缩放，对不透明色即为亮度平移。
 */
fun Color.shift(amount: Float): Color = Color(
    red = (red + amount).coerceIn(0f, 1f),
    green = (green + amount).coerceIn(0f, 1f),
    blue = (blue + amount).coerceIn(0f, 1f),
    alpha = alpha
)

/** 应用整体背景的颜色 */
@Composable
fun backgroundColor(): Color = MaterialTheme.colorScheme.surfaceContainer
@Composable
fun onBackgroundColor(): Color = MaterialTheme.colorScheme.onSurfaceVariant

/**
 * 卡片背景颜色
 * [androidx.compose.material3.Card]
 * [com.movtery.zalithlauncher.ui.components.BackgroundCard]
 * [androidx.compose.ui.window.Dialog]
 * @param influencedByBackground 是否受背景内容影响，更改自身不透明度
 */
@Composable
fun cardColor(
    influencedByBackground: Boolean = true
): Color = influencedByBackgroundColor(
    color = MaterialTheme.colorScheme.surface,
    enabled = influencedByBackground
)
@Composable
fun onCardColor(): Color = MaterialTheme.colorScheme.onSurface
/**
 * 卡片顶部Title的背景颜色，半透明的surface
 */
@Composable
fun cardTitleColor(
    alpha: Float = 0.5f
): Color = MaterialTheme.colorScheme.surface.copy(alpha = alpha)

/**
 * 卡片上的Item的背景颜色
 * @param influencedByBackground 是否受背景内容影响，更改自身不透明度
 */
@Composable
fun itemColor(
    influencedByBackground: Boolean = true,
    isDark: Boolean = isLauncherInDarkTheme()
): Color {
    return influencedByBackgroundColor(
        color = if (isDark) {
            MaterialTheme.colorScheme.surfaceContainer
        } else {
            MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
        },
        enabled = influencedByBackground
    )
}
@Composable
fun onItemColor() = MaterialTheme.colorScheme.onSurface

/**
 * 按钮背景颜色
 */
@Composable
fun buttonColor(
    isPrimary: Boolean = true
): Color = if (isPrimary) Color(0xFFDC2626) else MaterialTheme.colorScheme.surfaceVariant

@Composable
fun onButtonColor(
    isPrimary: Boolean = true
): Color = if (isPrimary) Color(0xFFF9FAFB) else MaterialTheme.colorScheme.onSurface
