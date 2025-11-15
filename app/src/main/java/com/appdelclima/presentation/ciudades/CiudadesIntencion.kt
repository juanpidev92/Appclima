
package com.appdelclima.presentation.ciudades

import com.appdelclima.repository.modelos.GeoCityDto

sealed interface CiudadesIntencion {
    data class QueryCambio(val q: String) : CiudadesIntencion
    data class Elegir(val city: GeoCityDto) : CiudadesIntencion
    data object UsarGeo : CiudadesIntencion
}
