package com.mundoavena.sistema.dto.inventario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de vista para cada fila de la tabla "Movimientos recientes" en el detalle de Grano.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovimientoRecienteDTO {
    private String fecha;       // ya formateado, ej. "22 jul, 10:32"
    private String silo;        // "Groat" o "Avena c/c"
    private String tipo;        // "ENTRADA" o "SALIDA"
    private String cantidadFmt; // ej. "+18.2 t" o "-6.5 t"
    private String ticket;      // número de ticket báscula
    private String saldoFmt;    // saldo del silo después del movimiento
}