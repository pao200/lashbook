package com.lashbook.service;

import com.lashbook.dto.MensajeContactoRequest;
import com.lashbook.entity.MensajeContacto;
import com.lashbook.repository.MensajeContactoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MensajeContactoService {

    private final MensajeContactoRepository mensajeContactoRepository;

    public MensajeContactoService(
        MensajeContactoRepository mensajeContactoRepository
    ) {
        this.mensajeContactoRepository = mensajeContactoRepository;
    }

    @Transactional
    public void guardar(
        MensajeContactoRequest request
    ) {
        MensajeContacto mensajeContacto =
            new MensajeContacto();

        mensajeContacto.setNombre(
            request.getNombre().trim()
        );

        mensajeContacto.setCorreo(
            request.getCorreo().trim().toLowerCase()
        );

        mensajeContacto.setMensaje(
            request.getMensaje().trim()
        );

        mensajeContacto.setLeido(false);

        mensajeContactoRepository.save(
            mensajeContacto
        );
    }
}