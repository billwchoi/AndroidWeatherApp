package com.example.androidweatherapp.ui

import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.androidweatherapp.model.WeatherItem
import com.example.androidweatherapp.model.WeatherResponse
import com.example.androidweatherapp.viewmodel.WeatherViewModel
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun LandingScreen(
    navController: NavHostController, // Navigation controller for navigating between screens
    viewModel: WeatherViewModel = hiltViewModel() // Weather view model obtained from Hilt for dependency injection
) {
    // State for the current location from the view model
    val permissions = viewModel.permissions
    val isLoading = remember { mutableStateOf(true) } // isLoading

    val context = LocalContext.current
    // Remember a launcher for starting the permissions request processÒ
    val launchMultiplePermissions = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionMaps ->
        // Check if all permissions are granted
        val areGranted = permissionMaps.values.reduce { acc, next -> acc && next }
        // If all permissions are granted, start location updates and show a toast
        if (areGranted) {
            viewModel.startLocationUpdates()
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
            // If permissions are denied, show a toast
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Launch an effect that checks permissions on composition
    LaunchedEffect(Unit) {
        // If permissions are already granted, start location updates
        if (permissions.all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }) {
            viewModel.startLocationUpdates()
        } else {
            // Request permissions if not already granted
            launchMultiplePermissions.launch(permissions)
        }
    }

    LaunchedEffect(Unit) {
        WeatherViewModel.landingScreenLoaded = true
    }

    // Remember the scrolling state of the list.
    val listState = rememberLazyListState()

    // Effect to refresh weather data when the list is visible.
    LaunchedEffect(listState) {
        viewModel.refresh()
    }

    // Collect the weather data as state from the view model
    val weather25Data by viewModel.weather25UseDataFlow.collectAsState()
    val forecast25Data by viewModel.forecast25UseDataFlow.collectAsState()

    Scaffold(
        // Define the bottom app bar with navigation items
        bottomBar = {
            BottomAppBar(
                containerColor = Color(0xFF008D75),
                contentColor = Color.Black
            ) {
                // Define the home navigation item
                BottomNavigationItem(
                    selected = false,
                    onClick = { navController.navigate("landing_screen") },
                    label = { Text("Home") },
                    icon = {
                        Icon(
                            Icons.Filled.Home,
                            contentDescription = "Home",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                )
                // Define the forecast navigation item
                BottomNavigationItem(
                    selected = false,
                    onClick = { navController.navigate("forecast_screen") },
                    label = { Text("Forecast") },
                    icon = {
                        Icon(
                            Icons.Filled.DateRange,
                            contentDescription = "Forecast",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                )
            }
        }
    ) { innerPadding ->

        Box(modifier = Modifier.fillMaxSize()) {
            // Conditionally display the CircularProgressIndicator
            if (WeatherViewModel.dataLoaded) {
                isLoading.value = false

                if (!WeatherViewModel.secondStartLocationUpdates) {
                    viewModel.startLocationUpdates()
                    WeatherViewModel.secondStartLocationUpdates = true
                }
            } else {
                isLoading.value = true
            }
            if (isLoading.value) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(50.dp)
                )
            }
            // Other UI elements
            Column {
                // Display the current weather using a custom composable
                CurrentWeatherDisplay(weather25Data)

                forecast25Data?.list?.let { forecastList ->
                    LazyColumn(modifier = Modifier.padding(innerPadding)) {
                        items(forecastList) { forecast ->
                            ForecastItemList(forecast = forecast, viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ForecastItemList(forecast: WeatherItem, viewModel: WeatherViewModel) {
    Column {
        ForecastItem(forecast, viewModel)
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
fun ForecastItem(forecast: WeatherItem, viewModel: WeatherViewModel) {
    Card(
        backgroundColor = Color(0xFF008D75), // Use the correct color for the card
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp) // This adds padding on the top and bottom of each item
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp) // This adds padding inside the card
        ) {
            convertTo12HourFormat(forecast.dtTxt)?.let {
                Text(
                    text = it, // Use a function to format the time
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.width(8.dp)) // This adds space between the text and the icon
            Text(
                text = "${forecast.main.temp.toInt()}°F",
                color = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp)) // This adds space between the temperature text and the icon
            CoilImage(
                modifier = Modifier.size(40.dp),
                imageModel = { "https://openweathermap.org/img/wn/${forecast.weather.first().icon}@2x.png" },
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                    contentDescription = "Weather Image",
                    colorFilter = null,
                ),
            )
        }
    }
}

// Composable function to display current weather details
@Composable
fun CurrentWeatherDisplay(
    weather25Data: WeatherResponse?
) {
    // Get city name from latitude and longitude, display coordinates if city name is null
    Column(modifier = Modifier.fillMaxWidth()) {
        if (weather25Data != null) {
            Text(
                text = weather25Data.name ?: "",
                color = Color.Black,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.h6
            )
        }
        Row(
            modifier = Modifier
                .background(Color(0xFFFFFFFF))
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                if (weather25Data != null) {
                    Text(
                        text = "Wind",
                        color = Color.Black,
                        style = MaterialTheme.typography.subtitle1
                    )
                    Text(
                        text = windDirection(weather25Data.wind.deg),
                        color = Color.Black,
                        style = MaterialTheme.typography.subtitle2
                    )

                    Text(
                        text = "${weather25Data.wind.speed} mph",
                        color = Color.Black,
                        style = MaterialTheme.typography.subtitle2
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                if (weather25Data != null) {
                    Text(
                        text = "Feels like: ${weather25Data.main.feels_like.toInt()}°",
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.End),
                        style = MaterialTheme.typography.body1
                    )
                    Text(
                        text = "${weather25Data.main.temp_min.toInt()}/${
                            weather25Data.main.temp_max.toInt()
                        }°",
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.End),
                        style = MaterialTheme.typography.body1
                    )
                }
            }
        }
    }
}

private fun convertTo12HourFormat(time24: String): String? {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    val date = inputFormat.parse(time24)
    return date?.let { outputFormat.format(it) }
}

private fun windDirection(degrees: Int): String {
    return when (degrees) {
        in 0..45 -> "N"
        in 46..135 -> "E"
        in 136..225 -> "S"
        in 226..315 -> "W"
        else -> "N"
    }
}



