package com.lashbook.repository;

import com.lashbook.entity.MensajeSoporte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MensajeSoporteRepository
        extends JpaRepository<MensajeSoporte, Long> {

    // Mensajes de una conversación ordenados cronológicamente
    List<MensajeSoporte> findByConversacionIdOrderByEnviadoEnAsc(
            Long conversacionId
    );

    // Mensajes no leídos de una conversación
    List<MensajeSoporte> findByConversacionIdAndLeidoFalseOrderByEnviadoEnAsc(
            Long conversacionId
    );

    // Contador de mensajes pendientes
    long countByConversacionIdAndLeidoFalse(Long conversacionId);
}