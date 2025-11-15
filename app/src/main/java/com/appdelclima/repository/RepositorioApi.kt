
package com.appdelclima.repository

import com.appdelclima.repository.modelos.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class RepositorioApi(
    private val apiKey: String,
    private val client: HttpClient = HttpClient(Android) {
        install(Logging) { level = LogLevel.NONE } // usa BODY para debug
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true; explicitNulls = false }) }
    }
) : Repositorio {
    override suspend fun buscarCiudades(q: String, limit: Int): List<GeoCityDto> =
        client.get("https://api.openweathermap.org/geo/1.0/direct?q=${q.trim()}&limit=$limit&appid=$apiKey").body()

    override suspend fun climaActual(lat: Double, lon: Double): CurrentDto =
        client.get("https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&units=metric&lang=es&appid=$apiKey").body()

    override suspend fun pronostico5(lat: Double, lon: Double): ForecastDto =
        client.get("https://api.openweathermap.org/data/2.5/forecast?lat=$lat&lon=$lon&units=metric&lang=es&appid=$apiKey").body()
}
