package com.lashbook.repository;

import com.lashbook.entity.HistorialEstadoCita;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HistorialEstadoCitaRepository
        extends JpaRepository<HistorialEstadoCita, UUID> {

    List<HistorialEstadoCita>
    findByCita_IdOrderByFechaCambioDesc(UUID citaId);
}