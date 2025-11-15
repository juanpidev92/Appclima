
package com.appdelclima.repository.modelos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeoCityDto(val name: String, val country: String, val lat: Double, val lon: Double)

@Serializable
data class CurrentDto(val weather: List<WeatherDescDto> = emptyList(), val main: MainDto)

@Serializable
data class WeatherDescDto(val main: String, val description: String)

@Serializable
data class MainDto(val temp: Double, val humidity: Int)

@Serializable
data class ForecastDto(val list: List<ForecastItemDto> = emptyList())

@Serializable
data class ForecastItemDto(
    @SerialName("dt_txt") val dtTxt: String,
    val main: MainForecastDto,
    val weather: List<WeatherDescDto> = emptyList()
)

@Serializable
data class MainForecastDto(
    @SerialName("temp_min") val tempMin: Double,
    @SerialName("temp_max") val tempMax: Double,
    val humidity: Int
)
