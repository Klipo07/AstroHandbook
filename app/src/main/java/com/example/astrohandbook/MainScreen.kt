package com.example.astrohandbook

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val displayedNews by viewModel.displayedNews.collectAsState()
    var showBlackHole by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Астрономический справочник") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // PNG черная дыра на фоне
            if (showBlackHole) {
                BlackHolePng(modifier = Modifier.fillMaxSize())
            }

            // Контент поверх черной дыры
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Кнопка для тестирования
                Button(
                    onClick = { showBlackHole = !showBlackHole },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(if (showBlackHole) "🔘 Скрыть черную дыру (PNG)" else "🕳️ Показать черную дыру (PNG)")
                }

                // Тестовая кнопка смены новостей
                Button(
                    onClick = { viewModel.forceReplaceRandomNews() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text("🔄 Тест: Сменить новость сейчас")
                }

                // Первая строка новостей
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (displayedNews.size > 0) {
                        NewsCard(
                            newsItem = displayedNews[0],
                            onLikeClick = { viewModel.likeNews(0) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (displayedNews.size > 1) {
                        NewsCard(
                            newsItem = displayedNews[1],
                            onLikeClick = { viewModel.likeNews(1) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Вторая строка новостей
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (displayedNews.size > 2) {
                        NewsCard(
                            newsItem = displayedNews[2],
                            onLikeClick = { viewModel.likeNews(2) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (displayedNews.size > 3) {
                        NewsCard(
                            newsItem = displayedNews[3],
                            onLikeClick = { viewModel.likeNews(3) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}