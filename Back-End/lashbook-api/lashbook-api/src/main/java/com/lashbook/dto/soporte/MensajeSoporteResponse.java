package com.lashbook.dto.soporte;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class MensajeSoporteResponse {

    private Long id;

    private UUID remitenteId;

    private String remitenteNombre;

    private String remitenteRol;

    private String mensaje;

    private Boolean leido;

    private OffsetDateTime enviadoEn;
}