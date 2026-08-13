package com.lashbook.repository;

import com.lashbook.entity.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ServicioRepository
    extends JpaRepository<Servicio, UUID> {

    boolean existsByNombreIgnoreCase(String nombre);

    List<Servicio> findByActivoTrueOrderByNombreAsc();
}