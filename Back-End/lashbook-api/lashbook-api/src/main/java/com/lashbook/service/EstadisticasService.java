package com.lashbook.service;

import com.lashbook.dto.EstadisticasResponse;
import com.lashbook.entity.EstadoCita;
import com.lashbook.repository.CitaRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class EstadisticasService {

    private final CitaRepository citaRepository;

    public EstadisticasService(
            CitaRepository citaRepository
    ) {
        this.citaRepository = citaRepository;
    }

    public EstadisticasResponse obtenerEstadisticas() {
        long totalCitas =
            citaRepository.count();

        long pendientes =
            citaRepository.countByEstado(
                EstadoCita.PENDIENTE
            );

        long confirmadas =
            citaRepository.countByEstado(
                EstadoCita.CONFIRMADA
            );

        long canceladas =
            citaRepository.countByEstado(
                EstadoCita.CANCELADA
            );

        long porReagendar =
            citaRepository.countByEstado(
                EstadoCita.REAGENDAR
            );

        long completadas =
            citaRepository.countByEstado(
                EstadoCita.COMPLETADA
            );

        BigDecimal ingresosTotales =
            citaRepository.sumarIngresosPorEstado(
                EstadoCita.COMPLETADA
            );

        if (ingresosTotales == null) {
            ingresosTotales = BigDecimal.ZERO;
        }

        return new EstadisticasResponse(
            totalCitas,
            pendientes,
            confirmadas,
            canceladas,
            porReagendar,
            completadas,
            ingresosTotales
        );
    }
}