package com.example.astrohandbook

data class Planet(
    val radius: Float,           // радиус планеты
    val orbitRadius: Float,       // радиус орбиты
    val speed: Float,             // скорость вращения (чем больше, тем быстрее)
    val textureResId: Int,        // ресурс текстуры
    val name: String = ""         // название (для отладки)
)