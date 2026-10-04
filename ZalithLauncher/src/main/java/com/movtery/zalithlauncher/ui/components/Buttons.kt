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

package com.movtery.zalithlauncher.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TooltipScope
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.movtery.zalithlauncher.ui.screens.content.elements.DisabledAlpha
import com.movtery.zalithlauncher.ui.theme.buttonColor
import com.movtery.zalithlauncher.ui.theme.onButtonColor
import com.movtery.zalithlauncher.ui.theme.shift
import com.movtery.zalithlauncher.utils.animation.KroxEaseOut
import com.movtery.zalithlauncher.utils.animation.KroxMotion
import kotlinx.coroutines.launch

@Composable
fun ScalingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = buttonColor(),
        contentColor = onButtonColor()
    ),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    // KROX 8.2/9.2：按下缩放 0.98，80ms 线性（KroxMotion.INSTANT）。
    // 不得使用 spring / bounce —— 见 docs/spec/master-spec.md §7.6。
    val scale = animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(durationMillis = KroxMotion.INSTANT, easing = KroxEaseOut),
        label = "ButtonScale"
    )
    // §9.2：悬停提亮约 8%，按下压暗约 8%。disabled 保持原色 —— DisabledAlpha 已是禁用的唯一信号。
    // 底色 120ms（KroxMotion.FAST）、缩放 80ms（KroxMotion.INSTANT）—— §9.2 的 120/80 分工。
    val containerColor by animateColorAsState(
        targetValue = when {
            !enabled -> colors.containerColor
            isPressed -> colors.containerColor.shift(-0.08f)
            isHovered -> colors.containerColor.shift(0.08f)
            else -> colors.containerColor
        },
        animationSpec = tween(durationMillis = KroxMotion.FAST, easing = KroxEaseOut),
        label = "ButtonContainer"
    )

    Button(
        onClick = onClick,
        modifier = modifier.graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
        },
        enabled = enabled,
        shape = shape,
        colors = colors.copy(containerColor = containerColor),
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content
    )
}

@Composable
fun MarqueeText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    textAlign: TextAlign? = null
) {
    Text(
        modifier = modifier.basicMarquee(Int.MAX_VALUE),
        text = text,
        color = color,
        style = style,
        maxLines = 1,
        textAlign = textAlign
    )
}

@Composable
fun IconTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.large,
    iconSize: Dp = 24.dp,
    painter: Painter,
    text: String,
    contentDescription: String? = text,
    style: TextStyle = MaterialTheme.typography.labelMedium,
    enabled: Boolean = true
) {
    BaseIconTextButton(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        icon = { modifier1 ->
            Icon(
                modifier = modifier1.size(iconSize),
                painter = painter,
                contentDescription = contentDescription
            )
        },
        text = text,
        style = style,
        enabled = enabled
    )
}

@Composable
fun IconTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.large,
    iconSize: Dp = 24.dp,
    imageVector: ImageVector,
    text: String,
    contentDescription: String? = text,
    style: TextStyle = MaterialTheme.typography.labelMedium,
    enabled: Boolean = true,
) {
    BaseIconTextButton(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        icon = { modifier1 ->
            Icon(
                modifier = modifier1.size(iconSize),
                imageVector = imageVector,
                contentDescription = contentDescription
            )
        },
        text = text,
        style = style,
        enabled = enabled
    )
}

@Composable
fun BaseIconTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.large,
    icon: @Composable (Modifier) -> Unit,
    text: String,
    style: TextStyle = MaterialTheme.typography.labelMedium,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier
            .clip(shape = shape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(PaddingValues(horizontal = 8.dp, vertical = 4.dp))
            .alpha(if (enabled) 1f else DisabledAlpha),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        icon(Modifier.align(Alignment.CenterVertically))
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(end = 4.dp),
            text = text,
            style = style
        )
    }
}

@Composable
fun TouchableButton(
    modifier: Modifier = Modifier,
    onTouch: (isPressed: Boolean) -> Unit = {},
    text: String
) {
    Surface(
        shape = CircleShape,
        color = buttonColor(),
        contentColor = onButtonColor(),
        modifier = modifier
            .semantics { role = Role.Button }
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    onTouch(true)

                    do {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id }
                    } while (change != null && change.pressed)

                    onTouch(false)
                }
            }
    ) {
        val mergedStyle = LocalTextStyle.current.merge(MaterialTheme.typography.labelLarge)
        CompositionLocalProvider(
            LocalContentColor provides ButtonDefaults.buttonColors().contentColor,
            LocalTextStyle provides mergedStyle,
        ) {
            Row(
                Modifier.defaultMinSize(
                    minWidth = ButtonDefaults.MinWidth,
                    minHeight = ButtonDefaults.MinHeight
                )
                    .padding(ButtonDefaults.ContentPadding),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = text)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TooltipIconButton(
    modifier: Modifier = Modifier,
    tooltipTitle: String,
    tooltipMessage: String,
    content: @Composable () -> Unit
) {
    TooltipIconButton(
        modifier = modifier,
        tooltip = {
            RichTooltip(
                modifier = Modifier.padding(all = 3.dp),
                title = { Text(text = tooltipTitle) },
                shadowElevation = 3.dp
            ) {
                Text(text = tooltipMessage)
            }
        },
        content = content
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TooltipIconButton(
    modifier: Modifier = Modifier,
    tooltip: @Composable (TooltipScope.() -> Unit),
    content: @Composable () -> Unit
) {
    val tooltipState = rememberTooltipState(isPersistent = true)
    val coroutineScope = rememberCoroutineScope()

    TooltipBox(
        modifier = modifier,
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
        tooltip = tooltip,
        state = tooltipState,
        enableUserInput = false
    ) {
        IconButton(
            onClick = {
                coroutineScope.launch {
                    if (tooltipState.isVisible) {
                        tooltipState.dismiss()
                    } else {
                        tooltipState.show()
                    }
                }
            }
        ) {
            content()
        }
    }
}