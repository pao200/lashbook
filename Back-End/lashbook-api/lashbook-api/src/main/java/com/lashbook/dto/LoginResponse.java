package com.lashbook.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String tipoToken;
    private long expiraEnSegundos;
    private UsuarioResponse usuario;
}