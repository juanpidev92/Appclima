
package com.appdelclima.repository

import com.appdelclima.repository.modelos.*

interface Repositorio {
    suspend fun buscarCiudades(q: String, limit: Int = 5): List<GeoCityDto>
    suspend fun climaActual(lat: Double, lon: Double): CurrentDto
    suspend fun pronostico5(lat: Double, lon: Double): ForecastDto
}
