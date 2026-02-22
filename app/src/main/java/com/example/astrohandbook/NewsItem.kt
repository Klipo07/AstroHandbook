package com.example.astrohandbook // Замени на свой package

// data class автоматически генерирует equals, hashcode, toString
data class NewsItem(
    val id: Int,           // Уникальный идентификатор
    val title: String,     // Текст новости
    val likes: Int = 0     // Количество лайков (по умолчанию 0)
)