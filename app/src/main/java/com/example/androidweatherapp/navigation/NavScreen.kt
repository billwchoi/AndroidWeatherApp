package com.example.androidweatherapp.navigation

// Sealed class representing different screens in the app as objects.
sealed class Screen(val route: String) {
    object LandingScreen : Screen("landing_screen")
    object ForecastScreen : Screen("forecast_screen")
}
