package com.mundoavena.sistema.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "monitoreo_pcc2")
@Data
public class MonitoreoPCC2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Info general
    @Column(nullable = false, length = 200)
    private String producto;

    @Column(length = 50)
    private String lotePT;

    @Column
    private LocalDate fechaProduccion;

    @Column
    private LocalDate fechaVencimiento;

    // Materia prima granel
    @Column(length = 100)
    private String loteMPGranel;

    @Column
    private Double cantidadMPGranel;

    @Column
    private Double reproceseMPGranel;

    @Column
    private Double totalMPGranel;

    // Tipo de producto
    @Column private Boolean esHojuelaAvena = false;
    @Column private Boolean esHarinaAvena = false;
    @Column private Boolean esHojuelaIntegral = false;
    @Column private Boolean esHarinaIntegral = false;
    @Column private Boolean esOtro = false;

    // Proceso de empaque - linea 1
    @Column(length = 100) private String empaque1Presentacion;
    @Column(length = 50)  private String empaque1Lote;
    @Column private LocalDate empaque1FechaInicio;
    @Column private LocalDate empaque1FechaFin;
    @Column private LocalTime empaque1HoraInicio;
    @Column private LocalTime empaque1HoraFin;

    // Proceso de empaque - linea 2
    @Column(length = 100) private String empaque2Presentacion;
    @Column(length = 50)  private String empaque2Lote;
    @Column private LocalDate empaque2FechaInicio;
    @Column private LocalDate empaque2FechaFin;
    @Column private LocalTime empaque2HoraInicio;
    @Column private LocalTime empaque2HoraFin;

    // Monitoreo bolsas individuales (24 lecturas)
    @Column private Double bolsa1;  @Column private Double bolsa2;
    @Column private Double bolsa3;  @Column private Double bolsa4;
    @Column private Double bolsa5;  @Column private Double bolsa6;
    @Column private Double bolsa7;  @Column private Double bolsa8;
    @Column private Double bolsa9;  @Column private Double bolsa10;
    @Column private Double bolsa11; @Column private Double bolsa12;
    @Column private Double bolsa13; @Column private Double bolsa14;
    @Column private Double bolsa15; @Column private Double bolsa16;
    @Column private Double bolsa17; @Column private Double bolsa18;
    @Column private Double bolsa19; @Column private Double bolsa20;
    @Column private Double bolsa21; @Column private Double bolsa22;
    @Column private Double bolsa23; @Column private Double bolsa24;

    // Monitoreo sacos (24 lecturas)
    @Column private Double sacoM1;  @Column private Double sacoM2;
    @Column private Double sacoM3;  @Column private Double sacoM4;
    @Column private Double sacoM5;  @Column private Double sacoM6;
    @Column private Double sacoM7;  @Column private Double sacoM8;
    @Column private Double sacoM9;  @Column private Double sacoM10;
    @Column private Double sacoM11; @Column private Double sacoM12;
    @Column private Double sacoM13; @Column private Double sacoM14;
    @Column private Double sacoM15; @Column private Double sacoM16;
    @Column private Double sacoM17; @Column private Double sacoM18;
    @Column private Double sacoM19; @Column private Double sacoM20;
    @Column private Double sacoM21; @Column private Double sacoM22;
    @Column private Double sacoM23; @Column private Double sacoM24;

    // Verificacion detector metales
    @Column private Boolean detectorMetalesCumple = false;

    // Rendimiento - Materia prima
    @Column private Double mpCantidadKg;
    @Column private Double mpProductoTerminadoKg;
    @Column private Double mpMermaKg;
    @Column private Double mpReprocesoKg;
    @Column private Double mpTotalKg;
    @Column private Double mpRendimientoPct;

    // Rendimiento - Material de empaque
    @Column private Double empCantidadKg;
    @Column private Double empProductoTerminadoKg;
    @Column private Double empMermaKg;
    @Column private Double empReprocesoKg;
    @Column private Double empTotalKg;
    @Column private Double empRendimientoPct;

    // Personal
    @Column(length = 200)
    private String personalCalidad;

    @Column(length = 500)
    private String observaciones;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    public void prePersist() {
        fechaRegistro = LocalDateTime.now();
    }
}