package com.lashbook.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.lashbook.entity.Cita;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class NotificacionWearableService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    NotificacionWearableService.class
            );

    private static final Locale LOCALE_MEXICO =
            Locale.forLanguageTag("es-MX");

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern(
                    "EEEE d 'de' MMMM",
                    LOCALE_MEXICO
            );

    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern(
                    "h:mm a",
                    LOCALE_MEXICO
            );

    private final FirebaseMessaging firebaseMessaging;

    public NotificacionWearableService(
            FirebaseApp firebaseApp
    ) {
        this.firebaseMessaging =
                FirebaseMessaging.getInstance(
                        firebaseApp
                );
    }

    public boolean enviarRecordatorio(
            Cita cita,
            String tokenFcm
    ) {
        if (cita == null) {
            throw new IllegalArgumentException(
                    "La cita es obligatoria"
            );
        }

        if (
                tokenFcm == null ||
                tokenFcm.isBlank()
        ) {
            throw new IllegalArgumentException(
                    "El token FCM es obligatorio"
            );
        }

        String nombreServicio =
                cita.getServicio().getNombre();

        String fecha =
                cita.getFecha().toString();

        String hora =
                cita.getHora().toString();

        String fechaFormateada =
                cita.getFecha()
                        .format(FORMATO_FECHA);

        String horaFormateada =
                cita.getHora()
                        .format(FORMATO_HORA);

        String mensajeNotificacion =
                nombreServicio +
                " · " +
                capitalizar(fechaFormateada) +
                " · " +
                horaFormateada +
                ". Tu cita es en aproximadamente 24 horas.";

        Message mensaje =
                Message.builder()
                        .setToken(tokenFcm)
                        .putData(
                                "titulo",
                                "Recordatorio de cita"
                        )
                        .putData(
                                "mensaje",
                                mensajeNotificacion
                        )
                        .putData(
                                "citaId",
                                cita.getId().toString()
                        )
                        .putData(
                                "servicio",
                                nombreServicio
                        )
                        .putData(
                                "fecha",
                                fecha
                        )
                        .putData(
                                "hora",
                                hora
                        )
                        .setAndroidConfig(
                                AndroidConfig.builder()
                                        .setPriority(
                                                AndroidConfig
                                                        .Priority
                                                        .HIGH
                                        )
                                        .build()
                        )
                        .build();

        try {
            String identificadorMensaje =
                    firebaseMessaging.send(
                            mensaje
                    );

            LOGGER.info(
                    "Recordatorio enviado para la cita {}. Mensaje FCM: {}",
                    cita.getId(),
                    identificadorMensaje
            );

            return true;
        } catch (
                FirebaseMessagingException error
        ) {
            LOGGER.error(
                    "No fue posible enviar el recordatorio de la cita {}",
                    cita.getId(),
                    error
            );

            return false;
        }
    }

    private String capitalizar(
            String texto
    ) {
        if (
                texto == null ||
                texto.isBlank()
        ) {
            return "";
        }

        return texto.substring(0, 1)
                .toUpperCase(LOCALE_MEXICO) +
                texto.substring(1);
    }
}