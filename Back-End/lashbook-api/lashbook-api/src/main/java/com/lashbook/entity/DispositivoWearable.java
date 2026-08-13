package com.lashbook.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "dispositivos_wearable",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_dispositivo_wearable_token",
            columnNames = "token_fcm"
        )
    }
)
@Getter
@Setter
public class DispositivoWearable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "usuario_id",
        nullable = false
    )
    private Usuario usuario;

    @Column(
        name = "token_fcm",
        nullable = false,
        length = 500
    )
    private String tokenFcm;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(
        name = "creado_en",
        nullable = false
    )
    private LocalDateTime creadoEn;

    @Column(
        name = "actualizado_en",
        nullable = false
    )
    private LocalDateTime actualizadoEn;

    @PrePersist
    public void prePersist() {
        LocalDateTime ahora = LocalDateTime.now();

        creadoEn = ahora;
        actualizadoEn = ahora;

        if (activo == null) {
            activo = true;
        }
    }

    @PreUpdate
    public void preUpdate() {
        actualizadoEn = LocalDateTime.now();
    }
}