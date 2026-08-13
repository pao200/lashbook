package com.lashbook.wearable.data

import android.content.Context
import com.lashbook.wearable.network.ActualizarEstadoWearableRequest
import com.lashbook.wearable.network.CitaWearableResponse
import com.lashbook.wearable.network.LashBookApi
import com.lashbook.wearable.network.LashBookApiClient
import com.lashbook.wearable.network.LoginWearableRequest
import com.lashbook.wearable.network.LoginWearableResponse
import com.lashbook.wearable.network.RegistrarDispositivoWearableRequest
import com.lashbook.wearable.security.ResultadoValidacionPin
import com.lashbook.wearable.security.SeguridadLocalStore

class WearableRepository(
    context: Context,
    private val api: LashBookApi =
        LashBookApiClient.api
) {

    private val seguridadLocalStore =
        SeguridadLocalStore(
            context.applicationContext
        )

    suspend fun iniciarSesion(
        correo: String,
        password: String
    ): LoginWearableResponse {
        val respuesta =
            api.iniciarSesion(
                LoginWearableRequest(
                    correo = correo.trim(),
                    password = password
                )
            )

        if (respuesta.usuario.rol != "CLIENTA") {
            throw IllegalArgumentException(
                "La aplicación del reloj solo permite cuentas de clienta"
            )
        }

        if (!respuesta.usuario.activo) {
            throw IllegalArgumentException(
                "La cuenta se encuentra desactivada"
            )
        }

        seguridadLocalStore.guardarToken(
            respuesta.token
        )

        return respuesta
    }

    suspend fun haySesionIniciada(): Boolean {
        return seguridadLocalStore
            .obtenerToken() != null
    }

    suspend fun crearPin(
        pin: String
    ) {
        seguridadLocalStore.guardarPin(pin)
    }

    suspend fun tienePin(): Boolean {
        return seguridadLocalStore.tienePin()
    }

    suspend fun validarPin(
        pin: String
    ): ResultadoValidacionPin {
        return seguridadLocalStore.validarPin(pin)
    }

    suspend fun segundosBloqueoRestantes(): Long {
        return seguridadLocalStore
            .segundosBloqueoRestantes()
    }

    suspend fun obtenerProximaCita():
        CitaWearableResponse {

        return api.obtenerProximaCita(
            autorizacion = obtenerAutorizacion()
        )
    }

    suspend fun cambiarEstadoCita(
        citaId: String,
        nuevoEstado: String
    ): CitaWearableResponse {

        val estadosPermitidos = setOf(
            "CONFIRMADA",
            "REAGENDAR",
            "CANCELADA"
        )

        require(
            nuevoEstado in estadosPermitidos
        ) {
            "La acción solicitada no está permitida"
        }

        return api.cambiarEstadoCita(
            autorizacion = obtenerAutorizacion(),
            citaId = citaId,
            request =
                ActualizarEstadoWearableRequest(
                    estado = nuevoEstado
                )
        )
    }

    suspend fun registrarTokenFirebase(
        tokenFcm: String
    ) {
        val respuesta =
            api.registrarDispositivo(
                autorizacion =
                    obtenerAutorizacion(),
                request =
                    RegistrarDispositivoWearableRequest(
                        tokenFcm = tokenFcm
                    )
            )

        if (!respuesta.isSuccessful) {
    throw IllegalStateException(
        "No fue posible registrar el reloj. HTTP ${respuesta.code()}"
    )
}
    }

    suspend fun cerrarSesion() {
        seguridadLocalStore.cerrarSesion()
    }

    private suspend fun obtenerAutorizacion():
        String {

        val token =
            seguridadLocalStore.obtenerToken()
                ?: throw IllegalStateException(
                    "Debes iniciar sesión en el reloj"
                )

        return "Bearer $token"
    }
}