package com.lashbook.service;

import com.lashbook.entity.DispositivoWearable;
import com.lashbook.entity.Usuario;
import com.lashbook.repository.DispositivoWearableRepository;
import com.lashbook.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class DispositivoWearableService {

    private final DispositivoWearableRepository
            dispositivoWearableRepository;

    private final UsuarioRepository
            usuarioRepository;

    public DispositivoWearableService(
            DispositivoWearableRepository
                    dispositivoWearableRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.dispositivoWearableRepository =
                dispositivoWearableRepository;

        this.usuarioRepository =
                usuarioRepository;
    }

    @Transactional
    public void registrarDispositivo(
            UUID usuarioId,
            String tokenFcm
    ) {
        if (
                tokenFcm == null ||
                tokenFcm.isBlank()
        ) {
            throw new IllegalArgumentException(
                    "El token FCM es obligatorio"
            );
        }

        Usuario usuario =
                usuarioRepository
                        .findById(usuarioId)
                        .orElseThrow(
                                () ->
                                        new NoSuchElementException(
                                                "Usuario no encontrado"
                                        )
                        );

        String tokenLimpio =
                tokenFcm.trim();

        /*
         * Desactivamos los tokens anteriores de la
         * misma clienta. Esto evita notificaciones
         * duplicadas después de reinstalar la app
         * o recrear el emulador.
         */
        List<DispositivoWearable>
                dispositivosActivos =
                dispositivoWearableRepository
                        .findByUsuario_IdAndActivoTrue(
                                usuarioId
                        );

        for (
                DispositivoWearable dispositivoAnterior :
                dispositivosActivos
        ) {
            if (
                    !tokenLimpio.equals(
                            dispositivoAnterior
                                    .getTokenFcm()
                    )
            ) {
                dispositivoAnterior.setActivo(
                        false
                );
            }
        }

        dispositivoWearableRepository.saveAll(
                dispositivosActivos
        );

        /*
         * Si el token ya existía, lo reutilizamos.
         * Si es nuevo, creamos un registro.
         */
        DispositivoWearable dispositivoActual =
                dispositivoWearableRepository
                        .findByTokenFcm(
                                tokenLimpio
                        )
                        .orElseGet(
                                DispositivoWearable::new
                        );

        dispositivoActual.setUsuario(usuario);
        dispositivoActual.setTokenFcm(
                tokenLimpio
        );
        dispositivoActual.setActivo(true);

        dispositivoWearableRepository.save(
                dispositivoActual
        );
    }
}