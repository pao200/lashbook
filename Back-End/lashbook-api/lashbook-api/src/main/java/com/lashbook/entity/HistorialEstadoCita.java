package com.lashbook.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "historial_estado_cita")
@Getter
@Setter
public class HistorialEstadoCita {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cita_id", nullable = false)
    private Cita cita;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior", length = 20)
    private EstadoCita estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo", nullable = false, length = 20)
    private EstadoCita estadoNuevo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrigenCambio origen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_actor_id")
    private Usuario usuarioActor;

    @Column(length = 500)
    private String detalle;

    @Column(name = "fecha_cambio", nullable = false)
    private LocalDateTime fechaCambio;

    @PrePersist
    public void prePersist() {
        if (fechaCambio == null) {
            fechaCambio = LocalDateTime.now();
        }
    }
}