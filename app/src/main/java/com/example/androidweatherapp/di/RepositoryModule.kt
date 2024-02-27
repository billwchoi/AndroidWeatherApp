package com.example.androidweatherapp.di

import com.example.androidweatherapp.data.WeatherRepositoryImpl
import com.example.androidweatherapp.domain.model.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    fun provideWeatherRepositoryImpl(weatherRepositoryImpl: WeatherRepositoryImpl): WeatherRepository

}