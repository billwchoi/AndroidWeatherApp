package com.example.androidweatherapp.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidweatherapp.domain.usecases.GetForecastUseCase
import com.example.androidweatherapp.domain.usecases.GetWeatherUseCase
import com.example.androidweatherapp.model.WeatherApi
import com.example.androidweatherapp.model.WeatherData
import com.example.androidweatherapp.model.WeatherForecast
import com.example.androidweatherapp.model.WeatherResponse
import com.example.androidweatherapp.utils.API_KEY
import com.example.androidweatherapp.utils.EXCLUDE
import com.example.androidweatherapp.utils.UNIT
import com.example.androidweatherapp.utils.WeatherResult
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val application: Application,
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getForecastUseCase: GetForecastUseCase
) : ViewModel() {

    // A state flow to hold the latest weather data
    private var _weather25UseDataFlow = MutableStateFlow<WeatherResponse?>(null)
    val weather25UseDataFlow get() = _weather25UseDataFlow

    private var _forecast25UseDataFlow = MutableStateFlow<WeatherForecast?>(null)
    val forecast25UseDataFlow get() = _forecast25UseDataFlow

    val permissions = arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
    )

    // Initialized FusedLocationProviderClient for location updates
    private var fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)
    // Callback for receiving location updates
    private var locationCallback: LocationCallback

    // A state flow to hold the current location data
    private val _currentLocation = MutableStateFlow(LatLng(38.804836, -77.046921))
    val currentLocation: StateFlow<LatLng> = _currentLocation.asStateFlow()

//    private val locationRequested = MutableStateFlow(false)

    companion object {
        var secondStartLocationUpdates = false
        var locationRequested = false
        var locationUpdated = false
        var locationLatitude = 0.0
        var locationLongitude = 0.0
        var landingScreenLoaded = false
        var dataLoaded = false
        var isLoading = false
    }

    init {
        // Initializing the location callback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                // Update the current location in state flow
                locationResult.locations.firstOrNull()?.let { location ->

                    if (!locationUpdated) {
                        _currentLocation.value = LatLng(location.latitude, location.longitude)

                        locationLatitude = location.latitude
                        locationLongitude = location.longitude

                        locationUpdated = true
                        refresh() // call api
                    }
                }
            }
        }
    }

    // Function to start location updates. Annotated to suppress the MissingPermission warning
    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {

        locationCallback.let {
            val locationRequest = if (!locationRequested) {
                locationRequested = true
                // First request - update immediately
                LocationRequest.Builder(
                    Priority.PRIORITY_HIGH_ACCURACY, 100
                )
                    .setWaitForAccurateLocation(false)
                    .setMinUpdateIntervalMillis(100 * 1 * 1) // Immediate update
                    .setMaxUpdateDelayMillis(100 * 1 * 1)
                    .build()
            } else {
                // Subsequent requests - update after 1 hour
                LocationRequest.Builder(
                    Priority.PRIORITY_HIGH_ACCURACY, 100
                )
                    .setWaitForAccurateLocation(false)
                    .setMinUpdateIntervalMillis(60 * 60 * 1000) // Update every 1 hour
                    .setMaxUpdateDelayMillis(60 * 60 * 1000) // Update every 1 hour
                    .build()
            }
            // Requesting location updates with the created request and callback
            fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                it,
                Looper.getMainLooper()
            )
        }
    }

    // Function to refresh weather data
    fun refresh() {
        if (locationUpdated) {
            fetchWeather25Use()
            fetchForecast25Use()
        }
    }

    private fun fetchWeather25Use() {
        dataLoaded = false
        // Launching a coroutine in IO dispatcher for network request
        viewModelScope.launch(Dispatchers.IO) {
            // Making the API call to get weather data
            getWeatherUseCase.invoke(locationLatitude, locationLongitude).let { result ->
                withContext(Dispatchers.Main) {
                    when(result) {
                        is WeatherResult.Success -> {
                            _weather25UseDataFlow.value = result.data
                            dataLoaded = true
                        }
                        is WeatherResult.Error -> {
                            result.message?.let { onError(it) }
                            dataLoaded = true
                        }
                        is WeatherResult.Loading -> {
                            dataLoaded = false
                        }
                    }
                }
            }

//            val response = weatherApi.getWeather25(locationLatitude, locationLongitude, API_KEY, UNIT)
//
//            // Switching context to the Main thread for UI operations
//            withContext(Dispatchers.Main) {
//                if (response.isSuccessful) {
//                    // Updating the state flow with the received data
//                    weather25DataFlow.value = response.body()
//                    dataLoaded = true
//                } else {
//                    // Handling errors
//                    onError(response.message())
//                    dataLoaded = true
//                }
//            }
        }
    }

    // Function to fetch weather data from the API
    private fun fetchForecast25Use() {
        dataLoaded = false
        // Launching a coroutine in IO dispatcher for network request
        viewModelScope.launch(Dispatchers.IO) {
            // Making the API call to get weather data

            getForecastUseCase.invoke(locationLatitude, locationLongitude).let { result ->
                withContext(Dispatchers.Main) {
                    when (result) {
                        is WeatherResult.Success -> {
                            _forecast25UseDataFlow.value = result.data
                            dataLoaded = true
                        }

                        is WeatherResult.Error -> {
                            result.message?.let { onError(it) }
                            dataLoaded = true
                        }
                        is WeatherResult.Loading -> {
                            dataLoaded = false
                        }
                    }
                }
            }

//            val response = weatherApi.getForecast25(locationLatitude, locationLongitude, API_KEY, UNIT)
//
//            // Switching context to the Main thread for UI operations
//            withContext(Dispatchers.Main) {
//                if (response.isSuccessful) {
//                    // Updating the state flow with the received data
//                    forecast25DataFlow.value = response.body()
//                    dataLoaded = true
//                } else {
//                    // Handling errors
//                    onError(response.message())
//                    dataLoaded = true
//                }
//            }
        }
    }

    private fun onError(message: String) {
        println(message)
    }

    override fun onCleared() {
        super.onCleared()
    }

}
