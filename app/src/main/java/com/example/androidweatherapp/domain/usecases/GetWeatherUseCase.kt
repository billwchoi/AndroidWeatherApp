package com.example.androidweatherapp.domain.usecases

import com.example.androidweatherapp.domain.model.WeatherRepository
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(private val repository: WeatherRepository) {
    suspend fun invoke(lat: Double, lon: Double) = repository.getWeather25(lat, lon)
}