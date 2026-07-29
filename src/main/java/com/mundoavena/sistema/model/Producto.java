package com.mundoavena.sistema.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "productos")
@Data
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bodega_id", nullable = false)
    private Bodega bodega;

    @Column(nullable = false)
    private String nombre;        // "Avenilla", "Harina OTW", "Avena Mosh 600g"

    @Column
    private String categoria;     // "Hojuela Campo Rico", "Avena Mosh" — null si la bodega no agrupa por categoría

    @Column(nullable = false)
    private String unidadMedida;  // "sacos", "kg", "unidades"

    @Column(nullable = false)
    private boolean activo = true; // para "eliminar" sin borrar historial — se desactiva, no se borra
}