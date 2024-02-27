package com.example.androidweatherapp.data

import com.example.androidweatherapp.model.WeatherApi
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val weatherApi: WeatherApi
) {
    suspend fun getWeather25(lat: Double, lon: Double) = weatherApi.getWeather25(lat, lon)

    suspend fun getForecast25(lat: Double, lon: Double) = weatherApi.getForecast25(lat, lon)
}