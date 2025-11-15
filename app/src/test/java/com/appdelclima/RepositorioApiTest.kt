
package com.appdelclima

import com.appdelclima.repository.RepositorioApi
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class RepositorioApiTest {
    @Test
    fun buscarCiudades_parseaLista() = runBlocking {
        val client = HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    respond(
                        content = "[{\"name\":\"Buenos Aires\",\"country\":\"AR\",\"lat\":-34.6,\"lon\":-58.38}]",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
            }
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
        val repo = RepositorioApi("FAKE", client)
        val r = repo.buscarCiudades("bue")
        assertEquals(1, r.size)
        assertEquals("AR", r.first().country)
    }
}
