package com.lashbook.wearable.security

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import kotlin.math.ceil

private val Context.dataStore by preferencesDataStore(
    name = "lashbook_seguridad"
)

enum class EstadoValidacionPin {
    CORRECTO,
    INCORRECTO,
    BLOQUEADO,
    SIN_PIN
}

data class ResultadoValidacionPin(
    val estado: EstadoValidacionPin,
    val intentosRestantes: Int = 0,
    val segundosBloqueo: Long = 0
)

class SeguridadLocalStore(
    private val context: Context,
    private val cryptoManager: CryptoManager =
        CryptoManager()
) {

    companion object {
        private const val MAXIMO_INTENTOS = 3
        private const val DURACION_BLOQUEO_MS = 30_000L

        private const val ITERACIONES_PIN = 120_000
        private const val LONGITUD_CLAVE_BITS = 256
        private const val LONGITUD_SALT_BYTES = 16

        private val TOKEN_CIFRADO =
            stringPreferencesKey("token_cifrado")

        private val PIN_HASH =
            stringPreferencesKey("pin_hash")

        private val PIN_SALT =
            stringPreferencesKey("pin_salt")

        private val INTENTOS_FALLIDOS =
            intPreferencesKey("intentos_fallidos")

        private val BLOQUEADO_HASTA =
            longPreferencesKey("bloqueado_hasta")
    }

    suspend fun guardarToken(
        token: String
    ) {
        val tokenCifrado =
            cryptoManager.cifrar(token)

        context.dataStore.edit { preferencias ->
            preferencias[TOKEN_CIFRADO] =
                tokenCifrado
        }
    }

    suspend fun obtenerToken(): String? {
        val preferencias =
            context.dataStore.data.first()

        val tokenCifrado =
            preferencias[TOKEN_CIFRADO]
                ?: return null

        return try {
            cryptoManager.descifrar(
                tokenCifrado
            )
        } catch (_: Exception) {
            null
        }
    }

    suspend fun guardarPin(
        pin: String
    ) {
        validarFormatoPin(pin)

        val salt = ByteArray(
            LONGITUD_SALT_BYTES
        )

        SecureRandom().nextBytes(salt)

        val hash =
            calcularHashPin(
                pin = pin,
                salt = salt
            )

        val saltBase64 =
            Base64.encodeToString(
                salt,
                Base64.NO_WRAP
            )

        val hashBase64 =
            Base64.encodeToString(
                hash,
                Base64.NO_WRAP
            )

        context.dataStore.edit { preferencias ->
            preferencias[PIN_SALT] =
                saltBase64

            preferencias[PIN_HASH] =
                hashBase64

            preferencias[INTENTOS_FALLIDOS] = 0
            preferencias[BLOQUEADO_HASTA] = 0L
        }
    }

    suspend fun tienePin(): Boolean {
        val preferencias =
            context.dataStore.data.first()

        return preferencias[PIN_HASH] != null &&
            preferencias[PIN_SALT] != null
    }

    suspend fun validarPin(
        pin: String
    ): ResultadoValidacionPin {
        if (!pin.matches(Regex("\\d{4}"))) {
            return registrarIntentoIncorrecto()
        }

        val preferencias =
            context.dataStore.data.first()

        val hashGuardadoBase64 =
            preferencias[PIN_HASH]
                ?: return ResultadoValidacionPin(
                    estado =
                        EstadoValidacionPin.SIN_PIN
                )

        val saltGuardadoBase64 =
            preferencias[PIN_SALT]
                ?: return ResultadoValidacionPin(
                    estado =
                        EstadoValidacionPin.SIN_PIN
                )

        val ahora =
            System.currentTimeMillis()

        val bloqueadoHasta =
            preferencias[BLOQUEADO_HASTA]
                ?: 0L

        if (bloqueadoHasta > ahora) {
            return ResultadoValidacionPin(
                estado =
                    EstadoValidacionPin.BLOQUEADO,
                segundosBloqueo =
                    calcularSegundosRestantes(
                        bloqueadoHasta,
                        ahora
                    )
            )
        }

        if (bloqueadoHasta != 0L) {
            limpiarBloqueoFinalizado()
        }

        val salt =
            Base64.decode(
                saltGuardadoBase64,
                Base64.NO_WRAP
            )

        val hashGuardado =
            Base64.decode(
                hashGuardadoBase64,
                Base64.NO_WRAP
            )

        val hashIngresado =
            calcularHashPin(
                pin = pin,
                salt = salt
            )

        val esCorrecto =
            MessageDigest.isEqual(
                hashGuardado,
                hashIngresado
            )

        if (esCorrecto) {
            reiniciarSeguridadPin()

            return ResultadoValidacionPin(
                estado =
                    EstadoValidacionPin.CORRECTO,
                intentosRestantes =
                    MAXIMO_INTENTOS
            )
        }

        return registrarIntentoIncorrecto()
    }

    suspend fun segundosBloqueoRestantes(): Long {
        val preferencias =
            context.dataStore.data.first()

        val bloqueadoHasta =
            preferencias[BLOQUEADO_HASTA]
                ?: 0L

        val ahora =
            System.currentTimeMillis()

        if (bloqueadoHasta <= ahora) {
            limpiarBloqueoFinalizado()
            return 0L
        }

        return calcularSegundosRestantes(
            bloqueadoHasta,
            ahora
        )
    }

    suspend fun cerrarSesion() {
        context.dataStore.edit { preferencias ->
            preferencias.clear()
        }
    }

    private suspend fun registrarIntentoIncorrecto():
        ResultadoValidacionPin {

        val preferencias =
            context.dataStore.data.first()

        val ahora =
            System.currentTimeMillis()

        val bloqueadoHasta =
            preferencias[BLOQUEADO_HASTA]
                ?: 0L

        if (bloqueadoHasta > ahora) {
            return ResultadoValidacionPin(
                estado =
                    EstadoValidacionPin.BLOQUEADO,
                segundosBloqueo =
                    calcularSegundosRestantes(
                        bloqueadoHasta,
                        ahora
                    )
            )
        }

        val intentosActuales =
            preferencias[INTENTOS_FALLIDOS]
                ?: 0

        val nuevosIntentos =
            intentosActuales + 1

        if (nuevosIntentos >= MAXIMO_INTENTOS) {
            val nuevoBloqueo =
                ahora + DURACION_BLOQUEO_MS

            context.dataStore.edit { datos ->
                datos[INTENTOS_FALLIDOS] = 0
                datos[BLOQUEADO_HASTA] =
                    nuevoBloqueo
            }

            return ResultadoValidacionPin(
                estado =
                    EstadoValidacionPin.BLOQUEADO,
                segundosBloqueo = 30
            )
        }

        context.dataStore.edit { datos ->
            datos[INTENTOS_FALLIDOS] =
                nuevosIntentos
        }

        return ResultadoValidacionPin(
            estado =
                EstadoValidacionPin.INCORRECTO,
            intentosRestantes =
                MAXIMO_INTENTOS - nuevosIntentos
        )
    }

    private suspend fun reiniciarSeguridadPin() {
        context.dataStore.edit { preferencias ->
            preferencias[INTENTOS_FALLIDOS] = 0
            preferencias[BLOQUEADO_HASTA] = 0L
        }
    }

    private suspend fun limpiarBloqueoFinalizado() {
        context.dataStore.edit { preferencias ->
            preferencias[INTENTOS_FALLIDOS] = 0
            preferencias[BLOQUEADO_HASTA] = 0L
        }
    }

    private fun calcularHashPin(
        pin: String,
        salt: ByteArray
    ): ByteArray {
        val especificacion =
            PBEKeySpec(
                pin.toCharArray(),
                salt,
                ITERACIONES_PIN,
                LONGITUD_CLAVE_BITS
            )

        return try {
            SecretKeyFactory
                .getInstance(
                    "PBKDF2WithHmacSHA256"
                )
                .generateSecret(especificacion)
                .encoded
        } finally {
            especificacion.clearPassword()
        }
    }

    private fun validarFormatoPin(
        pin: String
    ) {
        require(
            pin.matches(
                Regex("\\d{4}")
            )
        ) {
            "El NIP debe contener exactamente 4 dígitos"
        }
    }

    private fun calcularSegundosRestantes(
        bloqueadoHasta: Long,
        ahora: Long
    ): Long {
        val milisegundosRestantes =
            bloqueadoHasta - ahora

        return ceil(
            milisegundosRestantes / 1000.0
        )
            .toLong()
            .coerceAtLeast(0L)
    }
}