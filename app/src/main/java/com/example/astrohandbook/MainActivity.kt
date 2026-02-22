package com.example.astrohandbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.astrohandbook.ui.theme.AstroHandbookTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AstroHandbookTheme {
                var showOpenGL by remember { mutableStateOf(false) }
                var showMoon by remember { mutableStateOf(false) }
                var showNeptune by remember { mutableStateOf(false) }
                var showPlanetInfo by remember { mutableStateOf(false) }
                var selectedPlanet by remember { mutableStateOf("") }

                when {
                    showMoon -> {
                        MoonScreen(
                            onBackClick = { showMoon = false }
                        )
                    }
                    showNeptune -> {
                        NeptuneScreen(
                            onBackClick = { showNeptune = false }
                        )
                    }
                    showPlanetInfo -> {
                        PlanetInfoScreen(
                            planetName = selectedPlanet,
                            onBackClick = { showPlanetInfo = false }
                        )
                    }
                    showOpenGL -> {
                        OpenGLScreen(
                            onBackClick = { showOpenGL = false },
                            onMoonScreenRequested = {
                                showMoon = true
                            },
                            onNeptuneScreenRequested = {
                                showNeptune = true
                            },
                            onPlanetInfoRequested = { planetName ->
                                selectedPlanet = planetName
                                showPlanetInfo = true
                            }
                        )
                    }
                    else -> {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(
                                modifier = Modifier.weight(8f)
                            ) {
                                MainScreen()
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Button(
                                    onClick = { showOpenGL = true },
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text(
                                        text = "🚀 Открыть 3D сцену",
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}