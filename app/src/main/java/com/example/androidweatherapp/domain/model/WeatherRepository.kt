package com.example.androidweatherapp.domain.model

import com.example.androidweatherapp.model.WeatherForecast
import com.example.androidweatherapp.model.WeatherResponse
import com.example.androidweatherapp.utils.WeatherResult

interface WeatherRepository {
    suspend fun getWeather25(lat: Double, lot: Double): WeatherResult<WeatherResponse>
    suspend fun getForecast25(lat: Double, lot: Double): WeatherResult<WeatherForecast>
}