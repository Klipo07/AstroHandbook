package com.example.astrohandbook

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NewsCard(
    newsItem: NewsItem,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(true) }
    isVisible = true

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(500)) +
                slideInVertically(initialOffsetY = { it / 4 }),
        exit = fadeOut(animationSpec = tween(300))
    ) {
        Card(
            modifier = modifier
                .padding(4.dp)
                .fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f) // 70% непрозрачности
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Верхняя часть - 90% высоты для текста новости
                Box(
                    modifier = Modifier
                        .weight(9f)
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = newsItem.title,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White, // Белый текст для контраста
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Нижняя часть - 10% высоты для лайков
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        .clickable { onLikeClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "❤️ ${newsItem.likes}",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}