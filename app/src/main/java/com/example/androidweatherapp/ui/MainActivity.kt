package com.example.androidweatherapp.ui


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.androidweatherapp.navigation.NavigationSystem
import com.example.androidweatherapp.ui.theme.CoroutinesHiltComposeRetrofitTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoroutinesHiltComposeRetrofitTheme {
                NavigationSystem()
            }
        }
    }
}



