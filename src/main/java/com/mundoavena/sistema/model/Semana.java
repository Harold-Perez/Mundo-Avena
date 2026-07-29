package com.mundoavena.sistema.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "semanas")
@Data
public class Semana {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false)
    private boolean cerrada = false;

    @Column
    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {
        fechaCreacion = LocalDateTime.now();
    }
}