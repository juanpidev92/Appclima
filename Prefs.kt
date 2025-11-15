package com.ejemplo.clima

import android.content.Context

class Prefs(context: Context) {
    private val sp = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    fun getSavedCity(): SavedCity? {
        val name = sp.getString("name", null) ?: return null
        val lat = sp.getString("lat", null)?.toDoubleOrNull() ?: return null
        val lon = sp.getString("lon", null)?.toDoubleOrNull() ?: return null
        return SavedCity(name, lat, lon)
    }

    fun saveCity(name: String, lat: Double, lon: Double) {
        sp.edit()
            .putString("name", name)
            .putString("lat", lat.toString())
            .putString("lon", lon.toString())
            .apply()
    }
}

data class SavedCity(val name: String, val lat: Double, val lon: Double)
