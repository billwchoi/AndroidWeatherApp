package com.example.androidweatherapp.di

import android.app.Application
import com.example.androidweatherapp.model.WeatherApi
import com.example.androidweatherapp.utils.WEATHER_BASE_URL
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    // Provides a single instance of HttpLoggingInterceptor for logging network requests and responses.
    @Singleton
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY) // Set log level to BODY for detailed logging.
    }

    // Provides a single instance of OkHttpClient configured with a logging interceptor.
    @Singleton
    @Provides
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    // Provides a single instance of Retrofit for making network requests to the Weather API.
    @Provides
    @Singleton
    fun provideRetrofitWeather(okHttpClient: OkHttpClient): WeatherApi {
        return Retrofit.Builder()
            .baseUrl(WEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Add Gson converter for JSON deserialization.
            .client(okHttpClient) // Set the custom OkHttpClient for network requests.
            .build()
            .create(WeatherApi::class.java) // Create an implementation of the WeatherApi interface.
    }

    // Provides a single instance of FusedLocationProviderClient for location services.
    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(app: Application): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(app) // Get FusedLocationProviderClient for the application context.
    }

}