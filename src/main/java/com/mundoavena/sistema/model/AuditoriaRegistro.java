package com.mundoavena.sistema.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_registros")
@Data
public class AuditoriaRegistro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 100)
    private String accion;

    @Column(nullable = false, length = 100)
    private String tipoFormulario;

    @Column
    private Long registroId;

    @Column(length = 500)
    private String detalle;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(length = 50)
    private String ipAddress;

    @PrePersist
    public void prePersist() {
        fechaHora = LocalDateTime.now();
    }
}