package com.lashbook.dto;

import com.lashbook.entity.RolUsuario;

import java.time.LocalDateTime;
import java.util.UUID;

public class UsuarioResponse {

    private UUID id;
    private String nombre;
    private String correo;
    private RolUsuario rol;
    private Boolean activo;
    private LocalDateTime creadoEn;

    public UsuarioResponse(
            UUID id,
            String nombre,
            String correo,
            RolUsuario rol,
            Boolean activo,
            LocalDateTime creadoEn
    ) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.rol = rol;
        this.activo = activo;
        this.creadoEn = creadoEn;
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public Boolean getActivo() {
        return activo;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }
}