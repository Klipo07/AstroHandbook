package com.example.astrohandbook

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.*

object WaterTexture {

    // Генерирует реалистичную текстуру воды с волнами
    fun generateWaterTexture(size: Int = 512, time: Float): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

        for (x in 0 until size) {
            for (y in 0 until size) {
                // Нормализованные координаты (0..1)
                val u = x.toFloat() / size
                val v = y.toFloat() / size

                // Используем несколько слоев волн с разной частотой
                val wave1 = calculateWave(u * 4f, v * 4f, time * 1.5f, 0.5f)
                val wave2 = calculateWave(u * 8f, v * 8f, time * 2.5f, 0.3f)
                val wave3 = calculateWave(u * 16f, v * 16f, time * 4f, 0.2f)
                val wave4 = calculateRipple(u, v, time, 0.4f)

                // Комбинируем волны
                val waveHeight = (wave1 + wave2 + wave3 + wave4) * 0.5f + 0.5f

                // Цвет воды: от темно-синего до бирюзового
                val red = (0.1f + waveHeight * 0.2f).coerceIn(0f, 1f)
                val green = (0.3f + waveHeight * 0.5f).coerceIn(0f, 1f)
                val blue = (0.8f + waveHeight * 0.2f).coerceIn(0f, 1f)

                // Добавляем белую пену на гребнях волн
                val foam = if (waveHeight > 0.85f) {
                    (waveHeight - 0.85f) * 6f
                } else {
                    0f
                }

                val finalRed = (red + foam * 0.3f).coerceIn(0f, 1f)
                val finalGreen = (green + foam * 0.3f).coerceIn(0f, 1f)
                val finalBlue = (blue + foam).coerceIn(0f, 1f)

                // Добавляем небольшие вариации цвета для глубины
                val depthVariation = sin(u * 20f + time) * cos(v * 20f + time) * 0.1f

                val color = Color.rgb(
                    ((finalRed + depthVariation) * 255).toInt().coerceIn(0, 255),
                    ((finalGreen + depthVariation) * 255).toInt().coerceIn(0, 255),
                    ((finalBlue + depthVariation * 2) * 255).toInt().coerceIn(0, 255)
                )

                bitmap.setPixel(x, y, color)
            }
        }

        return bitmap
    }

    // Функция для создания волны
    private fun calculateWave(x: Float, y: Float, time: Float, amplitude: Float): Float {
        return (sin(x * PI.toFloat() + time) *
                cos(y * PI.toFloat() + time * 0.7f) * amplitude)
    }

    // Функция для создания круговых волн (рябь)
    private fun calculateRipple(u: Float, v: Float, time: Float, amplitude: Float): Float {
        val centerX = 0.5f
        val centerY = 0.5f
        val dx = u - centerX
        val dy = v - centerY
        val distance = sqrt(dx * dx + dy * dy) * 4f

        return sin(distance * 10f - time * 5f) * amplitude * exp(-distance * 1.5f)
    }
}