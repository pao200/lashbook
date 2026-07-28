package com.lashbook.controller;

import com.lashbook.dto.ActualizarEstadoCitaRequest;
import com.lashbook.dto.CitaRequest;
import com.lashbook.dto.CitaResponse;
import com.lashbook.dto.HistorialEstadoCitaResponse;
import com.lashbook.dto.ReagendarCitaRequest;
import com.lashbook.service.CitaService;
import com.lashbook.service.HistorialEstadoCitaService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class CitaController {

    private final CitaService citaService;
    private final HistorialEstadoCitaService historialEstadoCitaService;

    public CitaController(
            CitaService citaService,
            HistorialEstadoCitaService historialEstadoCitaService
    ) {
        this.citaService = citaService;
        this.historialEstadoCitaService =
            historialEstadoCitaService;
    }

    @PostMapping("/citas")
    public ResponseEntity<CitaResponse> registrar(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CitaRequest request
    ) {
        UUID usuarioId =
            UUID.fromString(jwt.getSubject());

        CitaResponse respuesta =
            citaService.registrar(
                usuarioId,
                request
            );

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(respuesta);
    }

    @GetMapping("/citas/mis-citas")
    public ResponseEntity<List<CitaResponse>> listarMisCitas(
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID usuarioId =
            UUID.fromString(jwt.getSubject());

        return ResponseEntity.ok(
            citaService.listarMisCitas(usuarioId)
        );
    }

    @PatchMapping("/citas/{id}/estado")
    public ResponseEntity<CitaResponse> cambiarMiEstado(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id,
            @Valid @RequestBody
            ActualizarEstadoCitaRequest request
    ) {
        UUID usuarioId =
            UUID.fromString(jwt.getSubject());

        return ResponseEntity.ok(
            citaService.cambiarEstadoClienta(
                usuarioId,
                id,
                request.getEstado()
            )
        );
    }

    @GetMapping("/citas/{id}/historial")
    public ResponseEntity<List<HistorialEstadoCitaResponse>>
    consultarHistorialClienta(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id
    ) {
        UUID usuarioId =
            UUID.fromString(jwt.getSubject());

        return ResponseEntity.ok(
            citaService.consultarHistorialClienta(
                usuarioId,
                id
            )
        );
    }

    @GetMapping("/admin/citas")
    public ResponseEntity<List<CitaResponse>> listarTodas() {
        return ResponseEntity.ok(
            citaService.listarTodas()
        );
    }

    @PatchMapping("/admin/citas/{id}/estado")
    public ResponseEntity<CitaResponse>
    cambiarEstadoAdministrativo(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id,
            @Valid @RequestBody
            ActualizarEstadoCitaRequest request
    ) {
        UUID usuarioActorId =
            UUID.fromString(jwt.getSubject());

        return ResponseEntity.ok(
            citaService.cambiarEstadoAdministrativo(
                usuarioActorId,
                id,
                request.getEstado()
            )
        );
    }

    @PatchMapping("/admin/citas/{id}/reagendar")
    public ResponseEntity<CitaResponse>
    reagendarAdministrativamente(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id,
            @Valid @RequestBody
            ReagendarCitaRequest request
    ) {
        UUID usuarioActorId =
            UUID.fromString(jwt.getSubject());

        return ResponseEntity.ok(
            citaService.reagendarAdministrativamente(
                usuarioActorId,
                id,
                request.getFecha(),
                request.getHora()
            )
        );
    }

    @GetMapping("/admin/citas/{id}/historial")
    public ResponseEntity<List<HistorialEstadoCitaResponse>>
    consultarHistorialAdministrativo(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
            historialEstadoCitaService.consultarPorCita(id)
        );
    }
}