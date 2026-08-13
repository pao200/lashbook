package com.lashbook.wearable.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

data class LoginWearableRequest(
    val correo: String,
    val password: String
)

data class UsuarioWearableResponse(
    val id: String,
    val nombre: String,
    val correo: String,
    val rol: String,
    val activo: Boolean,
    val creadoEn: String
)

data class LoginWearableResponse(
    val token: String,
    val tipoToken: String,
    val expiraEnSegundos: Long,
    val usuario: UsuarioWearableResponse
)

data class CitaWearableResponse(
    val id: String,
    val usuarioId: String,
    val nombreClienta: String,
    val servicioId: String,
    val nombreServicio: String,
    val fecha: String,
    val hora: String,
    val estado: String,
    val comentarios: String?,
    val recordatorioEnviado: Boolean,
    val creadoEn: String,
    val actualizadoEn: String
)

data class ActualizarEstadoWearableRequest(
    val estado: String
)

data class RegistrarDispositivoWearableRequest(
    val tokenFcm: String
)

interface LashBookApi {

    @POST("auth/login")
    suspend fun iniciarSesion(
        @Body
        request: LoginWearableRequest
    ): LoginWearableResponse

    @GET("wearable/citas/proxima")
    suspend fun obtenerProximaCita(
        @Header("Authorization")
        autorizacion: String
    ): CitaWearableResponse

    @PATCH("wearable/citas/{citaId}/estado")
    suspend fun cambiarEstadoCita(
        @Header("Authorization")
        autorizacion: String,

        @Path("citaId")
        citaId: String,

        @Body
        request: ActualizarEstadoWearableRequest
    ): CitaWearableResponse

    @POST("wearable/dispositivos")

    suspend fun registrarDispositivo(
        @Header("Authorization")
        autorizacion: String,

        @Body
        request: RegistrarDispositivoWearableRequest
    ): Response<Unit>
}