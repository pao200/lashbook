package com.lashbook.controller;

import com.lashbook.service.PruebaNotificacionWearableService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/wearable/notificaciones")
public class PruebaNotificacionWearableController {

    private final PruebaNotificacionWearableService
            pruebaNotificacionWearableService;

    public PruebaNotificacionWearableController(
            PruebaNotificacionWearableService
                    pruebaNotificacionWearableService
    ) {
        this.pruebaNotificacionWearableService =
                pruebaNotificacionWearableService;
    }

    @PostMapping("/prueba")
    public ResponseEntity<Map<String, Object>>
            enviarNotificacionPrueba(
                    @AuthenticationPrincipal Jwt jwt
            ) {

        UUID usuarioId =
                UUID.fromString(
                        jwt.getSubject()
                );

        int mensajesEnviados =
                pruebaNotificacionWearableService
                        .enviarNotificacionPrueba(
                                usuarioId
                        );

        return ResponseEntity.ok(
                Map.of(
                        "mensaje",
                        "Notificación de prueba enviada",
                        "dispositivosNotificados",
                        mensajesEnviados
                )
        );
    }
}