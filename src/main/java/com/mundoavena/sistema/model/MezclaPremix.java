package com.mundoavena.sistema.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "mezcla_premix")
@Data
public class MezclaPremix {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDate fecha;

    // Carga 1
    @Column private String presentacion1;
    @Column private String lote1;
    @Column private LocalTime horaInicio1;
    @Column private LocalTime horaFin1;

    // Carga 2
    @Column private String presentacion2;
    @Column private String lote2;
    @Column private LocalTime horaInicio2;
    @Column private LocalTime horaFin2;

    // Carga 3
    @Column private String presentacion3;
    @Column private String lote3;
    @Column private LocalTime horaInicio3;
    @Column private LocalTime horaFin3;

    // Carga 4
    @Column private String presentacion4;
    @Column private String lote4;
    @Column private LocalTime horaInicio4;
    @Column private LocalTime horaFin4;

    // Carga 5
    @Column private String presentacion5;
    @Column private String lote5;
    @Column private LocalTime horaInicio5;
    @Column private LocalTime horaFin5;

    // Carga 6
    @Column private String presentacion6;
    @Column private String lote6;
    @Column private LocalTime horaInicio6;
    @Column private LocalTime horaFin6;

    // Carga 7
    @Column private String presentacion7;
    @Column private String lote7;
    @Column private LocalTime horaInicio7;
    @Column private LocalTime horaFin7;

    // Carga 8
    @Column private String presentacion8;
    @Column private String lote8;
    @Column private LocalTime horaInicio8;
    @Column private LocalTime horaFin8;

    // Carga 9
    @Column private String presentacion9;
    @Column private String lote9;
    @Column private LocalTime horaInicio9;
    @Column private LocalTime horaFin9;

    // Carga 10
    @Column private String presentacion10;
    @Column private String lote10;
    @Column private LocalTime horaInicio10;
    @Column private LocalTime horaFin10;

    @Column(length = 500)
    private String observaciones;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    public void prePersist() {
        fechaRegistro = LocalDateTime.now();
    }
}