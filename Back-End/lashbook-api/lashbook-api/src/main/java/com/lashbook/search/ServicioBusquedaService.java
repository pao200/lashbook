package com.lashbook.search;

import com.lashbook.entity.Servicio;
import com.lashbook.repository.ServicioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ServicioBusquedaService {

    private final ServicioRepository servicioRepository;
    private final ServicioBusquedaRepository servicioBusquedaRepository;

    public ServicioBusquedaService(
        ServicioRepository servicioRepository,
        ServicioBusquedaRepository servicioBusquedaRepository
    ) {
        this.servicioRepository = servicioRepository;
        this.servicioBusquedaRepository = servicioBusquedaRepository;
    }

    public void sincronizarServiciosActivos() {
        List<Servicio> servicios =
            servicioRepository.findByActivoTrueOrderByNombreAsc();

        servicioBusquedaRepository.deleteAll();

        List<ServicioBusquedaDocument> documentos =
            servicios.stream()
                .map(this::convertirDocumento)
                .toList();

        servicioBusquedaRepository.saveAll(documentos);
    }

    public void indexarServicio(
        Servicio servicio
    ) {
        if (
            servicio == null ||
            servicio.getId() == null
        ) {
            return;
        }

        if (!Boolean.TRUE.equals(servicio.getActivo())) {
            eliminarServicio(servicio.getId());
            return;
        }

        ServicioBusquedaDocument documento =
            convertirDocumento(servicio);

        servicioBusquedaRepository.save(documento);
    }

    public void eliminarServicio(
        UUID servicioId
    ) {
        if (servicioId == null) {
            return;
        }

        servicioBusquedaRepository.deleteById(
            servicioId.toString()
        );
    }

    public List<ServicioBusquedaDocument> buscarPredictivo(
        String texto
    ) {
        if (
            texto == null ||
            texto.trim().length() < 2
        ) {
            return List.of();
        }

        return servicioBusquedaRepository
            .buscarPredictivo(texto.trim())
            .stream()
            .filter(documento ->
                Boolean.TRUE.equals(
                    documento.getActivo()
                )
            )
            .limit(6)
            .toList();
    }

    private ServicioBusquedaDocument convertirDocumento(
        Servicio servicio
    ) {
        return new ServicioBusquedaDocument(
            servicio.getId().toString(),
            servicio.getNombre(),
            servicio.getDescripcion(),
            servicio.getPrecio().doubleValue(),
            servicio.getDuracionMinutos(),
            servicio.getImagenUrl(),
            servicio.getActivo()
        );
    }
}