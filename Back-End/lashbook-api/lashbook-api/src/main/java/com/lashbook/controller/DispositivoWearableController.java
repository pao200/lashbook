package com.lashbook.controller;

import com.lashbook.dto.RegistrarDispositivoWearableRequest;
import com.lashbook.service.DispositivoWearableService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/wearable/dispositivos")
public class DispositivoWearableController {

    private final DispositivoWearableService
        dispositivoWearableService;

    public DispositivoWearableController(
            DispositivoWearableService
                dispositivoWearableService
    ) {
        this.dispositivoWearableService =
            dispositivoWearableService;
    }

    @PostMapping
    public ResponseEntity<Void> registrarDispositivo(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody
            RegistrarDispositivoWearableRequest request
    ) {
        UUID usuarioId =
            UUID.fromString(jwt.getSubject());

        dispositivoWearableService.registrarDispositivo(
            usuarioId,
            request.getTokenFcm()
        );

        return ResponseEntity.noContent().build();
    }
}