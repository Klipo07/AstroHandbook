package com.example.astrohandbook

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun BlackHolePng(modifier: Modifier = Modifier) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidthPx = screenWidth.value
    val screenHeightPx = screenHeight.value

    val infiniteTransition = rememberInfiniteTransition()

    // Анимация движения по диагонали
    val offsetX by infiniteTransition.animateFloat(
        initialValue = -200f,
        targetValue = screenWidthPx + 200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val offsetY by infiniteTransition.animateFloat(
        initialValue = -100f,
        targetValue = screenHeightPx + 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Пульсация размера
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Вращение
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(modifier = modifier) {
        Image(
            painter = painterResource(id = R.drawable.black_hole),
            contentDescription = "Black Hole",
            modifier = Modifier
                .offset(
                    x = (offsetX - 100).dp,
                    y = (offsetY - 100).dp
                )
                .size(200.dp)
                .scale(scale)
                .rotate(rotation),
            contentScale = ContentScale.Fit,
            alpha = 0.9f
        )
    }
}