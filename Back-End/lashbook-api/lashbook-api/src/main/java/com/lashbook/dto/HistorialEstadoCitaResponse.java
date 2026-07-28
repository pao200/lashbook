package com.lashbook.dto;

import com.lashbook.entity.EstadoCita;
import com.lashbook.entity.OrigenCambio;

import java.time.LocalDateTime;
import java.util.UUID;

public class HistorialEstadoCitaResponse {

    private final UUID id;
    private final EstadoCita estadoAnterior;
    private final EstadoCita estadoNuevo;
    private final OrigenCambio origen;
    private final UUID usuarioActorId;
    private final String nombreActor;
    private final String detalle;
    private final LocalDateTime fechaCambio;

    public HistorialEstadoCitaResponse(
            UUID id,
            EstadoCita estadoAnterior,
            EstadoCita estadoNuevo,
            OrigenCambio origen,
            UUID usuarioActorId,
            String nombreActor,
            String detalle,
            LocalDateTime fechaCambio
    ) {
        this.id = id;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.origen = origen;
        this.usuarioActorId = usuarioActorId;
        this.nombreActor = nombreActor;
        this.detalle = detalle;
        this.fechaCambio = fechaCambio;
    }

    public UUID getId() {
        return id;
    }

    public EstadoCita getEstadoAnterior() {
        return estadoAnterior;
    }

    public EstadoCita getEstadoNuevo() {
        return estadoNuevo;
    }

    public OrigenCambio getOrigen() {
        return origen;
    }

    public UUID getUsuarioActorId() {
        return usuarioActorId;
    }

    public String getNombreActor() {
        return nombreActor;
    }

    public String getDetalle() {
        return detalle;
    }

    public LocalDateTime getFechaCambio() {
        return fechaCambio;
    }
}