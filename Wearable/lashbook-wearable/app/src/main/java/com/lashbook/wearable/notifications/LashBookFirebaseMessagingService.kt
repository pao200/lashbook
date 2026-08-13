package com.lashbook.wearable.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lashbook.wearable.R
import com.lashbook.wearable.presentation.MainActivity

object NotificacionCitaContract {

    const val EXTRA_ACCION =
        "lashbook_extra_accion"

    const val EXTRA_CITA_ID =
        "lashbook_extra_cita_id"

    const val ACCION_ABRIR =
        "ABRIR"

    const val ACCION_CONFIRMAR =
        "CONFIRMADA"

    const val ACCION_REAGENDAR =
        "REAGENDAR"

    const val ACCION_CANCELAR =
        "CANCELADA"
}

class LashBookFirebaseMessagingService :
    FirebaseMessagingService() {

    companion object {
        private const val TAG =
            "LashBookFCM"

        private const val CANAL_ID =
            "recordatorios_citas"

        private const val NOTIFICACION_ID_BASE =
            1001
    }

    override fun onNewToken(
        token: String
    ) {
        super.onNewToken(token)

        /*
         * No imprimimos el token en Logcat.
         * La aplicación lo registrará en el backend
         * después de que la clienta inicie sesión.
         */
        Log.d(
            TAG,
            "Se generó un nuevo token FCM"
        )
    }

    override fun onMessageReceived(
        mensaje: RemoteMessage
    ) {
        super.onMessageReceived(mensaje)

        val datos =
            mensaje.data

        val citaId =
            datos["citaId"]
                ?: datos["cita_id"]
                ?: ""

        val servicio =
            datos["servicio"]
                ?: datos["nombreServicio"]
                ?: "Tu servicio"

        val fecha =
            datos["fecha"]
                ?: ""

        val hora =
            datos["hora"]
                ?: ""

        val titulo =
            mensaje.notification?.title
                ?: datos["titulo"]
                ?: "Recordatorio de cita"

        val contenido =
            mensaje.notification?.body
                ?: datos["mensaje"]
                ?: crearContenidoPredeterminado(
                    servicio = servicio,
                    fecha = fecha,
                    hora = hora
                )

        mostrarNotificacion(
            titulo = titulo,
            contenido = contenido,
            citaId = citaId
        )
    }

    private fun mostrarNotificacion(
        titulo: String,
        contenido: String,
        citaId: String
    ) {
        crearCanalNotificaciones()

        if (
            Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w(
                TAG,
                "No se concedió el permiso de notificaciones"
            )

            return
        }

        val abrirApp =
            crearPendingIntent(
                citaId = citaId,
                accion =
                    NotificacionCitaContract
                        .ACCION_ABRIR
            )

        val constructor =
            NotificationCompat.Builder(
                this,
                CANAL_ID
            )
                .setSmallIcon(
                    R.drawable.ic_launcher_foreground
                )
                .setContentTitle(titulo)
                .setContentText(contenido)
                .setStyle(
                    NotificationCompat
                        .BigTextStyle()
                        .bigText(contenido)
                )
                .setContentIntent(abrirApp)
                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )
                .setCategory(
                    NotificationCompat.CATEGORY_REMINDER
                )
                .setAutoCancel(true)

        /*
         * Las acciones solo se muestran cuando Firebase
         * envía el identificador real de la cita.
         */
        if (citaId.isNotBlank()) {
            constructor
                .addAction(
                    R.drawable.ic_launcher_foreground,
                    "Confirmar",
                    crearPendingIntent(
                        citaId = citaId,
                        accion =
                            NotificacionCitaContract
                                .ACCION_CONFIRMAR
                    )
                )
                .addAction(
                    R.drawable.ic_launcher_foreground,
                    "Reagendar",
                    crearPendingIntent(
                        citaId = citaId,
                        accion =
                            NotificacionCitaContract
                                .ACCION_REAGENDAR
                    )
                )
                .addAction(
                    R.drawable.ic_launcher_foreground,
                    "Cancelar",
                    crearPendingIntent(
                        citaId = citaId,
                        accion =
                            NotificacionCitaContract
                                .ACCION_CANCELAR
                    )
                )
        }

        val notificacionId =
            if (citaId.isBlank()) {
                NOTIFICACION_ID_BASE
            } else {
                citaId.hashCode() and
                    Int.MAX_VALUE
            }

        NotificationManagerCompat
            .from(this)
            .notify(
                notificacionId,
                constructor.build()
            )
    }

    private fun crearPendingIntent(
        citaId: String,
        accion: String
    ): PendingIntent {
        val intent =
            Intent(
                this,
                MainActivity::class.java
            ).apply {
                flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP

                putExtra(
                    NotificacionCitaContract
                        .EXTRA_CITA_ID,
                    citaId
                )

                putExtra(
                    NotificacionCitaContract
                        .EXTRA_ACCION,
                    accion
                )
            }

        val codigoSolicitud =
            "$citaId-$accion"
                .hashCode()

        return PendingIntent.getActivity(
            this,
            codigoSolicitud,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun crearContenidoPredeterminado(
        servicio: String,
        fecha: String,
        hora: String
    ): String {
        val fechaHora =
            when {
                fecha.isNotBlank() &&
                    hora.isNotBlank() ->
                    "$fecha a las $hora"

                fecha.isNotBlank() ->
                    fecha

                hora.isNotBlank() ->
                    hora

                else ->
                    ""
            }

        return if (fechaHora.isBlank()) {
            "$servicio. Tu cita es en aproximadamente 24 horas."
        } else {
            "$servicio, $fechaHora. Tu cita es en aproximadamente 24 horas."
        }
    }

    private fun crearCanalNotificaciones() {
        if (
            Build.VERSION.SDK_INT <
                Build.VERSION_CODES.O
        ) {
            return
        }

        val canal =
            NotificationChannel(
                CANAL_ID,
                "Recordatorios de citas",
                NotificationManager
                    .IMPORTANCE_HIGH
            ).apply {
                description =
                    "Recordatorios enviados 24 horas antes de una cita"
            }

        val administrador =
            getSystemService(
                NotificationManager::class.java
            )

        administrador
            .createNotificationChannel(
                canal
            )
    }
}