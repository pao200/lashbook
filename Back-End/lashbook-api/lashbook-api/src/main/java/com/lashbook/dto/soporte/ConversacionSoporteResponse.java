package com.lashbook.dto.soporte;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ConversacionSoporteResponse {

    private Long id;

    private UUID clientaId;

    private String clientaNombre;

    private String clientaCorreo;

    private String asunto;

    private String estado;

    private OffsetDateTime creadoEn;

    private OffsetDateTime actualizadoEn;

    private Long mensajesNoLeidos;

    private List<MensajeSoporteResponse> mensajes;
}