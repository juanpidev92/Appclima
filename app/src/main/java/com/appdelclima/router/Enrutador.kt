
package com.appdelclima.router

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.appdelclima.presentation.ciudades.CiudadesPage
import com.appdelclima.presentation.clima.ClimaPage
import com.appdelclima.repository.Repositorio
import com.appdelclima.repository.modelos.GeoCityDto

@Composable
fun Enrutador(
    navController: androidx.navigation.NavHostController,
    startDestination: String,
    repo: Repositorio
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Ruta.Ciudades.path) {
            CiudadesPage(
                repo = repo,
                onElegida = { c -> navController.navigate(Ruta.Clima.build(c.name, c.lat, c.lon)) { popUpTo(Ruta.Ciudades.path) { inclusive = true } } },
                onGeo = { c -> navController.navigate(Ruta.Clima.build(c.name, c.lat, c.lon)) { popUpTo(Ruta.Ciudades.path) { inclusive = true } } }
            )
        }
        composable(
            route = Ruta.Clima.path,
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("lat") { type = NavType.StringType },
                navArgument("lon") { type = NavType.StringType }
            )
        ) { backStack ->
            val name = backStack.arguments?.getString("name").orEmpty()
            val lat = backStack.arguments?.getString("lat")!!.toDouble()
            val lon = backStack.arguments?.getString("lon")!!.toDouble()
            ClimaPage(repo = repo, nombre = name, lat = lat, lon = lon) {
                navController.navigate(Ruta.Ciudades.path) {
                    popUpTo(Ruta.Ciudades.path) { inclusive = true }
                }
            }
        }
    }
}
