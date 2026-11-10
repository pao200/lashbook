package com.lashbook.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "mensajes_soporte")
@Getter
@Setter
public class MensajeSoporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Conversación a la que pertenece el mensaje.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversacion_id", nullable = false)
    private ConversacionSoporte conversacion;

    /*
     * Puede ser CLIENTA o LASHISTA.
     * Ambos ya existen dentro de la tabla usuarios.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "remitente_id", nullable = false)
    private Usuario remitente;

    @Column(nullable = false, length = 2000)
    private String mensaje;

    @Column(nullable = false)
    private Boolean leido = false;

    @Column(name = "enviado_en", nullable = false)
    private OffsetDateTime enviadoEn;

    @PrePersist
    public void prePersist() {

        if (leido == null) {
            leido = false;
        }

        if (enviadoEn == null) {
            enviadoEn = OffsetDateTime.now();
        }
    }
}