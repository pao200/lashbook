package com.lashbook.repository;

import com.lashbook.entity.Cita;
import com.lashbook.entity.EstadoCita;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface CitaRepository
        extends JpaRepository<Cita, UUID> {

    List<Cita> findByUsuario_IdOrderByFechaAscHoraAsc(
        UUID usuarioId
    );

    List<Cita> findByFechaOrderByHoraAsc(
        LocalDate fecha
    );

    List<Cita> findAllByOrderByFechaAscHoraAsc();

    // Se usa cuando se registra una cita nueva.
    boolean existsByFechaAndHoraAndEstadoIn(
        LocalDate fecha,
        LocalTime hora,
        Collection<EstadoCita> estados
    );

    // Se usa cuando se reagenda una cita existente.
    boolean existsByFechaAndHoraAndEstadoInAndIdNot(
        LocalDate fecha,
        LocalTime hora,
        Collection<EstadoCita> estados,
        UUID citaId
    );
}