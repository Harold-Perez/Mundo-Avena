package com.mundoavena.sistema.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "produccion_granel")
@Data
public class ProduccionGranel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, length = 20)
    private String turno;

    @Column private Double lecturaInicialBalanza;
    @Column private Double lecturaFinalBalanza;

    // Grano de Avena con Cascara
    @Column private String loteGranoAvena;
    @Column private Integer cantidadSacosGranoAvena;
    @Column private Double pesoSacosGranoAvena;
    @Column private Double totalGranoAvena;

    // Grano de Avena con Cascara (Arranque)
    @Column private String loteGranoAvenaArranque;
    @Column private Integer cantidadSacosGranoAvenaArranque;
    @Column private Double pesoSacosGranoAvenaArranque;
    @Column private Double totalGranoAvenaArranque;

    // Grano de Avena con Cascara (Maquinaria)
    @Column private String loteGranoAvenaMaquinaria;
    @Column private Integer cantidadSacosGranoAvenaMaquinaria;
    @Column private Double pesoSacosGranoAvenaMaquinaria;
    @Column private Double totalGranoAvenaMaquinaria;

    // Cascara
    @Column private String loteCascara;
    @Column private Integer cantidadSacosCascara;
    @Column private Double pesoSacosCascara;
    @Column private Double totalCascara;

    // Avenilla (Zaranda)
    @Column private String loteAvenilla;
    @Column private Integer cantidadSacosAvenilla;
    @Column private Double pesoSacosAvenilla;
    @Column private Double totalAvenilla;

    // Cebada Granos Extraños
    @Column private String loteCebada;
    @Column private Integer cantidadSacosCebada;
    @Column private Double pesoSacosCebada;
    @Column private Double totalCebada;

    // Grano Avena Quebrado Filtro 4
    @Column private String loteGranoQuebrado;
    @Column private Integer cantidadSacosGranoQuebrado;
    @Column private Double pesoSacosGranoQuebrado;
    @Column private Double totalGranoQuebrado;

    // Groats
    @Column private String loteGroats;
    @Column private Integer cantidadSacosGroats;
    @Column private Double pesoSacosGroats;
    @Column private Double totalGroats;

    // Fibra de Avena
    @Column private String loteFibraAvena;
    @Column private Integer cantidadSacosFibraAvena;
    @Column private Double pesoSacosFibraAvena;
    @Column private Double totalFibraAvena;

    // Fibra de Avena Rechazo
    @Column private String loteFibraAvenaRechazo;
    @Column private Integer cantidadSacosFibraAvenaRechazo;
    @Column private Double pesoSacosFibraAvenaRechazo;
    @Column private Double totalFibraAvenaRechazo;

    // Grano Perlado
    @Column private String loteGranoPerlado;
    @Column private Integer cantidadSacosGranoPerlado;
    @Column private Double pesoSacosGranoPerlado;
    @Column private Double totalGranoPerlado;

    // Grano Cortado Groat Cortadoras
    @Column private String loteGranoCortado;
    @Column private Integer cantidadSacosGranoCortado;
    @Column private Double pesoSacosGranoCortado;
    @Column private Double totalGranoCortado;

    // Hojuela Mosh
    @Column private String loteHojuelaMosh;
    @Column private Integer cantidadSacosHojuelaMosh;
    @Column private Double pesoSacosHojuelaMosh;
    @Column private Double totalHojuelaMosh;

    // Hojuela Mosh Arranque
    @Column private String loteHojuelaMoshArranque;
    @Column private Integer cantidadSacosHojuelaMoshArranque;
    @Column private Double pesoSacosHojuelaMoshArranque;
    @Column private Double totalHojuelaMoshArranque;

    // Hojuelon
    @Column private String loteHojuelon;
    @Column private Integer cantidadSacosHojuelon;
    @Column private Double pesoSacosHojuelon;
    @Column private Double totalHojuelon;

    // Harina OTW
    @Column private String loteHarinaOTW;
    @Column private Integer cantidadSacosHarinaOTW;
    @Column private Double pesoSacosHarinaOTW;
    @Column private Double totalHarinaOTW;

    // Crema de Avena
    @Column private String loteCremaAvena;
    @Column private Integer cantidadSacosCremaAvena;
    @Column private Double pesoSacosCremaAvena;
    @Column private Double totalCremaAvena;

    // Hojuela Integral
    @Column private String loteHojuelaIntegral;
    @Column private Integer cantidadSacosHojuelaIntegral;
    @Column private Double pesoSacosHojuelaIntegral;
    @Column private Double totalHojuelaIntegral;

    // Harina de Avena
    @Column private String loteHarinaAvena;
    @Column private Integer cantidadSacosHarinaAvena;
    @Column private Double pesoSacosHarinaAvena;
    @Column private Double totalHarinaAvena;

    // Granola
    @Column private String loteGranola;
    @Column private Integer cantidadSacosGranola;
    @Column private Double pesoSacosGranola;
    @Column private Double totalGranola;

    // Carbonato de Calcio
    @Column private String loteCarbonatoCalcio;
    @Column private Integer cantidadSacosCarbonatoCalcio;
    @Column private Double pesoSacosCarbonatoCalcio;
    @Column private Double totalCarbonatoCalcio;

    // Vitaminas Premezcla
    @Column private String loteVitaminas;
    @Column private Integer cantidadSacosVitaminas;
    @Column private Double pesoSacosVitaminas;
    @Column private Double totalVitaminas;

    // Tipo de saco
    @Column private Boolean sacoBlanco = false;
    @Column private Boolean sacoRojoMundoAvena = false;
    @Column private Boolean sacoAvenaEstrella = false;
    @Column private Boolean sacoOtros = false;

    @Column(length = 500)
    private String observaciones;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    public void prePersist() {
        fechaRegistro = LocalDateTime.now();
    }
}