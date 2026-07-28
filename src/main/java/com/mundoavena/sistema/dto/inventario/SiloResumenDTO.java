package com.mundoavena.sistema.dto.inventario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de vista para cada silo en la pantalla de detalle de Grano.
 * Capacidad fija: 600,000 kg por silo (600 t), según los silos reales de la planta.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SiloResumenDTO {
    private String nombre;          // "Silo Groat", "Silo Avena con Cáscara"
    private long saldoActualKg;     // suma de saldos de todos los lotes/contenedores del silo
    private long capacidadKg;       // 600000 fijo, pero lo dejamos como campo por si cambia
    private int porcentajeUso;      // (saldoActualKg / capacidadKg) * 100, ya calculado
}