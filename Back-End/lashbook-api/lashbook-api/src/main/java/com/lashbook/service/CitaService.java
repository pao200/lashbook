package com.lashbook.service;

import com.lashbook.dto.HistorialEstadoCitaResponse;
import com.lashbook.dto.CitaRequest;
import com.lashbook.dto.CitaResponse;
import com.lashbook.entity.Cita;
import com.lashbook.entity.EstadoCita;
import com.lashbook.entity.OrigenCambio;
import com.lashbook.entity.Servicio;
import com.lashbook.entity.Usuario;
import com.lashbook.repository.CitaRepository;
import com.lashbook.repository.ServicioRepository;
import com.lashbook.repository.UsuarioRepository;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

@Service
public class CitaService {

    private final CitaRepository citaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicioRepository servicioRepository;
    private final HistorialEstadoCitaService historialEstadoCitaService;

    public CitaService(
            CitaRepository citaRepository,
            UsuarioRepository usuarioRepository,
            ServicioRepository servicioRepository,
            HistorialEstadoCitaService historialEstadoCitaService
    ) {
        this.citaRepository = citaRepository;
        this.usuarioRepository = usuarioRepository;
        this.servicioRepository = servicioRepository;
        this.historialEstadoCitaService =
            historialEstadoCitaService;
    }

    @Transactional
    public CitaResponse registrar(
            UUID usuarioId,
            CitaRequest request
    ) {
        Usuario usuario = usuarioRepository
            .findById(usuarioId)
            .orElseThrow(() ->
                new NoSuchElementException(
                    "Usuario no encontrado"
                )
            );

        Servicio servicio = servicioRepository
            .findById(request.getServicioId())
            .orElseThrow(() ->
                new NoSuchElementException(
                    "Servicio no encontrado"
                )
            );

        if (!Boolean.TRUE.equals(servicio.getActivo())) {
            throw new IllegalArgumentException(
                "El servicio seleccionado no está disponible"
            );
        }

        LocalDateTime fechaHoraSolicitada =
            LocalDateTime.of(
                request.getFecha(),
                request.getHora()
            );

        if (fechaHoraSolicitada.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                "La fecha y hora no pueden estar en el pasado"
            );
        }

        boolean horarioOcupado =
            citaRepository.existsByFechaAndHoraAndEstadoIn(
                request.getFecha(),
                request.getHora(),
                Set.of(
                    EstadoCita.PENDIENTE,
                    EstadoCita.CONFIRMADA
                )
            );

        if (horarioOcupado) {
            throw new IllegalArgumentException(
                "El horario seleccionado ya está ocupado"
            );
        }

        Cita cita = new Cita();

        cita.setUsuario(usuario);
        cita.setServicio(servicio);
        cita.setFecha(request.getFecha());
        cita.setHora(request.getHora());
        cita.setEstado(EstadoCita.PENDIENTE);

        cita.setComentarios(
            request.getComentarios() == null
                ? null
                : request.getComentarios().trim()
        );

        cita.setRecordatorioEnviado(false);

        Cita guardada = citaRepository.save(cita);

        historialEstadoCitaService.registrarCambio(guardada,null,EstadoCita.PENDIENTE,OrigenCambio.WEB_CLIENTA,guardada.getUsuario(),"La clienta creó la cita");

        return convertirRespuesta(guardada);
    }

    @Transactional(readOnly = true)
    public List<CitaResponse> listarMisCitas(
            UUID usuarioId
    ) {
        return citaRepository
            .findByUsuario_IdOrderByFechaAscHoraAsc(usuarioId)
            .stream()
            .map(this::convertirRespuesta)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<CitaResponse> listarTodas() {
        return citaRepository
            .findAllByOrderByFechaAscHoraAsc()
            .stream()
            .map(this::convertirRespuesta)
            .toList();
    }

    @Transactional
    public CitaResponse cambiarEstadoClienta(
            UUID usuarioId,
            UUID citaId,
            EstadoCita nuevoEstado
    ) {
        Cita cita = buscarEntidad(citaId);

        if (!cita.getUsuario().getId().equals(usuarioId)) {
            throw new AccessDeniedException(
                "No puedes modificar una cita que no te pertenece"
            );
        }

        Set<EstadoCita> estadosPermitidos = Set.of(
            EstadoCita.CONFIRMADA,
            EstadoCita.CANCELADA,
            EstadoCita.REAGENDAR
        );

        if (!estadosPermitidos.contains(nuevoEstado)) {
            throw new IllegalArgumentException(
                "La clienta solamente puede confirmar, cancelar o solicitar reagendar"
            );
        }
        

        validarEstadoFinal(cita);

        if (cita.getEstado() == nuevoEstado) {
            throw new IllegalArgumentException(
                "La cita ya tiene el estado solicitado"
            );
        }

        EstadoCita estadoAnterior = cita.getEstado();

        cita.setEstado(nuevoEstado);

        Cita citaGuardada = citaRepository.save(cita);

        historialEstadoCitaService.registrarCambio(
            citaGuardada,
            estadoAnterior,
            nuevoEstado,
            OrigenCambio.WEB_CLIENTA,
            citaGuardada.getUsuario(),
            obtenerDetalleClienta(nuevoEstado)
        );

        return convertirRespuesta(citaGuardada);
    }

    @Transactional(readOnly = true)
    public List<HistorialEstadoCitaResponse> consultarHistorialClienta(
        UUID usuarioId,
        UUID citaId
    ) {
    Cita cita = buscarEntidad(citaId);

    if (!cita.getUsuario().getId().equals(usuarioId)) {
        throw new AccessDeniedException(
            "No puedes consultar el historial de una cita que no te pertenece"
        );
    }

       return historialEstadoCitaService.consultarPorCita(citaId);
    }



    @Transactional
    public CitaResponse cambiarEstadoAdministrativo(
            UUID usuarioActorId,
            UUID citaId,
            EstadoCita nuevoEstado
    ) {
        Cita cita = buscarEntidad(citaId);

        Usuario usuarioActor = usuarioRepository
            .findById(usuarioActorId)
            .orElseThrow(() ->
                new NoSuchElementException(
                    "Usuario administrador no encontrado"
                )
            );

        if (cita.getEstado() == nuevoEstado) {
            throw new IllegalArgumentException(
                "La cita ya tiene el estado solicitado"
            );
        }

        if (cita.getEstado() == EstadoCita.CANCELADA) {
            throw new IllegalArgumentException(
                "No se puede modificar una cita cancelada"
            );
        }

        if (cita.getEstado() == EstadoCita.COMPLETADA) {
            throw new IllegalArgumentException(
                "No se puede modificar una cita completada"
            );
        }

        if (
            nuevoEstado == EstadoCita.COMPLETADA &&
            cita.getEstado() != EstadoCita.CONFIRMADA
        ) {
            throw new IllegalArgumentException(
                "Solo una cita confirmada puede marcarse como completada"
            );
        }

        EstadoCita estadoAnterior = cita.getEstado();

        cita.setEstado(nuevoEstado);

        Cita citaGuardada = citaRepository.save(cita);

        historialEstadoCitaService.registrarCambio(
            citaGuardada,
            estadoAnterior,
            nuevoEstado,
            OrigenCambio.WEB_ADMIN,
            usuarioActor,
            "La lashista cambió el estado de la cita"
        );

        return convertirRespuesta(citaGuardada);
    }

    @Transactional
    public CitaResponse reagendarAdministrativamente(
        UUID usuarioActorId,
        UUID citaId,
        LocalDate nuevaFecha,
        LocalTime nuevaHora
    ) {
    Cita cita = buscarEntidad(citaId);

    Usuario usuarioActor = usuarioRepository
        .findById(usuarioActorId)
        .orElseThrow(() ->
            new NoSuchElementException(
                "Usuario administrador no encontrado"
            )
        );

    if (cita.getEstado() != EstadoCita.REAGENDAR) {
        throw new IllegalArgumentException(
            "La cita debe estar en estado REAGENDAR"
        );
    }

    LocalDateTime nuevaFechaHora =
        LocalDateTime.of(nuevaFecha, nuevaHora);

    if (nuevaFechaHora.isBefore(LocalDateTime.now())) {
        throw new IllegalArgumentException(
            "La nueva fecha y hora no pueden estar en el pasado"
        );
    }

    boolean horarioOcupado =
        citaRepository
            .existsByFechaAndHoraAndEstadoInAndIdNot(
                nuevaFecha,
                nuevaHora,
                Set.of(
                    EstadoCita.PENDIENTE,
                    EstadoCita.CONFIRMADA
                ),
                citaId
            );

        if (horarioOcupado) {
        throw new IllegalArgumentException(
            "El nuevo horario ya está ocupado"
        );
    }

    EstadoCita estadoAnterior = cita.getEstado();

    cita.setFecha(nuevaFecha);
    cita.setHora(nuevaHora);
    cita.setEstado(EstadoCita.PENDIENTE);
    cita.setRecordatorioEnviado(false);

    Cita citaGuardada = citaRepository.save(cita);

    historialEstadoCitaService.registrarCambio(
        citaGuardada,
        estadoAnterior,
        EstadoCita.PENDIENTE,
        OrigenCambio.WEB_ADMIN,
        usuarioActor,
        "La lashista reagendó la cita"
    );

     return convertirRespuesta(citaGuardada);
    }
    private void validarEstadoFinal(Cita cita) {
    if (cita.getEstado() == EstadoCita.CANCELADA) {
        throw new IllegalArgumentException(
            "La cita ya fue cancelada"
        );
    }

    if (cita.getEstado() == EstadoCita.COMPLETADA) {
        throw new IllegalArgumentException(
            "La cita ya fue completada"
        );
    }
}

private String obtenerDetalleClienta(
        EstadoCita nuevoEstado
) {
    return switch (nuevoEstado) {
        case CONFIRMADA ->
            "La clienta confirmó la cita";

        case CANCELADA ->
            "La clienta canceló la cita";

        case REAGENDAR ->
            "La clienta solicitó reagendar la cita";

        default ->
            "La clienta modificó el estado de la cita";
    };
}

private Cita buscarEntidad(UUID citaId) {
    return citaRepository
        .findById(citaId)
        .orElseThrow(() ->
            new NoSuchElementException(
                "Cita no encontrada"
            )
        );
}

private CitaResponse convertirRespuesta(Cita cita) {
    return new CitaResponse(
        cita.getId(),
        cita.getUsuario().getId(),
        cita.getUsuario().getNombre(),
        cita.getServicio().getId(),
        cita.getServicio().getNombre(),
        cita.getFecha(),
        cita.getHora(),
        cita.getEstado(),
        cita.getComentarios(),
        cita.getRecordatorioEnviado(),
        cita.getCreadoEn(),
        cita.getActualizadoEn()
    );
   }
}