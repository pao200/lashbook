package com.lashbook.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class ReagendarCitaRequest {

    @NotNull(message = "La nueva fecha es obligatoria")
    @FutureOrPresent(
        message = "La nueva fecha no puede estar en el pasado"
    )
    private LocalDate fecha;

    @NotNull(message = "La nueva hora es obligatoria")
    private LocalTime hora;
}