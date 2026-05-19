package com.mundoavena.sistema.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "control_pesos_granel")
@Data
public class ControlPesosGranel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 100)
    private String producto;

    @Column(length = 50)
    private String lote;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column
    private LocalTime horaInicio;

    @Column
    private LocalTime horaFin;

    @Column(length = 200)
    private String operador;

    @Column(length = 200)
    private String envasadores;

    // Pesos de sacos (hasta 70 sacos por formulario)
    @Column private Double saco1;  @Column private Double saco2;
    @Column private Double saco3;  @Column private Double saco4;
    @Column private Double saco5;  @Column private Double saco6;
    @Column private Double saco7;  @Column private Double saco8;
    @Column private Double saco9;  @Column private Double saco10;
    @Column private Double saco11; @Column private Double saco12;
    @Column private Double saco13; @Column private Double saco14;
    @Column private Double saco15; @Column private Double saco16;
    @Column private Double saco17; @Column private Double saco18;
    @Column private Double saco19; @Column private Double saco20;
    @Column private Double saco21; @Column private Double saco22;
    @Column private Double saco23; @Column private Double saco24;
    @Column private Double saco25; @Column private Double saco26;
    @Column private Double saco27; @Column private Double saco28;
    @Column private Double saco29; @Column private Double saco30;
    @Column private Double saco31; @Column private Double saco32;
    @Column private Double saco33; @Column private Double saco34;
    @Column private Double saco35; @Column private Double saco36;
    @Column private Double saco37; @Column private Double saco38;
    @Column private Double saco39; @Column private Double saco40;
    @Column private Double saco41; @Column private Double saco42;
    @Column private Double saco43; @Column private Double saco44;
    @Column private Double saco45; @Column private Double saco46;
    @Column private Double saco47; @Column private Double saco48;
    @Column private Double saco49; @Column private Double saco50;
    @Column private Double saco51; @Column private Double saco52;
    @Column private Double saco53; @Column private Double saco54;
    @Column private Double saco55; @Column private Double saco56;
    @Column private Double saco57; @Column private Double saco58;
    @Column private Double saco59; @Column private Double saco60;
    @Column private Double saco61; @Column private Double saco62;
    @Column private Double saco63; @Column private Double saco64;
    @Column private Double saco65; @Column private Double saco66;
    @Column private Double saco67; @Column private Double saco68;
    @Column private Double saco69; @Column private Double saco70;

    @Column
    private Double totalKg;

    @Column
    private Integer totalSacos;

    @Column(length = 500)
    private String observaciones;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    public void prePersist() {
        fechaRegistro = LocalDateTime.now();
    }
}