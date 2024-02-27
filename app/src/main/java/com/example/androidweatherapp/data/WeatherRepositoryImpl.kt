package com.example.androidweatherapp.data

import com.example.androidweatherapp.domain.model.WeatherRepository
import com.example.androidweatherapp.model.WeatherForecast
import com.example.androidweatherapp.model.WeatherResponse
import com.example.androidweatherapp.utils.WeatherResult
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : WeatherRepository {
    override suspend fun getWeather25(lat: Double, lon: Double): WeatherResult<WeatherResponse> {
        WeatherResult.Loading(null)

        try {
            val response = remoteDataSource.getWeather25(lat, lon)
            if (response.isSuccessful) {
                val body = response.body()
                body.let {
                    return WeatherResult.Success(data = body)
                }
            } else {
                return WeatherResult.Error(data = null, message = response.message())
            }
        } catch (e: Exception) {
            return WeatherResult.Error(data = null, message = e.message)
        }

    }

    override suspend fun getForecast25(lat: Double, lon: Double): WeatherResult<WeatherForecast> {
        WeatherResult.Loading(null)

        try {
            val response = remoteDataSource.getForecast25(lat, lon)
            if (response.isSuccessful) {
                val body = response.body()
                body.let {
                    return WeatherResult.Success(data = body)
                }
            } else {
                return WeatherResult.Error(data = null, message = response.message())
            }
        } catch (e: Exception) {
            return WeatherResult.Error(data = null, message = e.message)
        }
    }

}