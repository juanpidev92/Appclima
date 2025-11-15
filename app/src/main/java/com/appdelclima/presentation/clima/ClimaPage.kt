
package com.appdelclima.presentation.clima

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.appdelclima.repository.Repositorio
import com.appdelclima.repository.modelos.*

@Composable
fun ClimaPage(repo: Repositorio, nombre: String, lat: Double, lon: Double, onCambiarCiudad: () -> Unit) {
    val ctx = LocalContext.current
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var current by remember { mutableStateOf<CurrentDto?>(null) }
    var forecast by remember { mutableStateOf<ForecastDto?>(null) }

    LaunchedEffect(lat, lon) {
        cargando = true; error = null
        runCatching {
            current = repo.climaActual(lat, lon)
            forecast = repo.pronostico5(lat, lon)
        }.onFailure { error = "Error obteniendo clima" }
        cargando = false
    }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Clima — $nombre", style = MaterialTheme.typography.headlineLarge)
            TextButton(onClick = onCambiarCiudad) { Text("Cambiar ciudad") }
        }
        when {
            cargando -> Text("Cargando…")
            error != null -> Text(error!!, color = MaterialTheme.colorScheme.error)
            current != null && forecast != null -> {
                val c = current!!
                val cond = c.weather.firstOrNull()?.description ?: "—"
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Ahora: $cond", style = MaterialTheme.typography.titleMedium)
                        Text("Temp: ${"%.1f".format(c.main.temp)}°  |  Humedad: ${c.main.humidity}%")
                    }
                }
                Text("Próximos períodos", style = MaterialTheme.typography.titleMedium)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    forecast!!.list.take(10).forEach {
                        val desc = it.weather.firstOrNull()?.main ?: "—"
                        Text("${it.dtTxt}: ${"%.0f".format(it.main.tempMin)}°/${"%.0f".format(it.main.tempMax)}° • $desc • Hum ${it.main.humidity}%")
                    }
                }
                Button(
                    onClick = {
                        val text = buildString {
                            appendLine("Pronóstico — $nombre")
                            forecast!!.list.take(10).forEach { d ->
                                val dsc = d.weather.firstOrNull()?.main ?: "—"
                                appendLine("${d.dtTxt}: ${"%.0f".format(d.main.tempMin)}°/${"%.0f".format(d.main.tempMax)}° • $dsc • Hum ${d.main.humidity}%")
                            }
                        }
                        val share = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"; putExtra(Intent.EXTRA_TEXT, text)
                        }
                        ctx.startActivity(Intent.createChooser(share, "Compartir pronóstico"))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Compartir pronóstico") }
            }
        }
    }
}
