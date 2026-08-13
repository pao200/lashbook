package com.lashbook.controller;

import com.lashbook.dto.ImagenServicioResponse;
import com.lashbook.dto.ServicioRequest;
import com.lashbook.dto.ServicioResponse;
import com.lashbook.service.ServicioService;
import com.lashbook.service.SupabaseStorageService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
public class ServicioController {

    private final ServicioService servicioService;
    private final SupabaseStorageService storageService;

    public ServicioController(
            ServicioService servicioService,
            SupabaseStorageService storageService
    ) {
        this.servicioService = servicioService;
        this.storageService = storageService;
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
            .body(
                servicioService.crear(request)
            );
    }

    @PostMapping(
        value = "/api/admin/servicios/imagen",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ImagenServicioResponse>
    subirImagen(
            @RequestParam("archivo")
            MultipartFile archivo
    ) {
        String imagenUrl =
            storageService.subirImagen(archivo);

        ImagenServicioResponse respuesta =
            new ImagenServicioResponse(imagenUrl);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(respuesta);
    }

    @PutMapping("/api/admin/servicios/{id}")
    public ResponseEntity<ServicioResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ServicioRequest request
    ) {
        return ResponseEntity.ok(
            servicioService.actualizar(
                id,
                request
            )
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