package com.lashbook.dto;

import com.lashbook.entity.EstadoCita;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public class CitaResponse {

    private UUID id;
    private UUID usuarioId;
    private String nombreClienta;
    private UUID servicioId;
    private String nombreServicio;
    private LocalDate fecha;
    private LocalTime hora;
    private EstadoCita estado;
    private String comentarios;
    private Boolean recordatorioEnviado;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    public CitaResponse(
            UUID id,
            UUID usuarioId,
            String nombreClienta,
            UUID servicioId,
            String nombreServicio,
            LocalDate fecha,
            LocalTime hora,
            EstadoCita estado,
            String comentarios,
            Boolean recordatorioEnviado,
            LocalDateTime creadoEn,
            LocalDateTime actualizadoEn
    ) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.nombreClienta = nombreClienta;
        this.servicioId = servicioId;
        this.nombreServicio = nombreServicio;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
        this.comentarios = comentarios;
        this.recordatorioEnviado = recordatorioEnviado;
        this.creadoEn = creadoEn;
        this.actualizadoEn = actualizadoEn;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public String getNombreClienta() {
        return nombreClienta;
    }

    public UUID getServicioId() {
        return servicioId;
    }

    public String getNombreServicio() {
        return nombreServicio;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public String getComentarios() {
        return comentarios;
    }

    public Boolean getRecordatorioEnviado() {
        return recordatorioEnviado;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }
}