package com.mundoavena.sistema.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "conciliacion_materiales")
@Data
public class ConciliacionMateriales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, length = 20)
    private String turno;

    @Column(nullable = false, length = 100)
    private String producto;

    @Column(length = 50)
    private String lote;

    // Material de empaque
    @Column private Double empaque_utilizado;
    @Column private Double empaque_merma;
    @Column private Double empaque_total;

    // Materia prima
    @Column private Double mp_granel;
    @Column private Double mp_merma;
    @Column private Double mp_total;

    @Column(length = 500)
    private String observaciones;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    public void prePersist() {
        fechaRegistro = LocalDateTime.now();
    }
}