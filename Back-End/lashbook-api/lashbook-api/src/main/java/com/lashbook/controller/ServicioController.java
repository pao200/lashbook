package com.lashbook.controller;

import com.lashbook.dto.ServicioRequest;
import com.lashbook.dto.ServicioResponse;
import com.lashbook.service.ServicioService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class ServicioController {

    private final ServicioService servicioService;

    public ServicioController(
            ServicioService servicioService
    ) {
        this.servicioService = servicioService;
    }

    @GetMapping("/api/servicios")
    public ResponseEntity<List<ServicioResponse>>
    listarActivos() {
        return ResponseEntity.ok(
            servicioService.listarActivos()
        );
    }

    @GetMapping("/api/servicios/{id}")
    public ResponseEntity<ServicioResponse> obtener(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
            servicioService.obtenerPorId(id)
        );
    }

    @PostMapping("/api/admin/servicios")
    public ResponseEntity<ServicioResponse> crear(
            @Valid @RequestBody ServicioRequest request
    ) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(servicioService.crear(request));
    }

    @PutMapping("/api/admin/servicios/{id}")
    public ResponseEntity<ServicioResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ServicioRequest request
    ) {
        return ResponseEntity.ok(
            servicioService.actualizar(id, request)
        );
    }

    @DeleteMapping("/api/admin/servicios/{id}")
    public ResponseEntity<ServicioResponse> desactivar(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
            servicioService.desactivar(id)
        );
    }
}