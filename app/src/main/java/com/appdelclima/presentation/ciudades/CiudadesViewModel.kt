
package com.appdelclima.presentation.ciudades

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdelclima.repository.Repositorio
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf

class CiudadesViewModel(private val repo: Repositorio) : ViewModel() {
    var estado = mutableStateOf(CiudadesEstado())
        private set

    fun onIntent(i: CiudadesIntencion) {
        when (i) {
            is CiudadesIntencion.QueryCambio -> {
                estado.value = estado.value.copy(query = i.q)
                buscar()
            }
            else -> Unit
        }
    }

    private fun buscar() = viewModelScope.launch {
        val q = estado.value.query.trim()
        if (q.isBlank()) {
            estado.value = estado.value.copy(resultados = emptyList(), error = null, cargando = false)
            return@launch
        }
        estado.value = estado.value.copy(cargando = true, error = null)
        runCatching { repo.buscarCiudades(q, 5) }
            .onSuccess { estado.value = estado.value.copy(resultados = it) }
            .onFailure { estado.value = estado.value.copy(error = "Error buscando ciudades", resultados = emptyList()) }
        estado.value = estado.value.copy(cargando = false)
    }
}
