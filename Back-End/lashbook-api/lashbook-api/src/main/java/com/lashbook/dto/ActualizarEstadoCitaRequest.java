package com.lashbook.dto;

import com.lashbook.entity.EstadoCita;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActualizarEstadoCitaRequest {

    @NotNull(message = "El estado es obligatorio")
    private EstadoCita estado;
}