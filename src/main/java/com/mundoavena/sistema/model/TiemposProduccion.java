package com.mundoavena.sistema.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "tiempos_produccion")
@Data
public class TiemposProduccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, length = 50)
    private String operador;

    // Motores
    @Column
    private LocalTime motoresInicio;

    @Column
    private LocalTime motoresFin;

    // Produccion
    @Column
    private LocalTime produccionInicio;

    @Column
    private LocalTime produccionFin;

    // Contador de agua
    @Column
    private String contadorAguaInicio;

    @Column
    private String contadorAguaFin;

    @Column(length = 500)
    private String observaciones;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    public void prePersist() {
        fechaRegistro = LocalDateTime.now();
    }
}