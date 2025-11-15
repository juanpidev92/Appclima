
package com.appdelclima.router

sealed class Ruta(val path: String) {
    data object Ciudades : Ruta("ciudades")
    data object Clima : Ruta("clima/{name}/{lat}/{lon}") {
        fun build(name: String, lat: Double, lon: Double) = "clima/$name/$lat/$lon"
    }
}
