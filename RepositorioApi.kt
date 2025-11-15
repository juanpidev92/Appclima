package com.ejemplo.clima

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class RepositorioApi(private val apiKey: String) {

    private val client = HttpClient(Android) {
        install(Logging) { level = LogLevel.NONE } // usa BODY para debug
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true; explicitNulls = false })
        }
    }

    suspend fun buscarCiudades(q: String, limit: Int = 5): List<GeoCityDto> {
        val url = "https://api.openweathermap.org/geo/1.0/direct" +
                "?q=${q.trim()}&limit=$limit&appid=$apiKey"
        return client.get(url).body()
    }

    suspend fun climaActual(lat: Double, lon: Double): CurrentDto {
        val url = "https://api.openweathermap.org/data/2.5/weather" +
                "?lat=$lat&lon=$lon&units=metric&lang=es&appid=$apiKey"
        return client.get(url).body()
    }

    suspend fun pronostico5(lat: Double, lon: Double): ForecastDto {
        val url = "https://api.openweathermap.org/data/2.5/forecast" +
                "?lat=$lat&lon=$lon&units=metric&lang=es&appid=$apiKey"
        return client.get(url).body()
    }
}
