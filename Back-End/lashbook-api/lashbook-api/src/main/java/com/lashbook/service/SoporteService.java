package com.lashbook.service;

import com.lashbook.dto.soporte.ConversacionSoporteResponse;
import com.lashbook.dto.soporte.CrearConversacionRequest;
import com.lashbook.dto.soporte.EnviarMensajeRequest;
import com.lashbook.dto.soporte.MensajeSoporteResponse;

import com.lashbook.entity.ConversacionSoporte;
import com.lashbook.entity.EstadoConversacionSoporte;
import com.lashbook.entity.MensajeSoporte;
import com.lashbook.entity.RolUsuario;
import com.lashbook.entity.Usuario;

import com.lashbook.repository.ConversacionSoporteRepository;
import com.lashbook.repository.MensajeSoporteRepository;
import com.lashbook.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SoporteService {

    private final ConversacionSoporteRepository conversacionRepository;
    private final MensajeSoporteRepository mensajeRepository;
    private final UsuarioRepository usuarioRepository;


    // ============================================================
    // CLIENTA - CREAR CONVERSACIÓN
    // ============================================================

    @Transactional
    public ConversacionSoporteResponse crearConversacion(
            String identificadorUsuario,
            CrearConversacionRequest request
    ) {

        Usuario clienta = obtenerUsuario(identificadorUsuario);

        if (clienta.getRol() != RolUsuario.CLIENTA) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo las clientas pueden iniciar conversaciones de soporte"
            );
        }

        ConversacionSoporte conversacion = new ConversacionSoporte();

        conversacion.setClienta(clienta);
        conversacion.setAsunto(request.getAsunto().trim());
        conversacion.setEstado(EstadoConversacionSoporte.ABIERTA);

        conversacion = conversacionRepository.save(conversacion);


        MensajeSoporte mensajeInicial = new MensajeSoporte();

        mensajeInicial.setConversacion(conversacion);
        mensajeInicial.setRemitente(clienta);
        mensajeInicial.setMensaje(request.getMensaje().trim());
        mensajeInicial.setLeido(false);

        mensajeRepository.save(mensajeInicial);


        // Actualizamos la fecha para que la conversación
        // aparezca como la más reciente.
        conversacion.setActualizadoEn(OffsetDateTime.now());

        conversacionRepository.save(conversacion);

        return convertirConversacion(conversacion);
    }


    // ============================================================
    // CLIENTA - VER SUS CONVERSACIONES
    // ============================================================

    @Transactional(readOnly = true)
    public List<ConversacionSoporteResponse> listarMisConversaciones(
            String identificadorUsuario
    ) {

        Usuario clienta = obtenerUsuario(identificadorUsuario);

        if (clienta.getRol() != RolUsuario.CLIENTA) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Esta operación corresponde a una clienta"
            );
        }

        return conversacionRepository
                .findByClientaIdOrderByActualizadoEnDesc(
                        clienta.getId()
                )
                .stream()
                .map(this::convertirConversacion)
                .toList();
    }


    // ============================================================
    // CLIENTA / LASHISTA - VER UNA CONVERSACIÓN
    // ============================================================

    @Transactional(readOnly = true)
    public ConversacionSoporteResponse obtenerConversacion(
            String identificadorUsuario,
            Long conversacionId
    ) {

        Usuario usuario = obtenerUsuario(identificadorUsuario);

        ConversacionSoporte conversacion =
                obtenerConversacionPorId(conversacionId);

        validarAccesoConversacion(
                usuario,
                conversacion
        );

        return convertirConversacion(conversacion);
    }


    // ============================================================
    // CLIENTA / LASHISTA - ENVIAR MENSAJE
    // ============================================================

    @Transactional
    public ConversacionSoporteResponse enviarMensaje(
            String identificadorUsuario,
            Long conversacionId,
            EnviarMensajeRequest request
    ) {

        Usuario usuario = obtenerUsuario(identificadorUsuario);

        ConversacionSoporte conversacion =
                obtenerConversacionPorId(conversacionId);

        validarAccesoConversacion(
                usuario,
                conversacion
        );

        if (conversacion.getEstado()
                == EstadoConversacionSoporte.CERRADA) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se pueden enviar mensajes a una conversación cerrada"
            );
        }

        MensajeSoporte mensaje = new MensajeSoporte();

        mensaje.setConversacion(conversacion);
        mensaje.setRemitente(usuario);
        mensaje.setMensaje(request.getMensaje().trim());
        mensaje.setLeido(false);

        mensajeRepository.save(mensaje);


        // Subimos la conversación al inicio
        // de la bandeja de soporte.
        conversacion.setActualizadoEn(OffsetDateTime.now());

        conversacionRepository.save(conversacion);

        return convertirConversacion(conversacion);
    }


    // ============================================================
    // LASHISTA - VER TODAS LAS CONVERSACIONES
    // ============================================================

    @Transactional(readOnly = true)
    public List<ConversacionSoporteResponse> listarConversacionesLashista(
            String identificadorUsuario
    ) {

        Usuario lashista = obtenerUsuario(identificadorUsuario);

        validarLashista(lashista);

        return conversacionRepository
                .findAllByOrderByActualizadoEnDesc()
                .stream()
                .map(this::convertirConversacion)
                .toList();
    }


    // ============================================================
    // LASHISTA - VER CONVERSACIONES POR ESTADO
    // ============================================================

    @Transactional(readOnly = true)
    public List<ConversacionSoporteResponse> listarPorEstado(
            String identificadorUsuario,
            EstadoConversacionSoporte estado
    ) {

        Usuario lashista = obtenerUsuario(identificadorUsuario);

        validarLashista(lashista);

        return conversacionRepository
                .findByEstadoOrderByActualizadoEnDesc(estado)
                .stream()
                .map(this::convertirConversacion)
                .toList();
    }


    // ============================================================
    // LASHISTA - CERRAR CONVERSACIÓN
    // ============================================================

    @Transactional
    public ConversacionSoporteResponse cerrarConversacion(
            String identificadorUsuario,
            Long conversacionId
    ) {

        Usuario lashista = obtenerUsuario(identificadorUsuario);

        validarLashista(lashista);

        ConversacionSoporte conversacion =
                obtenerConversacionPorId(conversacionId);

        conversacion.setEstado(
                EstadoConversacionSoporte.CERRADA
        );

        conversacion.setActualizadoEn(
                OffsetDateTime.now()
        );

        conversacionRepository.save(conversacion);

        return convertirConversacion(conversacion);
    }


    // ============================================================
    // OBTENER USUARIO DESDE EL JWT
    // ============================================================

    private Usuario obtenerUsuario(String identificador) {

        /*
         * authentication.getName() puede contener:
         *
         * 1. UUID del usuario
         * 2. Correo del usuario
         *
         * Primero intentamos interpretarlo como UUID.
         */

        try {

            UUID usuarioId = UUID.fromString(identificador);

            return usuarioRepository
                    .findById(usuarioId)
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Usuario no encontrado"
                            )
                    );

        } catch (IllegalArgumentException e) {

            /*
             * Si no tiene formato UUID,
             * entonces asumimos que es el correo.
             */

            return usuarioRepository
                    .findByCorreo(identificador)
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Usuario no encontrado"
                            )
                    );
        }
    }


    // ============================================================
    // OBTENER CONVERSACIÓN POR ID
    // ============================================================

    private ConversacionSoporte obtenerConversacionPorId(Long id) {

        return conversacionRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Conversación de soporte no encontrada"
                        )
                );
    }


    // ============================================================
    // VALIDAR ACCESO A UNA CONVERSACIÓN
    // ============================================================

    private void validarAccesoConversacion(
            Usuario usuario,
            ConversacionSoporte conversacion
    ) {

        /*
         * Una LASHISTA puede consultar
         * cualquier conversación.
         */

        if (usuario.getRol() == RolUsuario.LASHISTA) {
            return;
        }


        /*
         * Una CLIENTA únicamente puede consultar
         * sus propias conversaciones.
         */

        if (usuario.getRol() == RolUsuario.CLIENTA
                && conversacion
                .getClienta()
                .getId()
                .equals(usuario.getId())) {

            return;
        }


        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "No tienes permiso para acceder a esta conversación"
        );
    }


    // ============================================================
    // VALIDAR ROL LASHISTA
    // ============================================================

    private void validarLashista(Usuario usuario) {

        if (usuario.getRol() != RolUsuario.LASHISTA) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Esta operación requiere rol LASHISTA"
            );
        }
    }


    // ============================================================
    // CONVERTIR MENSAJE -> DTO
    // ============================================================

    private MensajeSoporteResponse convertirMensaje(
            MensajeSoporte mensaje
    ) {

        Usuario remitente = mensaje.getRemitente();

        return new MensajeSoporteResponse(
                mensaje.getId(),
                remitente.getId(),
                remitente.getNombre(),
                remitente.getRol().name(),
                mensaje.getMensaje(),
                mensaje.getLeido(),
                mensaje.getEnviadoEn()
        );
    }


    // ============================================================
    // CONVERTIR CONVERSACIÓN -> DTO
    // ============================================================

    private ConversacionSoporteResponse convertirConversacion(
            ConversacionSoporte conversacion
    ) {

        List<MensajeSoporteResponse> mensajes =
                mensajeRepository
                        .findByConversacionIdOrderByEnviadoEnAsc(
                                conversacion.getId()
                        )
                        .stream()
                        .map(this::convertirMensaje)
                        .toList();


        long mensajesNoLeidos =
                mensajeRepository
                        .countByConversacionIdAndLeidoFalse(
                                conversacion.getId()
                        );


        Usuario clienta = conversacion.getClienta();


        return new ConversacionSoporteResponse(
                conversacion.getId(),
                clienta.getId(),
                clienta.getNombre(),
                clienta.getCorreo(),
                conversacion.getAsunto(),
                conversacion.getEstado().name(),
                conversacion.getCreadoEn(),
                conversacion.getActualizadoEn(),
                mensajesNoLeidos,
                mensajes
        );
    }
}