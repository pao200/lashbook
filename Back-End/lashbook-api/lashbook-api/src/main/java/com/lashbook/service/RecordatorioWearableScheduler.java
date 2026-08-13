package com.lashbook.service;

import com.lashbook.entity.Cita;
import com.lashbook.entity.DispositivoWearable;
import com.lashbook.entity.EstadoCita;
import com.lashbook.repository.CitaRepository;
import com.lashbook.repository.DispositivoWearableRepository;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
public class RecordatorioWearableScheduler {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    RecordatorioWearableScheduler.class
            );

    private final CitaRepository citaRepository;

    private final DispositivoWearableRepository
            dispositivoRepository;

    private final NotificacionWearableService
            notificacionService;

    /*
     * false = funcionamiento real:
     *         recordatorio hasta 24 horas antes.
     *
     * true = modo demostración:
     *        recordatorio pocos minutos antes.
     */
    @Value("${lashbook.wearable.modo-demo:false}")
    private boolean modoDemo;

    /*
     * Cantidad de minutos de anticipación
     * utilizada únicamente cuando
     * modo-demo=true.
     */
    @Value("${lashbook.wearable.anticipacion-demo-minutos:5}")
    private long anticipacionDemoMinutos;
    @Value("${lashbook.zona-horaria:America/Mexico_City}")
    private String zonaHoraria;

    public RecordatorioWearableScheduler(
            CitaRepository citaRepository,
            DispositivoWearableRepository
                    dispositivoRepository,
            NotificacionWearableService
                    notificacionService
    ) {
        this.citaRepository =
                citaRepository;

        this.dispositivoRepository =
                dispositivoRepository;

        this.notificacionService =
                notificacionService;
    }

    /*
     * Espera 15 segundos después del arranque
     * y posteriormente revisa las citas
     * cada minuto.
     */
    @Scheduled(
            initialDelay = 15_000,
            fixedDelay = 60_000
    )
    @Transactional
    public void enviarRecordatoriosPendientes() {

        LocalDateTime ahora =
                LocalDateTime.now(
                ZoneId.of(zonaHoraria)
        );

        /*
         * En modo normal conserva las 24 horas.
         *
         * En modo demo utiliza, por ejemplo,
         * 5 minutos.
         */
        LocalDateTime limiteRecordatorio;

        if (modoDemo) {
            limiteRecordatorio =
                    ahora.plusMinutes(
                            anticipacionDemoMinutos
                    );

            LOGGER.debug(
                    "Scheduler Wearable en MODO DEMO. Anticipación: {} minutos",
                    anticipacionDemoMinutos
            );
        } else {
            limiteRecordatorio =
                    ahora.plusHours(24);

            LOGGER.debug(
                    "Scheduler Wearable en modo normal. Anticipación: 24 horas"
            );
        }

        List<Cita> citasPendientes =
                citaRepository
                        .findByRecordatorioEnviadoFalseAndEstadoInOrderByFechaAscHoraAsc(
                                Set.of(
                                        EstadoCita.PENDIENTE,
                                        EstadoCita.CONFIRMADA
                                )
                        );

        for (Cita cita : citasPendientes) {

            LocalDateTime fechaHoraCita =
                    LocalDateTime.of(
                            cita.getFecha(),
                            cita.getHora()
                    );

            /*
             * No se envían recordatorios si:
             *
             * 1. La cita ya pasó.
             * 2. La cita todavía está fuera
             *    de la ventana configurada.
             */
            if (
                    fechaHoraCita.isBefore(ahora) ||
                    fechaHoraCita.isAfter(
                            limiteRecordatorio
                    )
            ) {
                continue;
            }

            List<DispositivoWearable> dispositivos =
                    dispositivoRepository
                            .findByUsuario_IdAndActivoTrue(
                                    cita.getUsuario()
                                            .getId()
                            );

            if (dispositivos.isEmpty()) {

                LOGGER.info(
                        "La cita {} no tiene relojes activos registrados",
                        cita.getId()
                );

                continue;
            }

            boolean enviadoAlMenosUnaVez =
                    false;

            for (
                    DispositivoWearable dispositivo :
                    dispositivos
            ) {

                boolean enviado =
                        notificacionService
                                .enviarRecordatorio(
                                        cita,
                                        dispositivo
                                                .getTokenFcm()
                                );

                if (enviado) {
                    enviadoAlMenosUnaVez =
                            true;
                }
            }

            /*
             * Solo se marca como enviado
             * cuando Firebase aceptó al menos
             * una notificación.
             */
            if (enviadoAlMenosUnaVez) {

                cita.setRecordatorioEnviado(
                        true
                );

                citaRepository.save(cita);

                LOGGER.info(
                        "La cita {} quedó marcada con recordatorio enviado. Modo demo: {}",
                        cita.getId(),
                        modoDemo
                );
            }
        }
    }
}