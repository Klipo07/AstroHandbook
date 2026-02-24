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
import androidx.compose.ui.text.font.FontWeight

@Composable
fun NeptuneScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    val glSurfaceView = remember {
        GLSurfaceView(context).apply {
            setEGLContextClientVersion(2)
            setRenderer(NeptuneRenderer(context))
            renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { glSurfaceView },
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Button(onClick = onBackClick) {
                Text("← Назад")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "🌊 Нептун - Водная планета",
                color = Color.Cyan,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Поверхность с динамическими волнами",
                color = Color.White
            )
        }
    }
}