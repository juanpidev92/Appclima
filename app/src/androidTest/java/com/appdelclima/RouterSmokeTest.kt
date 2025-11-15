
package com.appdelclima

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test
import com.appdelclima.repository.RepositorioMock
import com.appdelclima.router.Enrutador
import androidx.navigation.compose.rememberNavController

class RouterSmokeTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun cargaNavHost() {
        compose.setContent {
            val nav = rememberNavController()
            Enrutador(navController = nav, startDestination = "ciudades", repo = RepositorioMock())
        }
    }
}
