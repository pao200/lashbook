package com.lashbook.wearable.presentation

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lashbook.wearable.data.WearableRepository
import com.lashbook.wearable.network.CitaWearableResponse
import com.lashbook.wearable.security.EstadoValidacionPin
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import android.util.Log

enum class PantallaWearable {
    CARGANDO,
    LOGIN,
    CREAR_PIN,
    DESBLOQUEAR,
    CITA
}

data class WearableUiState(
    val pantalla: PantallaWearable =
        PantallaWearable.CARGANDO,

    val correo: String = "",
    val password: String = "",

    val pin: String = "",
    val confirmarPin: String = "",

    val cita: CitaWearableResponse? = null,

    val cargando: Boolean = false,
    val mensaje: String = "",
    val error: String = "",

    val segundosBloqueo: Long = 0
)

class WearableViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        WearableRepository(application)

    private var bloqueoJob: Job? = null
    private var accionNotificacionPendiente:
    String? = null

private var citaNotificacionPendiente:
    String? = null

    var uiState by mutableStateOf(
        WearableUiState()
    )
        private set

    init {
        inicializar()
    }

 fun prepararAccionNotificacion(
    accion: String?,
    citaId: String?
) {
    if (accion == "ABRIR") {
    uiState = uiState.copy(
        pantalla = PantallaWearable.CITA,
        cargando = false,
        error = "",
        mensaje = ""
    )

    cargarProximaCita()
    return
}
    val accionesPermitidas =
        setOf(
            "CONFIRMADA",
            "REAGENDAR",
            "CANCELADA"
        )

    if (
        accion !in accionesPermitidas ||
        citaId.isNullOrBlank()
    ) {
        return
    }

    accionNotificacionPendiente =
        accion

    citaNotificacionPendiente =
        citaId

    val mensajeAccion =
        when (accion) {
            "CONFIRMADA" ->
                "Ingresa tu NIP para confirmar"

            "REAGENDAR" ->
                "Ingresa tu NIP para solicitar reagendar"

            "CANCELADA" ->
                "Ingresa tu NIP para cancelar"

            else ->
                "Ingresa tu NIP para continuar"
        }

    /*
     * Toda acción procedente de una notificación
     * obliga a bloquear nuevamente la aplicación.
     */
    uiState = uiState.copy(
        pantalla =
            PantallaWearable.DESBLOQUEAR,
        pin = "",
        cargando = false,
        error = "",
        mensaje = mensajeAccion
    )

    viewModelScope.launch {
        revisarBloqueoActual()
    }
}

    fun actualizarCorreo(
        correo: String
    ) {
        uiState = uiState.copy(
            correo = correo,
            error = ""
        )
    }

    fun actualizarPassword(
        password: String
    ) {
        uiState = uiState.copy(
            password = password,
            error = ""
        )
    }

    fun actualizarPin(
        pin: String
    ) {
        if (
            pin.length <= 4 &&
            pin.all { caracter ->
                caracter.isDigit()
            }
        ) {
            uiState = uiState.copy(
                pin = pin,
                error = ""
            )
        }
    }

    fun actualizarConfirmarPin(
        pin: String
    ) {
        if (
            pin.length <= 4 &&
            pin.all { caracter ->
                caracter.isDigit()
            }
        ) {
            uiState = uiState.copy(
                confirmarPin = pin,
                error = ""
            )
        }
    }

    fun iniciarSesion() {
        val correo =
            uiState.correo.trim()

        val password =
            uiState.password

        if (
            correo.isBlank() ||
            password.isBlank()
        ) {
            uiState = uiState.copy(
                error =
                    "Ingresa tu correo y contraseña"
            )

            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(
                cargando = true,
                error = "",
                mensaje = ""
            )

            try {
                repository.iniciarSesion(
                    correo = correo,
                    password = password
                )

                val tienePin =
                    repository.tienePin()

                uiState = uiState.copy(
                    pantalla =
                        if (tienePin) {
                            PantallaWearable.DESBLOQUEAR
                        } else {
                            PantallaWearable.CREAR_PIN
                        },
                    password = "",
                    pin = "",
                    confirmarPin = "",
                    cargando = false,
                    mensaje =
                        "Sesión iniciada correctamente"
                )

                if (tienePin) {
                    revisarBloqueoActual()
                }
            } catch (error: Exception) {
                uiState = uiState.copy(
                    cargando = false,
                    error =
                        error.message
                            ?: "No fue posible iniciar sesión"
                )
            }
        }
    }

    fun crearPin() {
        val pin =
            uiState.pin

        val confirmarPin =
            uiState.confirmarPin

        if (!pin.matches(Regex("\\d{4}"))) {
            uiState = uiState.copy(
                error =
                    "El NIP debe tener 4 dígitos"
            )

            return
        }

        if (pin != confirmarPin) {
            uiState = uiState.copy(
                error =
                    "Los NIP no coinciden"
            )

            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(
                cargando = true,
                error = "",
                mensaje = ""
            )

            try {
                repository.crearPin(pin)

                uiState = uiState.copy(
                    pantalla =
                        PantallaWearable.CITA,
                    pin = "",
                    confirmarPin = "",
                    cargando = false,
                    mensaje =
                        "NIP creado correctamente"
                )

                cargarProximaCita()
            } catch (error: Exception) {
                uiState = uiState.copy(
                    cargando = false,
                    error =
                        error.message
                            ?: "No fue posible crear el NIP"
                )
            }
        }
    }

    fun validarPin() {
        if (uiState.segundosBloqueo > 0) {
            return
        }

        val pin =
            uiState.pin

        if (!pin.matches(Regex("\\d{4}"))) {
            uiState = uiState.copy(
                error =
                    "Ingresa los 4 dígitos del NIP"
            )

            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(
                cargando = true,
                error = "",
                mensaje = ""
            )

            try {
                val resultado =
                    repository.validarPin(pin)

                when (resultado.estado) {
                   EstadoValidacionPin.CORRECTO -> {
    val accionPendiente =
        accionNotificacionPendiente

    val citaIdPendiente =
        citaNotificacionPendiente

    if (
        accionPendiente != null &&
        citaIdPendiente != null
    ) {
        ejecutarAccionNotificacionPendiente(
            accion = accionPendiente,
            citaId = citaIdPendiente
        )
    } else {
        uiState = uiState.copy(
            pantalla =
                PantallaWearable.CITA,
            pin = "",
            cargando = false,
            error = "",
            mensaje = "NIP correcto"
        )

        cargarProximaCita()
    }
}

                    EstadoValidacionPin.INCORRECTO -> {
                        uiState = uiState.copy(
                            pin = "",
                            cargando = false,
                            error =
                                crearMensajeIntentos(
                                    resultado
                                        .intentosRestantes
                                )
                        )
                    }

                    EstadoValidacionPin.BLOQUEADO -> {
                        uiState = uiState.copy(
                            pin = "",
                            cargando = false,
                            error =
                                "Demasiados intentos incorrectos",
                            segundosBloqueo =
                                resultado
                                    .segundosBloqueo
                        )

                        iniciarCuentaRegresiva()
                    }

                    EstadoValidacionPin.SIN_PIN -> {
                        uiState = uiState.copy(
                            pantalla =
                                PantallaWearable.CREAR_PIN,
                            pin = "",
                            cargando = false,
                            error =
                                "Primero debes crear un NIP"
                        )
                    }
                }
            } catch (error: Exception) {
                uiState = uiState.copy(
                    cargando = false,
                    error =
                        error.message
                            ?: "No fue posible validar el NIP"
                )
            }
        }
    }

    fun cargarProximaCita() {
    viewModelScope.launch {
        uiState = uiState.copy(
            cargando = true,
            error = "",
            mensaje = ""
        )

        try {
            val cita =
                repository.obtenerProximaCita()

            uiState = uiState.copy(
                pantalla =
                    PantallaWearable.CITA,
                cita = cita,
                cargando = false,
                error = ""
            )
        } catch (error: Exception) {
            val sesionInvalida =
                error is HttpException &&
                    error.code() == 401 ||
                error.message
                    ?.contains(
                        "Debes iniciar sesión",
                        ignoreCase = true
                    ) == true

            if (sesionInvalida) {
                uiState = uiState.copy(
                    pantalla =
                        PantallaWearable.LOGIN,
                    cita = null,
                    cargando = false,
                    password = "",
                    pin = "",
                    error =
                        "Tu sesión venció. Inicia sesión nuevamente",
                    mensaje = ""
                )
            } else {
                uiState = uiState.copy(
                    cita = null,
                    cargando = false,
                    error =
                        "No tienes próximas citas disponibles"
                )
            }
        }
    }
}
    fun confirmarCita() {
        cambiarEstadoCita(
            nuevoEstado = "CONFIRMADA",
            mensajeExito =
                "Cita confirmada correctamente"
        )
    }

    fun solicitarReagendar() {
        cambiarEstadoCita(
            nuevoEstado = "REAGENDAR",
            mensajeExito =
                "Solicitud de reagendamiento enviada"
        )
    }

    fun cancelarCita() {
        cambiarEstadoCita(
            nuevoEstado = "CANCELADA",
            mensajeExito =
                "Cita cancelada correctamente"
        )
    }

    fun registrarTokenFirebase(
    tokenFcm: String
) {
    viewModelScope.launch {
        try {
            repository.registrarTokenFirebase(
                tokenFcm
            )

            Log.d(
                "LashBookFCM",
                "Token FCM registrado correctamente en el backend"
            )
        } catch (error: Exception) {
            Log.e(
                "LashBookFCM",
                "Falló el registro del token FCM",
                error
            )
        }
    }
}

    fun cerrarSesion() {
        viewModelScope.launch {
            bloqueoJob?.cancel()

            repository.cerrarSesion()

            uiState = WearableUiState(
                pantalla =
                    PantallaWearable.LOGIN
            )
        }
    }

    private fun inicializar() {
        viewModelScope.launch {
            try {
                val haySesion =
                    repository.haySesionIniciada()

                if (!haySesion) {
                    uiState = WearableUiState(
                        pantalla =
                            PantallaWearable.LOGIN
                    )

                    return@launch
                }

                val tienePin =
                    repository.tienePin()

                if (!tienePin) {
                    uiState = WearableUiState(
                        pantalla =
                            PantallaWearable.CREAR_PIN
                    )

                    return@launch
                }

                uiState = WearableUiState(
                    pantalla =
                        PantallaWearable.DESBLOQUEAR
                )

                revisarBloqueoActual()
            } catch (_: Exception) {
                uiState = WearableUiState(
                    pantalla =
                        PantallaWearable.LOGIN,
                    error =
                        "Debes iniciar sesión nuevamente"
                )
            }
        }
    }

    private fun cambiarEstadoCita(
        nuevoEstado: String,
        mensajeExito: String
    ) {
        val citaId =
            uiState.cita?.id

        if (citaId == null) {
            uiState = uiState.copy(
                error =
                    "No hay una cita disponible"
            )

            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(
                cargando = true,
                error = "",
                mensaje = ""
            )

            try {
                val citaActualizada =
                    repository.cambiarEstadoCita(
                        citaId = citaId,
                        nuevoEstado =
                            nuevoEstado
                    )

                uiState = uiState.copy(
                    cita = citaActualizada,
                    cargando = false,
                    mensaje = mensajeExito
                )
            } catch (error: Exception) {
                uiState = uiState.copy(
                    cargando = false,
                    error =
                        error.message
                            ?: "No fue posible actualizar la cita"
                )
            }
        }
    }
    private suspend fun ejecutarAccionNotificacionPendiente(
    accion: String,
    citaId: String
) {
    accionNotificacionPendiente = null
    citaNotificacionPendiente = null

    uiState = uiState.copy(
        pantalla =
            PantallaWearable.CITA,
        pin = "",
        cargando = true,
        error = "",
        mensaje = ""
    )

    try {
        val citaActualizada =
            repository.cambiarEstadoCita(
                citaId = citaId,
                nuevoEstado = accion
            )

        val mensajeExito =
            when (accion) {
                "CONFIRMADA" ->
                    "Cita confirmada correctamente"

                "REAGENDAR" ->
                    "Solicitud de reagendamiento enviada"

                "CANCELADA" ->
                    "Cita cancelada correctamente"

                else ->
                    "Cita actualizada correctamente"
            }

        uiState = uiState.copy(
            cita = citaActualizada,
            cargando = false,
            mensaje = mensajeExito,
            error = ""
        )
    } catch (error: Exception) {
        uiState = uiState.copy(
            cargando = false,
            error =
                error.message
                    ?: "No fue posible realizar la acción"
        )
    }
}

    private suspend fun revisarBloqueoActual() {
        val segundos =
            repository
                .segundosBloqueoRestantes()

        uiState = uiState.copy(
            segundosBloqueo = segundos
        )

        if (segundos > 0) {
            iniciarCuentaRegresiva()
        }
    }

    private fun iniciarCuentaRegresiva() {
        bloqueoJob?.cancel()

        bloqueoJob =
            viewModelScope.launch {
                while (true) {
                    val segundos =
                        repository
                            .segundosBloqueoRestantes()

                    uiState = uiState.copy(
                        segundosBloqueo =
                            segundos
                    )

                    if (segundos <= 0) {
                        uiState = uiState.copy(
                            error = "",
                            mensaje =
                                "Ya puedes intentarlo nuevamente"
                        )

                        break
                    }

                    delay(1_000L)
                }
            }
    }

    private fun crearMensajeIntentos(
        intentosRestantes: Int
    ): String {
        return if (intentosRestantes == 1) {
            "NIP incorrecto. Te queda 1 intento"
        } else {
            "NIP incorrecto. Te quedan $intentosRestantes intentos"
        }
    }
}