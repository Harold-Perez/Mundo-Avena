package com.mundoavena.sistema.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "detalle_costo_producto")
@Data
public class DetalleCostoPorProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cierre_id", nullable = false)
    private CierreMensual cierre;

    @Column(nullable = false, length = 100)
    private String producto;

    @Column(nullable = false, length = 50)
    private String presentacion;

    // Produccion
    @Column(nullable = false)
    private Integer unidadesProducidas;

    @Column(nullable = false)
    private Double kgProducidos;

    // Costos MP
    @Column(nullable = false)
    private Double costoAvena = 0.0;

    @Column(nullable = false)
    private Double costoHarina = 0.0;

    @Column(nullable = false)
    private Double costoCarbonato = 0.0;

    @Column(nullable = false)
    private Double costoVitaminas = 0.0;

    @Column(nullable = false)
    private Double costoEmpaque = 0.0;

    // Costos conversion
    @Column(nullable = false)
    private Double costoEE = 0.0;

    @Column(nullable = false)
    private Double costoVapor = 0.0;

    @Column(nullable = false)
    private Double costoMO = 0.0;

    // Totales
    @Column(nullable = false)
    private Double costoTotalQ = 0.0;

    @Column(nullable = false)
    private Double costoUnitarioQ = 0.0;

    @Column(nullable = false)
    private Double costoUnitarioUSD = 0.0;
}