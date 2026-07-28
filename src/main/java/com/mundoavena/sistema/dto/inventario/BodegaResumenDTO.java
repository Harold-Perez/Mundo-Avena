package com.mundoavena.sistema.dto.inventario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de vista para las tarjetas de subinventario en el dashboard general.
 * Provisional: cuando generalicemos el modelo real (Bodega + Producto + LoteSemana),
 * esto se llena desde el servicio en vez de datos mock.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BodegaResumenDTO {
    private String slug;          // usado en la URL: /inventario/subinventario/{slug}
    private String nombre;        // "Descascarado", "Bodega Producto Terminado", etc.
    private String icono;         // emoji para la tarjeta
    private int lotesActivos;
    private String saldoTotalFmt; // ya formateado, ej. "12,450 kg" o "340 sacos"
    private boolean manejaLotes;  // false solo para Cáscara
    private boolean alertaBajo;   // true si algún lote está en saldo bajo
    private String tipoAgrupacion; // "plana", "ubicacion", "categoria" — según cómo se agrupa en el Excel real de esa bodega
}