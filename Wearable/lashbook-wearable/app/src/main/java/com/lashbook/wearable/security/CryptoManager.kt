package com.lashbook.wearable.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class CryptoManager {

    companion object {
        private const val ANDROID_KEYSTORE =
            "AndroidKeyStore"

        private const val CLAVE_ALIAS =
            "lashbook_wearable_key"

        private const val TRANSFORMACION =
            "AES/GCM/NoPadding"

        private const val TAMANO_TAG_GCM =
            128

        private const val SEPARADOR =
            ":"
    }

    private val keyStore: KeyStore =
        KeyStore.getInstance(
            ANDROID_KEYSTORE
        ).apply {
            load(null)
        }

    fun cifrar(
        texto: String
    ): String {
        val cipher =
            Cipher.getInstance(
                TRANSFORMACION
            )

        cipher.init(
            Cipher.ENCRYPT_MODE,
            obtenerOCrearClave()
        )

        val contenidoCifrado =
            cipher.doFinal(
                texto.toByteArray(
                    StandardCharsets.UTF_8
                )
            )

        val ivBase64 =
            Base64.encodeToString(
                cipher.iv,
                Base64.NO_WRAP
            )

        val contenidoBase64 =
            Base64.encodeToString(
                contenidoCifrado,
                Base64.NO_WRAP
            )

        return "$ivBase64$SEPARADOR$contenidoBase64"
    }

    fun descifrar(
        contenido: String
    ): String {
        val partes =
            contenido.split(
                SEPARADOR,
                limit = 2
            )

        require(partes.size == 2) {
            "El contenido cifrado no tiene un formato válido"
        }

        val iv =
            Base64.decode(
                partes[0],
                Base64.NO_WRAP
            )

        val datosCifrados =
            Base64.decode(
                partes[1],
                Base64.NO_WRAP
            )

        val cipher =
            Cipher.getInstance(
                TRANSFORMACION
            )

        cipher.init(
            Cipher.DECRYPT_MODE,
            obtenerOCrearClave(),
            GCMParameterSpec(
                TAMANO_TAG_GCM,
                iv
            )
        )

        val datosDescifrados =
            cipher.doFinal(
                datosCifrados
            )

        return String(
            datosDescifrados,
            StandardCharsets.UTF_8
        )
    }

    private fun obtenerOCrearClave(): SecretKey {
        val claveExistente =
            keyStore.getKey(
                CLAVE_ALIAS,
                null
            ) as? SecretKey

        if (claveExistente != null) {
            return claveExistente
        }

        val generador =
            KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEYSTORE
            )

        val configuracion =
            KeyGenParameterSpec.Builder(
                CLAVE_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or
                    KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(
                    KeyProperties.BLOCK_MODE_GCM
                )
                .setEncryptionPaddings(
                    KeyProperties.ENCRYPTION_PADDING_NONE
                )
                .setKeySize(256)
                .build()

        generador.init(configuracion)

        return generador.generateKey()
    }
}