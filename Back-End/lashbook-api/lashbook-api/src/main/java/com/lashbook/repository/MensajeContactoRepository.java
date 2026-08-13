package com.lashbook.repository;

import com.lashbook.entity.MensajeContacto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MensajeContactoRepository
    extends JpaRepository<MensajeContacto, UUID> {

    List<MensajeContacto> findAllByOrderByCreadoEnDesc();
}