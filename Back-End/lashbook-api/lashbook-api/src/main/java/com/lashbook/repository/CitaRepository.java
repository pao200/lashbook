package com.lashbook.repository;

import com.lashbook.entity.Cita;
import com.lashbook.entity.EstadoCita;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
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

    long countByEstado(
        EstadoCita estado
    );

    @Query("""
        SELECT SUM(c.servicio.precio)
        FROM Cita c
        WHERE c.estado = :estado
        """)
    BigDecimal sumarIngresosPorEstado(
        @Param("estado")
        EstadoCita estado
    );

    /*
     * Busca las citas futuras de la clienta
     * para mostrar la más próxima en Wear OS.
     */
    @Query("""
        SELECT c
        FROM Cita c
        WHERE c.usuario.id = :usuarioId
          AND c.estado IN :estados
          AND (
              c.fecha > :fechaActual
              OR (
                  c.fecha = :fechaActual
                  AND c.hora >= :horaActual
              )
          )
        ORDER BY c.fecha ASC, c.hora ASC
        """)
    List<Cita> buscarProximasCitasWearable(
        @Param("usuarioId")
        UUID usuarioId,

        @Param("estados")
        Collection<EstadoCita> estados,

        @Param("fechaActual")
        LocalDate fechaActual,

        @Param("horaActual")
        LocalTime horaActual
    );

    /*
     * Busca citas que todavía no han recibido
     * el recordatorio de 24 horas.
     *
     * El servicio comprobará cuáles comienzan
     * dentro de las próximas 24 horas.
     */
    List<Cita>
    findByRecordatorioEnviadoFalseAndEstadoInOrderByFechaAscHoraAsc(
        Collection<EstadoCita> estados
    );

    // Se usa al registrar una cita nueva.
    boolean existsByFechaAndHoraAndEstadoIn(
        LocalDate fecha,
        LocalTime hora,
        Collection<EstadoCita> estados
    );

    // Se usa al reagendar una cita existente.
    boolean existsByFechaAndHoraAndEstadoInAndIdNot(
        LocalDate fecha,
        LocalTime hora,
        Collection<EstadoCita> estados,
        UUID citaId
    );
}