
package com.appdelclima.repository

import com.appdelclima.repository.modelos.*

class RepositorioMock : Repositorio {
    override suspend fun buscarCiudades(q: String, limit: Int): List<GeoCityDto> =
        listOf(GeoCityDto("Buenos Aires","AR",-34.6,-58.38))

    override suspend fun climaActual(lat: Double, lon: Double): CurrentDto =
        CurrentDto(weather = listOf(WeatherDescDto("Clouds","nublado")), main = MainDto(20.5, 60))

    override suspend fun pronostico5(lat: Double, lon: Double): ForecastDto =
        ForecastDto(list = listOf(
            ForecastItemDto("2025-11-15 12:00:00", MainForecastDto(18.0, 23.0, 55), listOf(WeatherDescDto("Clouds","nublado")))
        ))
}
