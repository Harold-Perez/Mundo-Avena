package com.mundoavena.sistema.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "cierres_mensuales")
@Data
public class CierreMensual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer mes;

    @Column(nullable = false)
    private Integer anio;

    // Costos de conversion por kg
    @Column(nullable = false)
    private Double costoEEPorKg = 0.1640;

    @Column(nullable = false)
    private Double costoVaporPorKg = 0.1324;

    @Column(nullable = false)
    private Double costoMOPorKg = 0.5292;

    // Costos materia prima por kg
    @Column(nullable = false)
    private Double costoAvenaKg = 3.1857;

    @Column(nullable = false)
    private Double costoHarinaKg = 5.19;

    @Column(nullable = false)
    private Double costoCarbonatoKg = 2.0089;

    @Column(nullable = false)
    private Double costoVitaminasKg = 181.15;

    // Proporciones de MP por kg de PT
    @Column(nullable = false)
    private Double propHarinaKg = 0.014218;

    @Column(nullable = false)
    private Double propCarbonatoKg = 0.036440;

    @Column(nullable = false)
    private Double propVitaminasKg = 0.001380;

    // Costos de empaque por unidad segun presentacion
    @Column(nullable = false)
    private Double empNutremas1200g = 0.60894;

    @Column(nullable = false)
    private Double empNutremas900g = 0.41769;

    @Column(nullable = false)
    private Double empNutremasRTD900g = 0.42;

    @Column(nullable = false)
    private Double empNutremasFrescos600g = 0.34425;

    @Column(nullable = false)
    private Double empNutremas600g = 0.34425;

    @Column(nullable = false)
    private Double empRicoMosh360g = 0.23945;

    @Column(nullable = false)
    private Double empAvenaEstrella50lb = 2.88949;

    // Tipo de cambio
    @Column(nullable = false)
    private Double tipoCambio = 7.65;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCierre estado = EstadoCierre.BORRADOR;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario calculadoPor;

    @Column(nullable = false)
    private LocalDateTime fechaCalculo;

    @PrePersist
    public void prePersist() {
        fechaCalculo = LocalDateTime.now();
    }
}