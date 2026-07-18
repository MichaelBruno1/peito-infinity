package com.example.peitoinfinity.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

@Composable
fun <T : Number> AnimatedCounter(
    targetValue: T,
    modifier: Modifier = Modifier,
    durationMillis: Int = 1000,
    style: TextStyle = LocalTextStyle.current,
    format: (Float) -> String
) {
    val animatable = remember { Animatable(0f) }

    LaunchedEffect(targetValue) {
        animatable.animateTo(
            targetValue = targetValue.toFloat(),
            animationSpec = tween(durationMillis, easing = FastOutSlowInEasing)
        )
    }

    Text(
        text = format(animatable.value),
        modifier = modifier,
        style = style
    )
}
