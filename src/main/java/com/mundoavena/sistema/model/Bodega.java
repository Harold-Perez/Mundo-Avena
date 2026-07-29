package com.mundoavena.sistema.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "bodegas")
@Data
public class Bodega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String slug;          // "grano-groat", "grano-avena", "descascarado", "cascara", "bodega-b", "harina-otw", "producto-terminado"

    @Column(nullable = false)
    private String nombre;        // "Silo Groat", "Descascarado", etc.

    @Column(nullable = false)
    private String icono;

    @Column(nullable = false)
    private boolean manejaLotes;  // false solo para Cáscara

    @Column(nullable = false)
    private String tipoAgrupacion; // "plana", "ubicacion", "categoria", "sin-lotes" — igual al DTO que ya usamos en la UI

    @Column
    private Long capacidadKg;     // solo aplica a los 2 silos de Grano; null para las demás
}