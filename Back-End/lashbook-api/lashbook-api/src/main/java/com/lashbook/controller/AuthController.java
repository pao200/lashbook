package com.lashbook.controller;

import com.lashbook.dto.RegistroUsuarioRequest;
import com.lashbook.dto.UsuarioResponse;
import com.lashbook.service.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.lashbook.dto.LoginRequest;
import com.lashbook.dto.LoginResponse;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrar(
            @Valid @RequestBody RegistroUsuarioRequest request
    ) {
        UsuarioResponse usuario =
            usuarioService.registrarCliente(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(usuario);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
        @Valid @RequestBody LoginRequest request
    ) {
    LoginResponse respuesta =
        usuarioService.iniciarSesion(request);

     return ResponseEntity.ok(respuesta);
    }
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> obtenerPerfil(
        @AuthenticationPrincipal Jwt jwt
    ) {
       return ResponseEntity.ok(
        Map.of(
            "id", jwt.getSubject(),
            "nombre", jwt.getClaimAsString("nombre"),
            "correo", jwt.getClaimAsString("correo"),
            "rol", jwt.getClaimAsString("rol")
        )
        );
    }


    
}
