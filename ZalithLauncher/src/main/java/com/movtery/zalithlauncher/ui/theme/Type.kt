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

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * KROX 字体族：使用平台默认字体。
 * res/font/ 下没有内置字体文件，因此不向仓库引入二进制字体。
 * 详见 docs/spec/tokens.md §5。
 */
private val KroxFontFamily = FontFamily.Default

private fun kroxStyle(
    size: Int,
    lineHeight: Int,
    weight: FontWeight = FontWeight.Normal
): TextStyle = TextStyle(
    fontFamily = KroxFontFamily,
    fontSize = size.sp,
    fontWeight = weight,
    lineHeight = lineHeight.sp
)

/** 28sp/600/36 —— 页面上最大的单块标题 */
val KroxDisplay = kroxStyle(28, 36, FontWeight.SemiBold)
/** 22sp/600/30 —— 一级标题 */
val KroxH1 = kroxStyle(22, 30, FontWeight.SemiBold)
/** 16sp/600/24 —— 二级标题 */
val KroxH2 = kroxStyle(16, 24, FontWeight.SemiBold)
/** 14sp/600/20 —— 三级标题 */
val KroxH3 = kroxStyle(14, 20, FontWeight.SemiBold)
/** 13sp/400/20 —— 正文 */
val KroxBody = kroxStyle(13, 20)
/** 13sp/600/20 —— 强调正文，与 KroxBody 同尺寸，仅字重不同 */
val KroxBodyStrong = kroxStyle(13, 20, FontWeight.SemiBold)
/** 12sp/400/16 —— 说明文字 */
val KroxCaption = kroxStyle(12, 16)
/** 11sp/500/14 —— 次级元信息 */
val KroxMeta = kroxStyle(11, 14, FontWeight.Medium)
/** 10sp/600/14 —— 栏目标题 */
val KroxOverline = kroxStyle(10, 14, FontWeight.SemiBold)
/** 12sp/400/18 —— 等宽内容（版本号、路径、日志） */
val KroxMono = kroxStyle(12, 18)

val AppTypography = Typography(
    displayMedium = KroxDisplay,
    headlineSmall = KroxH1,
    titleLarge = KroxH2,
    titleMedium = KroxH2,
    titleSmall = KroxH3,
    bodyLarge = KroxBody,
    bodyMedium = KroxBody,
    bodySmall = KroxCaption,
    labelLarge = KroxMono,
    labelMedium = KroxMeta,
    labelSmall = KroxOverline
)

/**
 * KROX 圆角：xs4 / sm6 / md8 / lg12 / xl16，映射到 Material3 的5个槽位。
 * MaterialTheme.shapes.* 的全部既有引用（201 处）自动获得 KROX 圆角。
 * largeIncreased / extraLargeIncreased / extraExtraLarge 无引用，保留 M3 默认值。
 */
val KroxShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(6.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(12.dp),
    extraLarge = RoundedCornerShape(16.dp)
)
