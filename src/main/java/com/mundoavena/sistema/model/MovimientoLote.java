package com.mundoavena.sistema.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_lote")
@Data
public class MovimientoLote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lote_semana_id", nullable = false)
    private LoteSemana loteSemana;

    @Column(nullable = false)
    private LocalDate fecha;          // fecha real del movimiento, ej. 22/07/2026 — no solo "qué día de la semana"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimiento tipo;

    @Column(nullable = false)
    private double cantidad;          // siempre positiva; el signo lo da el campo "tipo", no un número negativo

    @Column
    private Double pesoKg;   // peso del movimiento en kg; null si esa bodega no lo usa (Grano)

    @Column
    private String ticketBascula;     // solo aplica a Grano; null para las demás bodegas

    @Column
    private String motivo;   // solo aplica a movimientos tipo AJUSTE — por qué se corrigió el saldo

    @Column
    private String registradoPor;     // username de quien lo registró — para auditoría

    @Column
    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {
        fechaCreacion = LocalDateTime.now();
    }

    public enum TipoMovimiento {
        ENTRADA, SALIDA, AJUSTE
    }
}