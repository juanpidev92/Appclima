
package com.appdelclima.presentation.ciudades

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.appdelclima.repository.Repositorio
import com.appdelclima.repository.modelos.GeoCityDto

@Composable
fun CiudadesPage(
    repo: Repositorio,
    onElegida: (GeoCityDto) -> Unit,
    onGeo: (GeoCityDto) -> Unit
) {
    val vm = remember { CiudadesViewModel(repo) }
    CiudadesView(
        estado = vm.estado.value,
        onIntent = vm::onIntent,
        onElegirCiudad = onElegida,
        onUsarGeo = { onGeo(GeoCityDto("Buenos Aires","AR",-34.6037,-58.3816)) }
    )
}
