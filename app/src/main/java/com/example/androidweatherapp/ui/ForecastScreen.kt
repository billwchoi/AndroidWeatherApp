package com.example.androidweatherapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.androidweatherapp.model.DailyWeather
import com.example.androidweatherapp.model.WeatherItem
import com.example.androidweatherapp.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ForecastScreen(
    navController: NavHostController,
    viewModel: WeatherViewModel = hiltViewModel()
) {

    // Remember the scrolling state of the list.
    val listState = rememberLazyListState()
    val isLoading = remember { mutableStateOf(true) } // isLoading

    // Effect to refresh weather data when the list is visible.
    LaunchedEffect(listState) {
        viewModel.refresh()
    }

    // Collect the weather data as state from the view model
    val forecast25Data by viewModel.forecast25UseDataFlow.collectAsState()
    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color(0xFF008D75),
                contentColor = Color.Black
            ) {
                BottomNavigationItem(
                    selected = false,
                    onClick = { navController.navigate("landing_screen") },
                    label = { Text("Home") },
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home") }
                )
                BottomNavigationItem(
                    selected = false,
                    onClick = { navController.navigate("forecast_screen") },
                    label = { Text("Forecast") },
                    icon = { Icon(Icons.Filled.DateRange, contentDescription = "Forecast") }
                )
            }
        }
    ) { innerPadding ->

        Box(modifier = Modifier.fillMaxSize()) {

            if (WeatherViewModel.dataLoaded) {
                isLoading.value = false
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

            val forecastData = mutableListOf<WeatherItem>()
            forecast25Data?.list?.groupBy { it.dtTxt.substring(0, 10) }?.forEach {
                forecastData.add(it.value.first())
            }

            forecastData.let { forecastList ->
                LazyColumn(modifier = Modifier.padding(innerPadding)) {
                    items(forecastList) { forecast ->
                        DailyItemList(forecast = forecast)
                    }
                }
            }
        }
    }
}

@Composable
fun DailyItemList(forecast: WeatherItem) {

    Column {
        DailyItem(forecast)
        Spacer(modifier = Modifier.height(2.dp))
    }

}


@Composable
fun DailyItem(forecast: WeatherItem) {
    DailyCard(
        convertToDayFormat(forecast.dtTxt) ?: "",
        forecast.main.tempMax.toInt().toString(),
        forecast.main.tempMin.toInt().toString(),
        forecast.wind.speed.toString(),
        windDirection(forecast.wind.deg),
        forecast.weather.first().description,
        forecast.weather.firstOrNull()?.icon ?: ""
    )
}

@Composable
fun DailyCard(
    date: String,
    highTemp: String,
    lowTemp: String,
    windSpeed: String,
    windDirection: String,
    description: String,
    icon: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF008D75))
    ) {
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = date,
            color = Color.Black,
            fontSize = 15.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Hi Temp: $highTemp",
                    color = Color.Black,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Low Temp: $lowTemp",
                    color = Color.Black,
                    fontSize = 15.sp
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Wind Speed",
                    color = Color.Black,
                    fontSize = 15.sp
                )
                Text(
                    text = "$windSpeed mph",
                    color = Color.Black,
                    fontSize = 15.sp
                )
                Text(
                    text = windDirection,
                    color = Color.Black,
                    fontSize = 15.sp
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                val imageUrl = "https://openweathermap.org/img/wn/$icon.png"
                AsyncImage(
                    model = imageUrl,
                    contentDescription = description,
                    modifier = Modifier.size(45.dp)
                )
                Text(
                    text = description,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.End),
                    fontSize = 13.sp
                )
            }
        }
    }
}

private fun convertToDayFormat(time24: String): String? {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val outputFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())

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