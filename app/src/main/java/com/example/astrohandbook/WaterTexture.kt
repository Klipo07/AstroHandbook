package com.example.astrohandbook

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.PI
import kotlin.math.sqrt

object WaterTexture {

    // Генерирует текстуру воды с волнами
    fun generateWaterTexture(size: Int = 512, time: Float): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

        val centerX = size / 2f
        val centerY = size / 2f

        for (x in 0 until size) {
            for (y in 0 until size) {
                // Нормализованные координаты (-1..1)
                val nx = (x - centerX) / centerX
                val ny = (y - centerY) / centerY

                // Расчет высоты волны с помощью нескольких синусоид
                val wave1 = sin(nx * 8f + time * 2f) * cos(ny * 6f + time * 1.5f)
                val wave2 = sin(nx * 12f - time * 3f) * cos(ny * 10f + time * 2f)
                val wave3 = sin((nx * nx + ny * ny) * 15f + time * 4f) * 0.5f

                // Комбинируем волны
                val waveHeight = (wave1 * 0.4f + wave2 * 0.3f + wave3 * 0.3f + 1f) * 0.5f

                // Цвет воды: от темно-синего до бирюзового в зависимости от высоты волны
                val red = (0.1f + waveHeight * 0.2f).coerceIn(0f, 1f)
                val green = (0.3f + waveHeight * 0.4f).coerceIn(0f, 1f)
                val blue = (0.8f + waveHeight * 0.2f).coerceIn(0f, 1f)

                // Добавляем белые барашки на гребнях волн
                val whiteCap = if (waveHeight > 0.9f) {
                    (waveHeight - 0.9f) * 10f
                } else {
                    0f
                }

                val finalRed = (red + whiteCap * 0.5f).coerceIn(0f, 1f)
                val finalGreen = (green + whiteCap * 0.5f).coerceIn(0f, 1f)
                val finalBlue = (blue + whiteCap).coerceIn(0f, 1f)

                val color = Color.rgb(
                    (finalRed * 255).toInt(),
                    (finalGreen * 255).toInt(),
                    (finalBlue * 255).toInt()
                )

                bitmap.setPixel(x, y, color)
            }
        }

        return bitmap
    }

    // Альтернативная версия с более реалистичными волнами
    fun generateAdvancedWaterTexture(size: Int = 512, time: Float): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

        for (x in 0 until size) {
            for (y in 0 until size) {
                // Используем шум Перлина (упрощенная версия)
                val value = calculateWaveValue(x.toFloat() / size, y.toFloat() / size, time)

                // Градиент от глубокого синего к бирюзовому
                val r = (0.1f + value * 0.3f).coerceIn(0f, 1f)
                val g = (0.3f + value * 0.5f).coerceIn(0f, 1f)
                val b = (0.8f + value * 0.2f).coerceIn(0f, 1f)

                val color = Color.rgb(
                    (r * 255).toInt(),
                    (g * 255).toInt(),
                    (b * 255).toInt()
                )

                bitmap.setPixel(x, y, color)
            }
        }

        return bitmap
    }

    private fun calculateWaveValue(x: Float, y: Float, time: Float): Float {
        // Несколько слоев волн с разными частотами и скоростями
        var value = 0f

        // Большие волны
        value += sin(x * 20f + time * 2f) * cos(y * 15f + time * 1.5f) * 0.3f
        value += sin(x * 40f - time * 3f) * cos(y * 30f + time * 2f) * 0.2f

        // Средние волны
        value += sin(x * 60f + time * 4f) * sin(y * 50f - time * 3f) * 0.15f

        // Мелкая рябь
        value += sin(x * 100f + time * 8f) * 0.1f
        value += cos(y * 120f - time * 7f) * 0.1f

        // Круговые волны (имитация ряби от брошенного камня)
        val distFromCenter = sqrt((x - 0.5f) * (x - 0.5f) + (y - 0.5f) * (y - 0.5f)) * 2f
        value += sin(distFromCenter * 30f - time * 5f) * 0.15f * (1f - distFromCenter)

        // Нормализуем в диапазон 0..1
        return (value + 1f) * 0.5f
    }
}