package com.example.astrohandbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.astrohandbook.ui.theme.AstroHandbookTheme // Убедись, что имя темы совпадает с твоим проектом

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AstroHandbookTheme {
                // Создаем ViewModel
                val viewModel: MainViewModel = viewModel()
                // Отображаем главный экран
                MainScreen(viewModel = viewModel)
            }
        }
    }
}