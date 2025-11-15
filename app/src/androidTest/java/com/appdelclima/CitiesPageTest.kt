
package com.appdelclima

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test
import com.appdelclima.presentation.ciudades.CiudadesPage
import com.appdelclima.repository.Repositorio
import com.appdelclima.repository.modelos.*

class CitiesPageTest {
    @get:Rule val compose = createComposeRule()

    class FakeRepo: Repositorio {
        override suspend fun buscarCiudades(q: String, limit: Int) =
            listOf(GeoCityDto("Buenos Aires","AR",-34.6,-58.38))
        override suspend fun climaActual(lat: Double, lon: Double) =
            CurrentDto(emptyList(), MainDto(20.0,50))
        override suspend fun pronostico5(lat: Double, lon: Double) =
            ForecastDto()
    }

    @Test
    fun escribirQuery_muestraResultados() {
        compose.setContent {
            CiudadesPage(repo = FakeRepo(), onElegida = {}, onGeo = {})
        }
        compose.onNodeWithText("Buscar ciudad").performTextInput("bue")
        compose.waitForIdle()
        compose.onNodeWithText("Buenos Aires (AR)").assertExists()
    }
}
