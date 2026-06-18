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

    @Column(nullable = false)
    private Integer unidadesProducidas;

    @Column(nullable = false)
    private Double kgProducidos;

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

    @Column(nullable = false)
    private Double costoEE = 0.0;

    @Column(nullable = false)
    private Double costoVapor = 0.0;

    @Column(nullable = false)
    private Double costoMO = 0.0;

    @Column(nullable = false)
    private Double costoTotalQ = 0.0;

    @Column(nullable = false)
    private Double costoUnitarioQ = 0.0;

    @Column(nullable = false)
    private Double costoUnitarioUSD = 0.0;

    // =============================================
    // Campos de formateo — NO se persisten en BD
    // =============================================
    @Transient
    private String costoAvenaFmt;

    @Transient
    private String costoEEFmt;

    @Transient
    private String costoVaporFmt;

    @Transient
    private String costoMOFmt;

    @Transient
    private String costoTotalQFmt;

    @Transient
    private String costoUnitarioQFmt;

    @Transient
    private String costoUnitarioUSDFmt;

    @Transient
    private String kgProducidosFmt;
}