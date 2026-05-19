package com.mundoavena.sistema.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "control_diario_pt")
@Data
public class ControlDiarioPT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 200)
    private String producto;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(length = 50)
    private String lote;

    @Column
    private LocalDate fechaVencimiento;

    @Column
    private LocalTime horaInicio;

    @Column
    private LocalTime horaFin;

    @Column(length = 200)
    private String operadores;

    @Column(length = 200)
    private String envasadores;

    // Total unidades envasadas
    @Column
    private Integer totalUnidades;

    // Total sacos fabricados
    @Column
    private Integer totalSacos;

    // Unidades por saco
    @Column
    private Integer unidadesPorSaco;

    // Total fardos fabricados
    @Column
    private Integer totalFardos;

    // Total producto empacado kg
    @Column
    private Double totalProductoEmpacadoKg;

    // Materia prima utilizada kg
    @Column
    private Double materiaPrimaUtilizadaKg;

    // Proveniente de produccion
    @Column
    private Boolean provieneDeProdccion = false;

    // Tipo de producto
    @Column private Boolean esHojuelaAvena = false;
    @Column private Boolean esHarinaAvena = false;
    @Column private Boolean esHojuelaIntegral = false;
    @Column private Boolean esHarinaIntegral = false;
    @Column private Boolean esOtro = false;

    @Column(length = 500)
    private String observaciones;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    public void prePersist() {
        fechaRegistro = LocalDateTime.now();
    }
}