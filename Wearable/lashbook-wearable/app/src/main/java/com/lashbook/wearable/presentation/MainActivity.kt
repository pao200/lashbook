package com.lashbook.wearable.presentation

import java.time.ZoneId
import android.content.Intent
import com.lashbook.wearable.notifications.NotificacionCitaContract
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Text
import com.google.firebase.messaging.FirebaseMessaging
import com.lashbook.wearable.network.CitaWearableResponse
import com.lashbook.wearable.presentation.theme.LashBookWearOSTheme
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val FondoOscuro =
    Color(0xFF2C2423)

private val CafePrincipal =
    Color(0xFF6E4E4A)

private val CafeOscuro =
    Color(0xFF4E3633)

private val RosaClaro =
    Color(0xFFEAD7D2)

private val RosaSuave =
    Color(0xFFF1E4E0)

private val Crema =
    Color(0xFFFFFDFC)

private val RojoSuave =
    Color(0xFF9A4E43)

private val VerdeSuave =
    Color(0xFF59705A)

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG =
            "LashBookWearable"
    }

    private val wearableViewModel:
        WearableViewModel by viewModels()

    private var tokenFirebase:
        String? by mutableStateOf(null)

    private val solicitarPermiso =
        registerForActivityResult(
            ActivityResultContracts
                .RequestPermission()
        ) { concedido ->

            Log.d(
                TAG,
                "Permiso de notificaciones: $concedido"
            )
        }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        solicitarPermisoNotificaciones()
        obtenerTokenFirebase()

        setContent {
            val estado =
                wearableViewModel.uiState

            LaunchedEffect(
                tokenFirebase,
                estado.pantalla
            ) {
                val token =
                    tokenFirebase

                val haySesion =
                    estado.pantalla ==
                        PantallaWearable.CREAR_PIN ||
                    estado.pantalla ==
                        PantallaWearable.DESBLOQUEAR ||
                    estado.pantalla ==
                        PantallaWearable.CITA

                if (
                    token != null &&
                    haySesion
                ) {
                    wearableViewModel
                        .registrarTokenFirebase(
                            token
                        )
                }
            }

            WearApp(
                viewModel =
                    wearableViewModel
            )
        }
        procesarIntentNotificacion(intent)
    }
    override fun onNewIntent(
    intent: Intent
) {
    super.onNewIntent(intent)

    setIntent(intent)

    procesarIntentNotificacion(intent)
}

private fun procesarIntentNotificacion(
    intent: Intent?
) {
    val accion =
        intent?.getStringExtra(
            NotificacionCitaContract
                .EXTRA_ACCION
        )

    val citaId =
        intent?.getStringExtra(
            NotificacionCitaContract
                .EXTRA_CITA_ID
        )

    wearableViewModel
        .prepararAccionNotificacion(
            accion = accion,
            citaId = citaId
        )

    intent?.removeExtra(
        NotificacionCitaContract
            .EXTRA_ACCION
    )

    intent?.removeExtra(
        NotificacionCitaContract
            .EXTRA_CITA_ID
    )
}

    private fun solicitarPermisoNotificaciones() {
        if (
            Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission
                    .POST_NOTIFICATIONS
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            solicitarPermiso.launch(
                Manifest.permission
                    .POST_NOTIFICATIONS
            )
        }
    }

    private fun obtenerTokenFirebase() {
        FirebaseMessaging
            .getInstance()
            .token
            .addOnCompleteListener { tarea ->

                if (!tarea.isSuccessful) {
                    Log.e(
                        TAG,
                        "No fue posible obtener el token FCM",
                        tarea.exception
                    )

                    return@addOnCompleteListener
                }

                /*
                 * El token no se muestra en pantalla.
                 * Se registra automáticamente después
                 * de iniciar sesión.
                 */
                tokenFirebase =
                    tarea.result

                Log.d(
                    TAG,
                    "Token FCM obtenido correctamente"
                )
            }
    }
}

@Composable
private fun WearApp(
    viewModel: WearableViewModel
) {
    val estado =
        viewModel.uiState

    LashBookWearOSTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoOscuro),
            contentAlignment =
                Alignment.Center
        ) {
            when (estado.pantalla) {
                PantallaWearable.CARGANDO -> {
                    PantallaCargando()
                }

                PantallaWearable.LOGIN -> {
                    PantallaLogin(
                        estado = estado,
                        viewModel = viewModel
                    )
                }

                PantallaWearable.CREAR_PIN -> {
                    PantallaCrearPin(
                        estado = estado,
                        viewModel = viewModel
                    )
                }

                PantallaWearable.DESBLOQUEAR -> {
                    PantallaDesbloquear(
                        estado = estado,
                        viewModel = viewModel
                    )
                }

                PantallaWearable.CITA -> {
                    PantallaCita(
                        estado = estado,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
private fun PantallaCargando() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp),
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {
        Text(
            text = "LashBook",
            color = Crema,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Cargando…",
            color = RosaClaro,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun PantallaLogin(
    estado: WearableUiState,
    viewModel: WearableViewModel
) {
    ContenidoDesplazable {
        Encabezado(
            titulo = "LashBook",
            subtitulo = "Iniciar sesión"
        )

        CampoWear(
            etiqueta = "Correo",
            valor = estado.correo,
            placeholder =
                "Correo",
            keyboardType =
                KeyboardType.Email,
            onValueChange =
                viewModel::actualizarCorreo
        )

        CampoWear(
            etiqueta = "Contraseña",
            valor = estado.password,
            placeholder = "Contraseña",
            keyboardType =
                KeyboardType.Password,
            ocultarContenido = true,
            onValueChange =
                viewModel::actualizarPassword
        )

        MensajeEstado(
            estado = estado
        )

        AccionWearButton(
            texto =
                if (estado.cargando) {
                    "Ingresando…"
                } else {
                    "Ingresar"
                },
            fondo = CafePrincipal,
            textoColor = Crema,
            habilitado =
                !estado.cargando,
            onClick =
                viewModel::iniciarSesion
        )
    }
}

@Composable
private fun PantallaCrearPin(
    estado: WearableUiState,
    viewModel: WearableViewModel
) {
    ContenidoDesplazable {
        Encabezado(
            titulo = "Crear NIP",
            subtitulo =
                "Protege las acciones del reloj"
        )

        CampoWear(
            etiqueta = "Nuevo NIP",
            valor = estado.pin,
            placeholder = "••••",
            keyboardType =
                KeyboardType.NumberPassword,
            ocultarContenido = true,
            onValueChange =
                viewModel::actualizarPin
        )

        CampoWear(
            etiqueta = "Confirmar NIP",
            valor =
                estado.confirmarPin,
            placeholder = "••••",
            keyboardType =
                KeyboardType.NumberPassword,
            ocultarContenido = true,
            onValueChange =
                viewModel::actualizarConfirmarPin
        )

        Text(
            text =
                "Debe contener exactamente 4 números",
            color = RosaClaro,
            fontSize = 9.sp,
            lineHeight = 11.sp,
            textAlign = TextAlign.Center,
            modifier =
                Modifier.fillMaxWidth()
        )

        MensajeEstado(
            estado = estado
        )

        AccionWearButton(
            texto =
                if (estado.cargando) {
                    "Guardando…"
                } else {
                    "Crear NIP"
                },
            fondo = CafePrincipal,
            textoColor = Crema,
            habilitado =
                !estado.cargando,
            onClick =
                viewModel::crearPin
        )

        AccionWearButton(
            texto = "Cerrar sesión",
            fondo = RosaSuave,
            textoColor = CafeOscuro,
            habilitado =
                !estado.cargando,
            onClick =
                viewModel::cerrarSesion
        )
    }
}

@Composable
private fun PantallaDesbloquear(
    estado: WearableUiState,
    viewModel: WearableViewModel
) {
    ContenidoDesplazable {
        Encabezado(
            titulo = "LashBook",
            subtitulo =
                "Ingresa tu NIP"
        )

        if (
            estado.segundosBloqueo > 0
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.82f)
                    .background(
                        color = RojoSuave,
                        shape =
                            RoundedCornerShape(
                                16.dp
                            )
                    )
                    .padding(
                        horizontal = 12.dp,
                        vertical = 12.dp
                    ),
                contentAlignment =
                    Alignment.Center
            ) {
                Text(
                    text =
                        "Bloqueado durante " +
                            "${estado.segundosBloqueo} s",
                    color = Crema,
                    fontSize = 12.sp,
                    fontWeight =
                        FontWeight.Bold,
                    textAlign =
                        TextAlign.Center
                )
            }
        } else {
            CampoWear(
                etiqueta = "NIP",
                valor = estado.pin,
                placeholder = "••••",
                keyboardType =
                    KeyboardType.NumberPassword,
                ocultarContenido = true,
                onValueChange =
                    viewModel::actualizarPin
            )
        }

        MensajeEstado(
            estado = estado
        )

        AccionWearButton(
            texto =
                if (estado.cargando) {
                    "Validando…"
                } else {
                    "Desbloquear"
                },
            fondo = CafePrincipal,
            textoColor = Crema,
            habilitado =
                !estado.cargando &&
                estado.segundosBloqueo <= 0,
            onClick =
                viewModel::validarPin
        )

        AccionWearButton(
            texto = "Cerrar sesión",
            fondo = RosaSuave,
            textoColor = CafeOscuro,
            habilitado =
                !estado.cargando,
            onClick =
                viewModel::cerrarSesion
        )
    }
}

@Composable
private fun PantallaCita(
    estado: WearableUiState,
    viewModel: WearableViewModel
) {
    ContenidoDesplazable {
        Encabezado(
            titulo = "LashBook",
            subtitulo = "Próxima cita"
        )

        MensajeEstado(
            estado = estado
        )

        if (
            estado.cargando &&
            estado.cita == null
        ) {
            Text(
                text =
                    "Consultando tu próxima cita…",
                color = RosaClaro,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier.fillMaxWidth()
            )
        } else if (
            estado.cita == null
        ) {
            Text(
                text =
                    "No hay una próxima cita disponible",
                color = Crema,
                fontSize = 12.sp,
                lineHeight = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth(0.82f)
                    .padding(vertical = 8.dp)
            )

            AccionWearButton(
                texto = "Volver a consultar",
                fondo = CafePrincipal,
                textoColor = Crema,
                habilitado =
                    !estado.cargando,
                onClick =
                    viewModel::cargarProximaCita
            )
        } else {
            TarjetaCita(
                cita = estado.cita
            )

            val estadoCita =
                estado.cita.estado

            val citaActiva =
                estadoCita == "PENDIENTE" ||
                    estadoCita == "CONFIRMADA"

            AccionWearButton(
                texto =
                    if (
                        estadoCita ==
                        "CONFIRMADA"
                    ) {
                        "Cita confirmada"
                    } else {
                        "Confirmar"
                    },
                fondo = VerdeSuave,
                textoColor = Crema,
                habilitado =
                    !estado.cargando &&
                    estadoCita ==
                        "PENDIENTE",
                onClick =
                    viewModel::confirmarCita
            )

            AccionWearButton(
                texto =
                    "Solicitar reagendar",
                fondo = RosaSuave,
                textoColor = CafeOscuro,
                habilitado =
                    !estado.cargando &&
                    citaActiva,
                onClick =
                    viewModel::solicitarReagendar
            )

            AccionWearButton(
                texto = "Cancelar cita",
                fondo = RojoSuave,
                textoColor = Crema,
                habilitado =
                    !estado.cargando &&
                    citaActiva,
                onClick =
                    viewModel::cancelarCita
            )

            AccionWearButton(
                texto = "Actualizar",
                fondo = CafePrincipal,
                textoColor = Crema,
                habilitado =
                    !estado.cargando,
                onClick =
                    viewModel::cargarProximaCita
            )
        }

        AccionWearButton(
            texto = "Cerrar sesión",
            fondo = Color.Transparent,
            textoColor = RosaClaro,
            habilitado =
                !estado.cargando,
            onClick =
                viewModel::cerrarSesion
        )
    }
}

@Composable
private fun TarjetaCita(
    cita: CitaWearableResponse
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.84f)
            .background(
                color = RosaClaro,
                shape =
                    RoundedCornerShape(
                        18.dp
                    )
            )
            .padding(
                horizontal = 12.dp,
                vertical = 10.dp
            ),
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {
        Text(
            text =
                formatearEstado(
                    cita.estado
                ),
            color =
                colorEstado(
                    cita.estado
                ),
            fontSize = 9.sp,
            lineHeight = 11.sp,
            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(3.dp)
        )

        Text(
            text = cita.nombreServicio,
            color = CafeOscuro,
            fontSize = 15.sp,
            lineHeight = 17.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Text(
            text =
                formatearFecha(
                    cita.fecha
                ),
            color = CafeOscuro,
            fontSize = 11.sp,
            lineHeight = 13.sp,
            textAlign = TextAlign.Center
        )

        Text(
            text =
                formatearHora(
                    cita.hora
                ),
            color = CafeOscuro,
            fontSize = 18.sp,
            lineHeight = 21.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(3.dp)
        )

        Text(
            text =
                calcularTiempoRestante(
                    fecha = cita.fecha,
                    hora = cita.hora
                ),
            color = CafePrincipal,
            fontSize = 9.sp,
            lineHeight = 11.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ContenidoDesplazable(
    contenido: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(
                start = 26.dp,
                end = 26.dp,
                top = 44.dp,
                bottom = 28.dp
            ),
        horizontalAlignment =
            Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.spacedBy(9.dp)
    ) {
        contenido()
    }
}

@Composable
private fun Encabezado(
    titulo: String,
    subtitulo: String
) {
    Text(
        text = titulo,
        color = Crema,
        fontSize = 18.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )

    Text(
        text = subtitulo,
        color = RosaClaro,
        fontSize = 10.sp,
        lineHeight = 12.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun CampoWear(
    etiqueta: String,
    valor: String,
    placeholder: String,
    keyboardType: KeyboardType,
    ocultarContenido: Boolean = false,
    onValueChange: (String) -> Unit
) {
    Column(
        modifier =
            Modifier.fillMaxWidth(0.84f)
    ) {
        Text(
            text = etiqueta,
            color = RosaClaro,
            fontSize = 9.sp,
            modifier =
                Modifier.padding(
                    start = 5.dp,
                    bottom = 3.dp
                )
        )

        BasicTextField(
            value = valor,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions =
                KeyboardOptions(
                    keyboardType =
                        keyboardType
                ),
            visualTransformation =
                if (ocultarContenido) {
                    PasswordVisualTransformation()
                } else {
                    VisualTransformation.None
                },
            cursorBrush =
                SolidColor(
                    CafePrincipal
                ),
            textStyle = TextStyle(
                color = CafeOscuro,
                fontSize = 12.sp,
                textAlign =
                    TextAlign.Center
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Crema,
                    shape =
                        RoundedCornerShape(
                            14.dp
                        )
                )
                .padding(
                    horizontal = 10.dp,
                    vertical = 10.dp
                ),
            decorationBox = { campoInterior ->
                Box(
                    modifier =
                        Modifier.fillMaxWidth(),
                    contentAlignment =
                        Alignment.Center
                ) {
                    if (valor.isEmpty()) {
                        Text(
                            text = placeholder,
                            color =
                                CafePrincipal.copy(
                                    alpha = 0.55f
                                ),
                            fontSize = 11.sp,
                            textAlign =
                                TextAlign.Center
                        )
                    }

                    campoInterior()
                }
            }
        )
    }
}

@Composable
private fun MensajeEstado(
    estado: WearableUiState
) {
    val texto =
        when {
            estado.error.isNotBlank() ->
                estado.error

            estado.mensaje.isNotBlank() ->
                estado.mensaje

            else -> ""
        }

    if (texto.isBlank()) {
        return
    }

    val color =
        if (estado.error.isNotBlank()) {
            RosaClaro
        } else {
            Crema
        }

    Text(
        text = texto,
        color = color,
        fontSize = 9.sp,
        lineHeight = 11.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth(0.86f)
            .background(
                color =
                    if (
                        estado.error.isNotBlank()
                    ) {
                        RojoSuave.copy(
                            alpha = 0.65f
                        )
                    } else {
                        VerdeSuave.copy(
                            alpha = 0.65f
                        )
                    },
                shape =
                    RoundedCornerShape(
                        12.dp
                    )
            )
            .padding(
                horizontal = 9.dp,
                vertical = 7.dp
            )
    )
}

@Composable
private fun AccionWearButton(
    texto: String,
    fondo: Color,
    textoColor: Color,
    habilitado: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = habilitado,
        modifier = Modifier
            .fillMaxWidth(0.84f)
            .height(42.dp)
            .alpha(
                if (habilitado) {
                    1f
                } else {
                    0.45f
                }
            ),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = fondo,
                contentColor = textoColor
            )
    ) {
        Text(
            text = texto,
            fontSize = 11.sp,
            lineHeight = 13.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

private fun formatearFecha(
    fecha: String
): String {
    return try {
        val locale =
            Locale("es", "MX")

        LocalDate
            .parse(fecha)
            .format(
                DateTimeFormatter.ofPattern(
                    "EEEE d 'de' MMMM",
                    locale
                )
            )
            .replaceFirstChar { caracter ->
                if (caracter.isLowerCase()) {
                    caracter.titlecase(locale)
                } else {
                    caracter.toString()
                }
            }
    } catch (_: Exception) {
        fecha
    }
}

private fun formatearHora(
    hora: String
): String {
    return try {
        LocalTime
            .parse(hora)
            .format(
                DateTimeFormatter.ofPattern(
                    "h:mm a",
                    Locale("es", "MX")
                )
            )
    } catch (_: Exception) {
        hora
    }
}

private fun formatearEstado(
    estado: String
): String {
    return when (estado) {
        "PENDIENTE" -> "PENDIENTE"
        "CONFIRMADA" -> "CONFIRMADA"
        "REAGENDAR" ->
            "REAGENDAMIENTO SOLICITADO"

        "CANCELADA" -> "CANCELADA"
        "COMPLETADA" -> "COMPLETADA"
        else -> estado
    }
}

private fun colorEstado(
    estado: String
): Color {
    return when (estado) {
        "CONFIRMADA" -> VerdeSuave
        "CANCELADA" -> RojoSuave
        "REAGENDAR" -> CafePrincipal
        else -> CafeOscuro
    }
}


private fun calcularTiempoRestante(
    fecha: String,
    hora: String
): String {
    return try {
        val zonaHoraria =
            ZoneId.of(
                "America/Mexico_City"
            )

        val ahora =
            LocalDateTime.now(
                zonaHoraria
            )

        val fechaHoraCita =
            LocalDateTime.of(
                LocalDate.parse(fecha),
                LocalTime.parse(hora)
            )

        val minutosTotales =
            Duration.between(
                ahora,
                fechaHoraCita
            ).toMinutes()

        if (minutosTotales <= 0) {
            return "La hora de la cita ya pasó"
        }

        val horasTotales =
            minutosTotales / 60

        val diasCompletos =
            horasTotales / 24

        when {
            diasCompletos > 1 ->
                "Faltan $diasCompletos días"

            diasCompletos == 1L ->
                "Falta 1 día"

            horasTotales > 1 ->
                "Faltan $horasTotales horas"

            horasTotales == 1L ->
                "Falta 1 hora"

            minutosTotales > 1 ->
                "Faltan $minutosTotales minutos"

            else ->
                "Comienza pronto"
        }
    } catch (_: Exception) {
        "Próxima cita"
    }
}
