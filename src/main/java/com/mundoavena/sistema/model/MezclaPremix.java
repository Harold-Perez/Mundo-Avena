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
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Datos generales
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String operador;
    private String lotePremix;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    // Sección 3.1 — Porcentajes
    private Double pctVitamina = 2.7;
    private Double pctCarbonato = 70.0;
    private Double pctHarina = 27.3;
    private Double pctTotal = 100.0;

    // Sección 3.1 — Cantidades
    private Double cantVitamina1 = 0.0;
    private Double cantVitamina2 = 0.0;
    private Double cantCarbonato1 = 0.0;
    private Double cantCarbonato2 = 0.0;
    private Double cantHarina1 = 0.0;
    private Double cantHarina2 = 0.0;
    private Double cantTotal1 = 0.0;
    private Double cantTotal2 = 0.0;

    // Sección 3.2 — Fila 1
    private String loteHarinaMp1; private Double cantHarinaMp1 = 0.0;
    private String loteCarbonatoMp1; private Double cantCarbonatoMp1 = 0.0;
    private String loteVitaminasMp1; private Double cantVitaminasMp1 = 0.0;
    private Double devBodega1 = 0.0;

    // Fila 2
    private String loteHarinaMp2; private Double cantHarinaMp2 = 0.0;
    private String loteCarbonatoMp2; private Double cantCarbonatoMp2 = 0.0;
    private String loteVitaminasMp2; private Double cantVitaminasMp2 = 0.0;
    private Double devBodega2 = 0.0;

    // Fila 3
    private String loteHarinaMp3; private Double cantHarinaMp3 = 0.0;
    private String loteCarbonatoMp3; private Double cantCarbonatoMp3 = 0.0;
    private String loteVitaminasMp3; private Double cantVitaminasMp3 = 0.0;
    private Double devBodega3 = 0.0;

    // Fila 4
    private String loteHarinaMp4; private Double cantHarinaMp4 = 0.0;
    private String loteCarbonatoMp4; private Double cantCarbonatoMp4 = 0.0;
    private String loteVitaminasMp4; private Double cantVitaminasMp4 = 0.0;
    private Double devBodega4 = 0.0;

    // Fila 5
    private String loteHarinaMp5; private Double cantHarinaMp5 = 0.0;
    private String loteCarbonatoMp5; private Double cantCarbonatoMp5 = 0.0;
    private String loteVitaminasMp5; private Double cantVitaminasMp5 = 0.0;
    private Double devBodega5 = 0.0;

    // Fila 6
    private String loteHarinaMp6; private Double cantHarinaMp6 = 0.0;
    private String loteCarbonatoMp6; private Double cantCarbonatoMp6 = 0.0;
    private String loteVitaminasMp6; private Double cantVitaminasMp6 = 0.0;
    private Double devBodega6 = 0.0;

    // Fila 7
    private String loteHarinaMp7; private Double cantHarinaMp7 = 0.0;
    private String loteCarbonatoMp7; private Double cantCarbonatoMp7 = 0.0;
    private String loteVitaminasMp7; private Double cantVitaminasMp7 = 0.0;
    private Double devBodega7 = 0.0;

    // Fila 8
    private String loteHarinaMp8; private Double cantHarinaMp8 = 0.0;
    private String loteCarbonatoMp8; private Double cantCarbonatoMp8 = 0.0;
    private String loteVitaminasMp8; private Double cantVitaminasMp8 = 0.0;
    private Double devBodega8 = 0.0;

    // Fila 9
    private String loteHarinaMp9; private Double cantHarinaMp9 = 0.0;
    private String loteCarbonatoMp9; private Double cantCarbonatoMp9 = 0.0;
    private String loteVitaminasMp9; private Double cantVitaminasMp9 = 0.0;
    private Double devBodega9 = 0.0;

    private String observaciones;

    @PrePersist
    public void prePersist() {
        fechaRegistro = LocalDateTime.now();
    }
}