package com.lashbook.repository;

import com.lashbook.entity.ConversacionSoporte;
import com.lashbook.entity.EstadoConversacionSoporte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConversacionSoporteRepository
        extends JpaRepository<ConversacionSoporte, Long> {

    // Conversaciones pertenecientes a una clienta
    List<ConversacionSoporte> findByClientaIdOrderByActualizadoEnDesc(UUID clientaId);

    // Todas las conversaciones según su estado
    List<ConversacionSoporte> findByEstadoOrderByActualizadoEnDesc(
            EstadoConversacionSoporte estado
    );

    // Todas las conversaciones, de la más reciente a la más antigua
    List<ConversacionSoporte> findAllByOrderByActualizadoEnDesc();

    // Verifica que una conversación pertenezca a una clienta
    boolean existsByIdAndClientaId(Long id, UUID clientaId);
}