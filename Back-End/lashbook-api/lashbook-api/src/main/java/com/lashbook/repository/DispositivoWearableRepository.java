package com.lashbook.repository;

import com.lashbook.entity.DispositivoWearable;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DispositivoWearableRepository
        extends JpaRepository<DispositivoWearable, UUID> {

    Optional<DispositivoWearable> findByTokenFcm(
        String tokenFcm
    );

    List<DispositivoWearable>
    findByUsuario_IdAndActivoTrue(
        UUID usuarioId
    );
}