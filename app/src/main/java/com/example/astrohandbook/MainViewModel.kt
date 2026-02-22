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

        val indexToReplace = (0 until currentList.size).random()
        val randomNews = NewsRepository.newsList.random()
        val newNewsItem = randomNews.copy(likes = 0)

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

    fun forceReplaceRandomNews() {
        replaceRandomNews()
    }
}