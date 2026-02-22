package com.example.astrohandbook

import android.opengl.GLSurfaceView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Color

@Composable
fun MoonScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    val glSurfaceView = remember {
        GLSurfaceView(context).apply {
            setEGLContextClientVersion(2)
            setRenderer(MoonRenderer(context))
            renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // OpenGL поверхность
        AndroidView(
            factory = { glSurfaceView },
            modifier = Modifier.fillMaxSize()
        )

        // Верхняя панель с кнопкой назад
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Button(
                onClick = onBackClick
            ) {
                Text("← Назад к планетам")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Луна - освещение по модели Фонга",
                color = Color.White
            )
        }
    }
}