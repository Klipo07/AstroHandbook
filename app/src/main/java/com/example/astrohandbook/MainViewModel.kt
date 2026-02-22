package com.example.astrohandbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _displayedNews = MutableStateFlow(
        NewsRepository.newsList.take(4).map { it.copy() }
    )
    val displayedNews: StateFlow<List<NewsItem>> = _displayedNews.asStateFlow()

    init {
        // Запускаем таймер при создании ViewModel
        startNewsRotation()
    }

    private fun startNewsRotation() {
        viewModelScope.launch {
            while (true) {
                // Ждем 5 секунд
                delay(5000) // 5000 миллисекунд = 5 секунд

                // Меняем одну случайную новость
                replaceRandomNews()
            }
        }
    }

    private fun replaceRandomNews() {
        val currentList = _displayedNews.value
        if (currentList.isEmpty()) return

        // Выбираем случайный индекс для замены (0..3)
        val indexToReplace = (0 until currentList.size).random()

        // Получаем случайную новость из общего списка
        val randomNews = NewsRepository.newsList.random()

        // Создаем копию с правильным id и 0 лайков
        val newNewsItem = randomNews.copy(likes = 0)

        // Обновляем список
        _displayedNews.update { currentList ->
            currentList.mapIndexed { index, newsItem ->
                if (index == indexToReplace) {
                    newNewsItem
                } else {
                    newsItem
                }
            }
        }
    }

    fun likeNews(index: Int) {
        _displayedNews.update { currentList ->
            currentList.mapIndexed { i, newsItem ->
                if (i == index) {
                    newsItem.copy(likes = newsItem.likes + 1)
                } else {
                    newsItem
                }
            }
        }
    }

    // Функция для немедленной смены новости (для тестирования)
    fun forceReplaceRandomNews() {
        replaceRandomNews()
    }
}