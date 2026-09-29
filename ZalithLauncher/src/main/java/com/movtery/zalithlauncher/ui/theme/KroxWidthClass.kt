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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * KROX 断点（master-spec.md §11.1）。宽度单位 dp。
 *
 * 断点**不改变页面构成**。Home 的 7f/3f 分割、文件管理器侧边栏、设置导航轨在任何宽度下
 * 保持不变（§11.2：绝不隐藏既有导航）。断点只决定一件事：外边距（gutter）。
 *
 * §11.1 的 960dp 内容封顶**不实现**：`BaseScreen` 是 52 个页面的根 `fillMaxSize()` 容器，
 * 在那里封顶会缩小每个页面的滚动/点击区域并在两侧留出背景带。改 gutter 已满足 §11.1 的
 * 「generous side margins」，若日后确需封顶，加在具体页面的根 Column 上而非 `BaseScreen`。
 *
 * 详见 docs/spec/master-spec.md §10.3、§11.1、§11.2 与 docs/audit/RESPONSIVE.md。
 */
enum class KroxWidthClass {
    /** < 600dp — 全出血单列，即当前默认形态 */
    Compact,

    /** 600–900dp — 同一构成，外边距仍为 space-6（见 [gutter]） */
    Medium,

    /** 900–1200dp — 同一构成，外边距 space-8（32dp） */
    Expanded,

    /** > 1200dp — 同一构成，外边距 space-10（40dp） */
    Large;

    /**
     * 页面外边距：断点只加宽 gutter，不改构成（§10.3）。
     *
     * Compact 与 Medium 都是 space-6（24dp）——这是当前默认形态，必须逐像素不变，
     * 否则 §2.2 的保留契约与 QA-02「Home 现有行为不变」会失败。只有 ≥900dp 才加宽。
     */
    val gutter: Dp
        get() = when (this) {
            Compact, Medium -> KroxSpacing.xxl
            Expanded -> KroxSpacing.xxxl
            Large -> KroxSpacing.huge
        }

    companion object {
        /** 由 dp 宽度推导断点。纯函数，便于在无 Composable 环境下断言。 */
        fun of(widthDp: Int): KroxWidthClass = when {
            widthDp < 600 -> Compact
            widthDp < 900 -> Medium
            widthDp < 1200 -> Expanded
            else -> Large
        }
    }
}

/**
 * 当前断点。默认 [KroxWidthClass.Compact]，因此在主题之外组合的对话框与预览不会崩溃。
 * 由 [com.movtery.zalithlauncher.ui.theme.KroxTheme] 在根部提供。
 */
val LocalKroxWidthClass = compositionLocalOf { KroxWidthClass.Compact }

/**
 * 读取当前断点。`LocalConfiguration.screenWidthDp` 自 API 13 起可用，
 * 而 minSdk 为 26，因此不需要版本判断；也不需要 `androidx.window`（§14.2）。
 */
@Composable
fun rememberKroxWidthClass(): KroxWidthClass =
    KroxWidthClass.of(LocalConfiguration.current.screenWidthDp)

/** 当前断点的页面外边距。替代原先写死的外边距字面量。 */
@Composable
fun kroxGutter(): Dp = LocalKroxWidthClass.current.gutter
