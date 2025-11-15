
package com.appdelclima

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.appdelclima.repository.Repositorio
import com.appdelclima.repository.RepositorioApi
import com.appdelclima.repository.RepositorioMock
import com.appdelclima.router.Enrutador
import com.appdelclima.router.Ruta

@Composable
fun MainPage(apiKey: String, usarMock: Boolean = false) {
    val nav = rememberNavController()
    val repo: Repositorio = remember { if (usarMock) RepositorioMock() else RepositorioApi(apiKey) }
    val start = Ruta.Ciudades.path
    Enrutador(navController = nav, startDestination = start, repo = repo)
}
