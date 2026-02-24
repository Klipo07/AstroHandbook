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

    // Хранилище лайков для каждой новости по её ID
    private val likesMap = mutableMapOf<Int, Int>()

    // Текущие отображаемые новости
    private val _displayedNews = MutableStateFlow(
        NewsRepository.newsList.take(4).map { newsItem ->
            // При инициализации загружаем сохраненные лайки или ставим 0
            newsItem.copy(likes = likesMap[newsItem.id] ?: 0)
        }
    )
    val displayedNews: StateFlow<List<NewsItem>> = _displayedNews.asStateFlow()

    init {
        startNewsRotation()
    }

    private fun startNewsRotation() {
        viewModelScope.launch {
            while (true) {
                delay(5000)
                replaceRandomNews()
            }
        }
    }

    private fun replaceRandomNews() {
        val currentList = _displayedNews.value
        if (currentList.isEmpty()) return

        // Выбираем случайный индекс для замены
        val indexToReplace = (0 until currentList.size).random()

        // Получаем случайную новость из общего списка
        val randomNews = NewsRepository.newsList.random()

        // Сохраняем лайки для новой новости (если они были раньше)
        val savedLikes = likesMap[randomNews.id] ?: 0

        // Создаем новость с сохраненными лайками
        val newNewsItem = randomNews.copy(likes = savedLikes)

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
                    // Увеличиваем лайки
                    val newLikes = newsItem.likes + 1
                    // Сохраняем в карту лайков по ID новости
                    likesMap[newsItem.id] = newLikes
                    newsItem.copy(likes = newLikes)
                } else {
                    newsItem
                }
            }
        }
    }

    fun forceReplaceRandomNews() {
        replaceRandomNews()
    }

    // Функция для просмотра статистики (опционально)
    fun getLikesStats(): String {
        return likesMap.entries.joinToString { "${it.key}=${it.value}" }
    }
}