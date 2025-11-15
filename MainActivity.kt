package com.ejemplo.clima

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val prefs = remember { Prefs(this) }
                val repo = remember { RepositorioApi(apiKey = "TU_API_KEY_AQUI") }

                val saved = remember { prefs.getSavedCity() }
                val nav = rememberNavController()
                val start = if (saved == null) "ciudades" else "clima/${saved.name}/${saved.lat}/${saved.lon}"

                NavHost(navController = nav, startDestination = start) {
                    composable("ciudades") {
                        CitiesScreen(
                            repo = repo,
                            onPickCity = { c ->
                                prefs.saveCity(c.name, c.lat, c.lon)
                                nav.navigate("clima/${c.name}/${c.lat}/${c.lon}") {
                                    popUpTo("ciudades") { inclusive = true }
                                    launchSingleTop = true
                                }
                            },
                            onPickByGeo = {
                                val fake = GeoCityDto("Buenos Aires", "AR", -34.6037, -58.3816)
                                prefs.saveCity(fake.name, fake.lat, fake.lon)
                                nav.navigate("clima/${fake.name}/${fake.lat}/${fake.lon}") {
                                    popUpTo("ciudades") { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                    composable(
                        route = "clima/{name}/{lat}/{lon}",
                        arguments = listOf(
                            navArgument("name") { type = NavType.StringType },
                            navArgument("lat") { type = NavType.StringType },
                            navArgument("lon") { type = NavType.StringType }
                        )
                    ) { backStack ->
                        val name = backStack.arguments?.getString("name").orEmpty()
                        val lat = backStack.arguments?.getString("lat")!!.toDouble()
                        val lon = backStack.arguments?.getString("lon")!!.toDouble()
                        WeatherScreen(
                            repo = repo,
                            cityName = name,
                            lat = lat,
                            lon = lon,
                            onChangeCity = {
                                nav.navigate("ciudades") {
                                    popUpTo("ciudades") { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/* ---------- Ciudades ---------- */

data class CitiesState(
    val query: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val results: List<GeoCityDto> = emptyList()
)

@Composable
fun CitiesScreen(
    repo: RepositorioApi,
    onPickCity: (GeoCityDto) -> Unit,
    onPickByGeo: () -> Unit
) {
    var state by rememberSaveable { mutableStateOf(CitiesState()) }

    LaunchedEffect(state.query) {
        val q = state.query.trim()
        if (q.isBlank()) { state = state.copy(results = emptyList(), error = null, loading = false); return@LaunchedEffect }
        state = state.copy(loading = true, error = null)
        try {
            state = state.copy(results = repo.buscarCiudades(q, 5))
        } catch (_: Throwable) {
            state = state.copy(error = "Error buscando ciudades", results = emptyList())
        } finally {
            state = state.copy(loading = false)
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Ciudades", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = state.query,
            onValueChange = { state = state.copy(query = it) },
            label = { Text("Buscar ciudad") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onPickByGeo) { Text("Usar mi ubicación") }
        }
        if (state.loading) { Spacer(Modifier.height(12.dp)); Text("Cargando…") }
        if (state.error != null) { Spacer(Modifier.height(12.dp)); Text(state.error!!, color = MaterialTheme.colorScheme.error) }

        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.results) { city ->
                Surface(tonalElevation = 2.dp, modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPickCity(city) }
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) { Text("${city.name} (${city.country})"); Text("Elegir") }
                }
            }
        }
    }
}

/* ---------- Clima ---------- */

data class WeatherState(
    val loading: Boolean = true,
    val error: String? = null,
    val current: CurrentDto? = null,
    val forecast: ForecastDto? = null
)

@Composable
fun WeatherScreen(
    repo: RepositorioApi,
    cityName: String,
    lat: Double,
    lon: Double,
    onChangeCity: () -> Unit
) {
    var state by remember { mutableStateOf(WeatherState()) }

    LaunchedEffect(lat, lon) {
        state = state.copy(loading = true, error = null)
        try {
            val now = repo.climaActual(lat, lon)
            val fc = repo.pronostico5(lat, lon)
            state = state.copy(current = now, forecast = fc)
        } catch (_: Throwable) {
            state = state.copy(error = "Error obteniendo clima")
        } finally {
            state = state.copy(loading = false)
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Clima — $cityName", style = MaterialTheme.typography.headlineLarge)
            TextButton(onClick = onChangeCity) { Text("Cambiar ciudad") }
        }

        when {
            state.loading -> Text("Cargando…")
            state.error != null -> Text(state.error!!, color = MaterialTheme.colorScheme.error)
            state.current != null && state.forecast != null -> {
                val c = state.current
                val cond = c!!.weather.firstOrNull()?.description ?: "—"
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Ahora: $cond", style = MaterialTheme.typography.titleMedium)
                        Text("Temp: ${"%.1f".format(c.main.temp)}°  |  Humedad: ${c.main.humidity}%")
                    }
                }

                Text("Próximas horas/días", style = MaterialTheme.typography.titleMedium)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    state.forecast!!.list.take(10).forEach { it ->
                        val desc = it.weather.firstOrNull()?.main ?: "—"
                        Text("${it.dtTxt}: ${"%.0f".format(it.main.tempMin)}°/${"%.0f".format(it.main.tempMax)}°  •  $desc  •  Hum ${it.main.humidity}%")
                    }
                }

                Button(
                    onClick = {
                        val text = buildString {
                            appendLine("Pronóstico — $cityName")
                            state.forecast!!.list.take(10).forEach { d ->
                                val dsc = d.weather.firstOrNull()?.main ?: "—"
                                appendLine("${d.dtTxt}: ${"%.0f".format(d.main.tempMin)}°/${"%.0f".format(d.main.tempMax)}° • $dsc • Hum ${d.main.humidity}%")
                            }
                        }
                        val share = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, text)
                        }
                        startActivity(Intent.createChooser(share, "Compartir pronóstico"))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Compartir pronóstico") }
            }
        }
    }
}
