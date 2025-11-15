
package com.appdelclima.presentation.ciudades

import com.appdelclima.repository.modelos.GeoCityDto

data class CiudadesEstado(
    val query: String = "",
    val cargando: Boolean = false,
    val error: String? = null,
    val resultados: List<GeoCityDto> = emptyList()
)
