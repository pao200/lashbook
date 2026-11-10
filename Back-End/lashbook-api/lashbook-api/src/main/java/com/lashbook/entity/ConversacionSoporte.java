package com.lashbook.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conversaciones_soporte")
@Getter
@Setter
public class ConversacionSoporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Clienta que creó la conversación.
     * Se relaciona con usuarios.id, que en tu proyecto es UUID.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "clienta_id", nullable = false)
    private Usuario clienta;

    @Column(nullable = false, length = 150)
    private String asunto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoConversacionSoporte estado;

    @Column(name = "creado_en", nullable = false)
    private OffsetDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private OffsetDateTime actualizadoEn;

    @OneToMany(
            mappedBy = "conversacion",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("enviadoEn ASC")
    private List<MensajeSoporte> mensajes = new ArrayList<>();

    @PrePersist
    public void prePersist() {

        OffsetDateTime ahora = OffsetDateTime.now();

        if (estado == null) {
            estado = EstadoConversacionSoporte.ABIERTA;
        }

        if (creadoEn == null) {
            creadoEn = ahora;
        }

        if (actualizadoEn == null) {
            actualizadoEn = ahora;
        }
    }

    @PreUpdate
    public void preUpdate() {
        actualizadoEn = OffsetDateTime.now();
    }
}