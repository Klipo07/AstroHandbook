package com.example.astrohandbook

import android.opengl.GLSurfaceView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun OpenGLScreen(
    onBackClick: () -> Unit // Функция для возврата
) {
    val context = LocalContext.current

    val glSurfaceView = remember {
        GLSurfaceView(context).apply {
            setEGLContextClientVersion(2)
            setRenderer(AstronomyRenderer(context))
            renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // OpenGL поверхность на весь экран
        AndroidView(
            factory = { glSurfaceView },
            modifier = Modifier.fillMaxSize()
        )

        // Кнопка "Назад" в верхнем левом углу поверх OpenGL
        Button(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Text("← Назад")
        }
    }
}