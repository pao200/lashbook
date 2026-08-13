package com.lashbook.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegistrarDispositivoWearableRequest {

    @NotBlank(
        message = "El token de notificaciones es obligatorio"
    )
    @Size(
        max = 500,
        message = "El token no puede superar 500 caracteres"
    )
    private String tokenFcm;

    public String getTokenFcm() {
        return tokenFcm;
    }

    public void setTokenFcm(
            String tokenFcm
    ) {
        this.tokenFcm = tokenFcm;
    }
}