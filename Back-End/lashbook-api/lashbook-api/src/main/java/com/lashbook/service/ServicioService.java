package com.lashbook.service;

import com.lashbook.dto.ServicioRequest;
import com.lashbook.dto.ServicioResponse;
import com.lashbook.entity.Servicio;
import com.lashbook.repository.ServicioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ServicioService {

    private final ServicioRepository servicioRepository;

    public ServicioService(
            ServicioRepository servicioRepository
    ) {
        this.servicioRepository = servicioRepository;
    }

    @Transactional
    public ServicioResponse crear(ServicioRequest request) {

        String nombreNormalizado = request.getNombre().trim();

        if (servicioRepository
                .existsByNombreIgnoreCase(nombreNormalizado)) {
            throw new IllegalArgumentException(
                "Ya existe un servicio con ese nombre"
            );
        }

        Servicio servicio = new Servicio();

        servicio.setNombre(nombreNormalizado);
        servicio.setDescripcion(request.getDescripcion().trim());
        servicio.setPrecio(request.getPrecio());
        servicio.setDuracionMinutos(
            request.getDuracionMinutos()
        );
        servicio.setImagenUrl(request.getImagenUrl());
        servicio.setActivo(true);

        Servicio guardado =
            servicioRepository.save(servicio);

        return convertirRespuesta(guardado);
    }

    @Transactional(readOnly = true)
    public List<ServicioResponse> listarActivos() {
        return servicioRepository
            .findByActivoTrueOrderByNombreAsc()
            .stream()
            .map(this::convertirRespuesta)
            .toList();
    }

    @Transactional(readOnly = true)
    public ServicioResponse obtenerPorId(UUID id) {
        Servicio servicio = buscarEntidad(id);

        if (!Boolean.TRUE.equals(servicio.getActivo())) {
            throw new NoSuchElementException(
                "El servicio no se encuentra disponible"
            );
        }

        return convertirRespuesta(servicio);
    }

    @Transactional
    public ServicioResponse actualizar(
            UUID id,
            ServicioRequest request
    ) {
        Servicio servicio = buscarEntidad(id);

        String nombreNormalizado = request.getNombre().trim();

        boolean cambioNombre =
            !servicio.getNombre()
                .equalsIgnoreCase(nombreNormalizado);

        if (cambioNombre &&
            servicioRepository.existsByNombreIgnoreCase(
                nombreNormalizado
            )) {
            throw new IllegalArgumentException(
                "Ya existe un servicio con ese nombre"
            );
        }

        servicio.setNombre(nombreNormalizado);
        servicio.setDescripcion(request.getDescripcion().trim());
        servicio.setPrecio(request.getPrecio());
        servicio.setDuracionMinutos(
            request.getDuracionMinutos()
        );
        servicio.setImagenUrl(request.getImagenUrl());

        return convertirRespuesta(
            servicioRepository.save(servicio)
        );
    }

    @Transactional
    public ServicioResponse desactivar(UUID id) {
        Servicio servicio = buscarEntidad(id);

        servicio.setActivo(false);

        return convertirRespuesta(
            servicioRepository.save(servicio)
        );
    }

    private Servicio buscarEntidad(UUID id) {
        return servicioRepository
            .findById(id)
            .orElseThrow(() ->
                new NoSuchElementException(
                    "Servicio no encontrado"
                )
            );
    }

    private ServicioResponse convertirRespuesta(
            Servicio servicio
    ) {
        return new ServicioResponse(
            servicio.getId(),
            servicio.getNombre(),
            servicio.getDescripcion(),
            servicio.getPrecio(),
            servicio.getDuracionMinutos(),
            servicio.getImagenUrl(),
            servicio.getActivo(),
            servicio.getCreadoEn(),
            servicio.getActualizadoEn()
        );
    }
}