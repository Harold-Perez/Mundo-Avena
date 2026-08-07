package com.mundoavena.sistema.dto.inventario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BodegaResumenDTO {
    private String slug;
    private String nombre;
    private String icono;
    private int lotesActivos;
    private String saldoTotalFmt;
    private boolean manejaLotes;
    private boolean alertaBajo;
    private String tipoAgrupacion;
    private String pesoTotalFmt;
}