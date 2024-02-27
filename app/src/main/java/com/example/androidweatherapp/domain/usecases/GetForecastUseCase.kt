package com.example.androidweatherapp.domain.usecases

import com.example.androidweatherapp.domain.model.WeatherRepository
import javax.inject.Inject

class GetForecastUseCase @Inject constructor(private val repository: WeatherRepository) {
    suspend fun invoke(lat: Double, lon: Double) = repository.getForecast25(lat, lon)
}