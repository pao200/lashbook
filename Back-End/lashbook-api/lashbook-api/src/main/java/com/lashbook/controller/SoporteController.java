package com.lashbook.controller;

import com.lashbook.dto.soporte.ConversacionSoporteResponse;
import com.lashbook.dto.soporte.CrearConversacionRequest;
import com.lashbook.dto.soporte.EnviarMensajeRequest;
import com.lashbook.entity.EstadoConversacionSoporte;
import com.lashbook.service.SoporteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/soporte")
@RequiredArgsConstructor
public class SoporteController {

    private final SoporteService soporteService;

    /*
     * ============================================================
     * CLIENTA - CREAR NUEVA CONVERSACIÓN
     * ============================================================
     */

    @PostMapping("/conversaciones")
    public ResponseEntity<ConversacionSoporteResponse> crearConversacion(
            Authentication authentication,
            @Valid @RequestBody CrearConversacionRequest request
    ) {

        String correo = authentication.getName();

        ConversacionSoporteResponse respuesta =
                soporteService.crearConversacion(
                        correo,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(respuesta);
    }

    /*
     * ============================================================
     * CLIENTA - VER SUS CONVERSACIONES
     * ============================================================
     */

    @GetMapping("/mis-conversaciones")
    public ResponseEntity<List<ConversacionSoporteResponse>>
    listarMisConversaciones(
            Authentication authentication
    ) {

        String correo = authentication.getName();

        return ResponseEntity.ok(
                soporteService.listarMisConversaciones(correo)
        );
    }

    /*
     * ============================================================
     * CLIENTA / LASHISTA - VER UNA CONVERSACIÓN
     * ============================================================
     */

    @GetMapping("/conversaciones/{id}")
    public ResponseEntity<ConversacionSoporteResponse>
    obtenerConversacion(
            Authentication authentication,
            @PathVariable Long id
    ) {

        String correo = authentication.getName();

        return ResponseEntity.ok(
                soporteService.obtenerConversacion(
                        correo,
                        id
                )
        );
    }

    /*
     * ============================================================
     * CLIENTA / LASHISTA - ENVIAR MENSAJE
     * ============================================================
     */

    @PostMapping("/conversaciones/{id}/mensajes")
    public ResponseEntity<ConversacionSoporteResponse>
    enviarMensaje(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody EnviarMensajeRequest request
    ) {

        String correo = authentication.getName();

        ConversacionSoporteResponse respuesta =
                soporteService.enviarMensaje(
                        correo,
                        id,
                        request
                );

        return ResponseEntity.ok(respuesta);
    }

    /*
     * ============================================================
     * LASHISTA - VER TODAS LAS CONVERSACIONES
     * ============================================================
     */

    @GetMapping("/admin/conversaciones")
    public ResponseEntity<List<ConversacionSoporteResponse>>
    listarConversacionesLashista(
            Authentication authentication
    ) {

        String correo = authentication.getName();

        return ResponseEntity.ok(
                soporteService.listarConversacionesLashista(
                        correo
                )
        );
    }

    /*
     * ============================================================
     * LASHISTA - FILTRAR POR ESTADO
     * ============================================================
     */

    @GetMapping("/admin/conversaciones/estado/{estado}")
    public ResponseEntity<List<ConversacionSoporteResponse>>
    listarPorEstado(
            Authentication authentication,
            @PathVariable EstadoConversacionSoporte estado
    ) {

        String correo = authentication.getName();

        return ResponseEntity.ok(
                soporteService.listarPorEstado(
                        correo,
                        estado
                )
        );
    }

    /*
     * ============================================================
     * LASHISTA - CERRAR CONVERSACIÓN
     * ============================================================
     */

    @PutMapping("/admin/conversaciones/{id}/cerrar")
    public ResponseEntity<ConversacionSoporteResponse>
    cerrarConversacion(
            Authentication authentication,
            @PathVariable Long id
    ) {

        String correo = authentication.getName();

        return ResponseEntity.ok(
                soporteService.cerrarConversacion(
                        correo,
                        id
                )
        );
    }
}