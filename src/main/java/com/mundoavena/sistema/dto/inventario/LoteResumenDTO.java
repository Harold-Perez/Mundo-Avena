package com.mundoavena.sistema.dto.inventario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoteResumenDTO {
    private String numeroLote;
    private String categoria;
    private String producto;
    private double cantidadInicial;
    private double entradas;
    private double salidas;
    private double saldoActual;
    private String unidad;
    private EstadoLote estado;
    private Double pesoInicialKg;
    private Double pesoEntradasKg;
    private Double pesoSalidasKg;
    private Double pesoSaldoActualKg;

    public enum EstadoLote {
        ACTIVO, SALDO_BAJO, AGOTADO
    }
}