package com.example.androidweatherapp

import com.example.androidweatherapp.utils.EXCLUDE
import com.example.androidweatherapp.utils.WEATHER_BASE_URL
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class UtilTest {

    @Test
    fun `get base url and exclude`() = runBlocking {
        val testUrl = "https://api.openweathermap.org/data/"
        val testExclude = "minutely,alerts"
        assertEquals(WEATHER_BASE_URL, testUrl)
        assertEquals(EXCLUDE, testExclude)
    }
}
