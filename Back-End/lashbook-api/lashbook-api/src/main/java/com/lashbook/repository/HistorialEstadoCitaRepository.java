package com.lashbook.repository;

import com.lashbook.entity.HistorialEstadoCita;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface HistorialEstadoCitaRepository
        extends JpaRepository<HistorialEstadoCita, UUID> {

    List<HistorialEstadoCita>
    findByCita_IdOrderByFechaCambioDesc(
            UUID citaId
    );

    @Modifying(
        flushAutomatically = true,
        clearAutomatically = true
    )
    @Query(
        value = """
            DELETE FROM historial_estado_cita
            WHERE cita_id = :citaId
            """,
        nativeQuery = true
    )
    int eliminarPorCita(
            @Param("citaId")
            UUID citaId
    );
}