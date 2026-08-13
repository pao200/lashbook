package com.lashbook.service;

import com.lashbook.entity.Cita;
import com.lashbook.entity.DispositivoWearable;
import com.lashbook.entity.EstadoCita;
import com.lashbook.repository.CitaRepository;
import com.lashbook.repository.DispositivoWearableRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

@Service
public class PruebaNotificacionWearableService {

    private final CitaRepository citaRepository;

    private final DispositivoWearableRepository
            dispositivoWearableRepository;

    private final NotificacionWearableService
            notificacionWearableService;

    public PruebaNotificacionWearableService(
            CitaRepository citaRepository,
            DispositivoWearableRepository
                    dispositivoWearableRepository,
            NotificacionWearableService
                    notificacionWearableService
    ) {
        this.citaRepository =
                citaRepository;

        this.dispositivoWearableRepository =
                dispositivoWearableRepository;

        this.notificacionWearableService =
                notificacionWearableService;
    }

    @Transactional
    public int enviarNotificacionPrueba(
            UUID usuarioId
    ) {
        Cita proximaCita =
                citaRepository
                        .buscarProximasCitasWearable(
                                usuarioId,
                                Set.of(
                                        EstadoCita.PENDIENTE,
                                        EstadoCita.CONFIRMADA
                                ),
                                LocalDate.now(),
                                LocalTime.now()
                        )
                        .stream()
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new NoSuchElementException(
                                                "No tienes una próxima cita disponible"
                                        )
                        );

        List<DispositivoWearable> dispositivos =
                dispositivoWearableRepository
                        .findByUsuario_IdAndActivoTrue(
                                usuarioId
                        );

        if (dispositivos.isEmpty()) {
            throw new NoSuchElementException(
                    "No hay relojes activos registrados"
            );
        }

        int mensajesEnviados = 0;

        for (
                DispositivoWearable dispositivo :
                dispositivos
        ) {
            boolean enviado =
                    notificacionWearableService
                            .enviarRecordatorio(
                                    proximaCita,
                                    dispositivo
                                            .getTokenFcm()
                            );

            if (enviado) {
                mensajesEnviados++;
            }
        }

        if (mensajesEnviados == 0) {
            throw new IllegalStateException(
                    "Firebase no aceptó la notificación de prueba"
            );
        }

        return mensajesEnviados;
    }
}