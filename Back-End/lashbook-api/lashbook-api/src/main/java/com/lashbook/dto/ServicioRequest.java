package com.lashbook.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ServicioRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120, message = "El nombre no puede superar 120 caracteres")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(
        value = "0.01",
        message = "El precio debe ser mayor a cero"
    )
    private BigDecimal precio;

    @NotNull(message = "La duración es obligatoria")
    @Min(
        value = 15,
        message = "La duración mínima es de 15 minutos"
    )
    private Integer duracionMinutos;

    @Size(
        max = 500,
        message = "La URL de la imagen no puede superar 500 caracteres"
    )
    private String imagenUrl;
}