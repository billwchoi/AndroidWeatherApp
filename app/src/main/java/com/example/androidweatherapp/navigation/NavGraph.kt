package com.example.androidweatherapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.androidweatherapp.ui.ForecastScreen
import com.example.androidweatherapp.ui.LandingScreen

// Composable function that sets up the navigation for the app.
@Composable
fun NavigationSystem() {
    // Create and remember a NavController to handle navigation
    val navController = rememberNavController()

    NavHost(
        navController,
        modifier = Modifier,
        startDestination = Screen.LandingScreen.route,
    ) {
        // Define a composable associated with the 'LandingScreen' route
        composable(Screen.LandingScreen.route) {
            // Call the LandingScreen composable when this route is navigated to
            LandingScreen(navController)
        }
        composable(Screen.ForecastScreen.route) {

            ForecastScreen(navController)
        }
    }
}
