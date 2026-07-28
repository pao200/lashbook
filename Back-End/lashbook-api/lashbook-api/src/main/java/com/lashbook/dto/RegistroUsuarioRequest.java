package com.lashbook.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistroUsuarioRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(
        min = 3,
        max = 100,
        message = "El nombre debe tener entre 3 y 100 caracteres"
    )
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    @Size(
        max = 150,
        message = "El correo no puede superar los 150 caracteres"
    )
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(
        min = 8,
        max = 72,
        message = "La contraseña debe tener entre 8 y 72 caracteres"
    )
    private String password;
}
