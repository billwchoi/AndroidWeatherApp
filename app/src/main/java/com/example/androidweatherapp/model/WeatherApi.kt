package com.example.androidweatherapp.model

import com.example.androidweatherapp.utils.API_KEY
import com.example.androidweatherapp.utils.UNIT
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// Defines the interface for the Weather API service.
interface WeatherApi {

    @GET("2.5/weather")
    suspend fun getWeather25(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String = API_KEY,
        @Query("units") units: String = UNIT
    ): Response<WeatherResponse>

    @GET("2.5/forecast")
    suspend fun getForecast25(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String = API_KEY,
        @Query("units") units: String = UNIT
    ): Response<WeatherForecast>

}
