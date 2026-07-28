package com.lashbook.service;

import com.lashbook.entity.Cita;
import com.lashbook.entity.EstadoCita;
import com.lashbook.entity.HistorialEstadoCita;
import com.lashbook.entity.OrigenCambio;
import com.lashbook.entity.Usuario;
import com.lashbook.repository.HistorialEstadoCitaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lashbook.dto.HistorialEstadoCitaResponse;

import java.util.List;
import java.util.UUID;

@Service
public class HistorialEstadoCitaService {

    private final HistorialEstadoCitaRepository historialRepository;

    public HistorialEstadoCitaService(
            HistorialEstadoCitaRepository historialRepository
    ) {
        this.historialRepository = historialRepository;
    }

    @Transactional
    public HistorialEstadoCita registrarCambio(
            Cita cita,
            EstadoCita estadoAnterior,
            EstadoCita estadoNuevo,
            OrigenCambio origen,
            Usuario usuarioActor,
            String detalle
    ) {
        HistorialEstadoCita historial =
            new HistorialEstadoCita();

        historial.setCita(cita);
        historial.setEstadoAnterior(estadoAnterior);
        historial.setEstadoNuevo(estadoNuevo);
        historial.setOrigen(origen);
        historial.setUsuarioActor(usuarioActor);

        historial.setDetalle(
            detalle == null || detalle.isBlank()
                ? null
                : detalle.trim()
        );

        return historialRepository.save(historial);
    }

    @Transactional(readOnly = true)
      public List<HistorialEstadoCitaResponse> consultarPorCita(
        UUID citaId
    ) {
    return historialRepository
        .findByCita_IdOrderByFechaCambioDesc(citaId)
        .stream()
        .map(historial ->
            new HistorialEstadoCitaResponse(
                historial.getId(),
                historial.getEstadoAnterior(),
                historial.getEstadoNuevo(),
                historial.getOrigen(),

                historial.getUsuarioActor() == null
                    ? null
                    : historial.getUsuarioActor().getId(),

                historial.getUsuarioActor() == null
                    ? "Sistema"
                    : historial.getUsuarioActor().getNombre(),

                historial.getDetalle(),
                historial.getFechaCambio()
            )
        )
        .toList();
    }




}