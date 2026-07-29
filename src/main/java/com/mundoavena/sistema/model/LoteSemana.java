package com.mundoavena.sistema.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "lotes_semana")
@Data
public class LoteSemana {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "semana_id", nullable = false)
    private Semana semana;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private String numeroLote;      // "4725AC24", "C-041", o "S/N" si no aplica (como en Descascarado)

    @Column
    private String ubicacion;       // solo aplica a Bodega B ("Ubicación 1", "Ubicación 2"...); null para las demás

    @Column(nullable = false)
    private double cantidadInicial; // saldo con el que arrancó esta semana (arrastrado de la semana anterior, o 0 si es lote nuevo)

    @Column(nullable = false)
    private double saldoActual;     // se actualiza con cada MovimientoLote

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoLote estado = EstadoLote.ACTIVO;

    public enum EstadoLote {
        ACTIVO, SALDO_BAJO, AGOTADO
    }
}