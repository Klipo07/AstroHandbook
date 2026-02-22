package com.example.astrohandbook

import android.opengl.GLSurfaceView
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.graphics.Color

@Composable
fun OpenGLScreen(
    onBackClick: () -> Unit,
    onMoonScreenRequested: () -> Unit  // Новый параметр для перехода к Луне
) {
    val context = LocalContext.current
    var selectedPlanetIndex by remember { mutableIntStateOf(0) } // 0 = Солнце

    // Список планет для информации
    val planets = listOf(
        Planet(0.8f, 0f, 0f, R.drawable.sun, "Солнце", ""),
        Planet(0.15f, 2.0f, 0.5f, R.drawable.mercury, "Меркурий", ""),
        Planet(0.18f, 2.8f, 0.35f, R.drawable.venus, "Венера", ""),
        Planet(0.2f, 3.6f, 0.25f, R.drawable.earth, "Земля", ""),
        Planet(0.17f, 4.4f, 0.2f, R.drawable.mars, "Марс", ""),
        Planet(0.06f, 0.5f, 1.2f, R.drawable.moon, "Луна", "")
    )

    // Создаем рендерер с возможностью обновления выбранной планеты
    val renderer = remember {
        AstronomyRenderer(context).apply {
            setSelectedPlanetIndex(selectedPlanetIndex)
        }
    }

    val glSurfaceView = remember {
        GLSurfaceView(context).apply {
            setEGLContextClientVersion(2)
            setRenderer(renderer)
            renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        }
    }

    // Обновляем выбранную планету в рендерере при изменении
    renderer.setSelectedPlanetIndex(selectedPlanetIndex)

    Box(modifier = Modifier.fillMaxSize()) {
        // OpenGL поверхность
        AndroidView(
            factory = { glSurfaceView },
            modifier = Modifier.fillMaxSize()
        )

        // Верхняя панель с кнопкой назад
        Button(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Text("← Назад")
        }

        // Нижняя панель с кнопками управления
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Кнопка информации - теперь с проверкой на Луну
            Button(
                onClick = {
                    val planet = planets[selectedPlanetIndex]
                    if (planet.name == "Луна") {
                        // Открываем экран Луны с освещением по Фонгу
                        onMoonScreenRequested()
                    } else {
                        Toast.makeText(
                            context,
                            "${planet.name}: ${planet.getInfo()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                modifier = Modifier
                    .padding(8.dp)
                    .width(200.dp)
            ) {
                Text("ℹ️ Информация")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Кнопки влево/вправо
            Row(
                modifier = Modifier.padding(8.dp)
            ) {
                Button(
                    onClick = {
                        selectedPlanetIndex = (selectedPlanetIndex - 1 + planets.size) % planets.size
                    },
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .width(100.dp)
                ) {
                    Text("◀ Влево")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        selectedPlanetIndex = (selectedPlanetIndex + 1) % planets.size
                    },
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .width(100.dp)
                ) {
                    Text("Вправо ▶")
                }
            }

            // Текст с названием выбранной планеты
            Text(
                text = "Выбрана: ${planets[selectedPlanetIndex].name}",
                modifier = Modifier.padding(8.dp),
                color = Color.White
            )

            // Подсказка для Луны
            if (planets[selectedPlanetIndex].name == "Луна") {
                Text(
                    text = "Нажмите Информация для просмотра Луны с освещением",
                    modifier = Modifier.padding(8.dp),
                    color = Color.Yellow
                )
            }
        }
    }
}