package com.lashbook.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ServicioBusquedaInicializador
    implements ApplicationRunner {

    private static final Logger logger =
        LoggerFactory.getLogger(
            ServicioBusquedaInicializador.class
        );

    private final ServicioBusquedaService
        servicioBusquedaService;

    public ServicioBusquedaInicializador(
        ServicioBusquedaService servicioBusquedaService
    ) {
        this.servicioBusquedaService =
            servicioBusquedaService;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            servicioBusquedaService
                .sincronizarServiciosActivos();

            logger.info(
                "Servicios sincronizados con Elasticsearch"
            );
        } catch (Exception error) {
            logger.warn(
                "No fue posible sincronizar Elasticsearch: {}",
                error.getMessage()
            );
        }
    }
}