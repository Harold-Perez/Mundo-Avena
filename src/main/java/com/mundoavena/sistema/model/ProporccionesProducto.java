package com.mundoavena.sistema.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "proporciones_producto")
@Data
public class ProporccionesProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombreProducto;

    @Column(nullable = false)
    private Double propHarina = 0.014218;

    @Column(nullable = false)
    private Double propCarbonato = 0.036440;

    @Column(nullable = false)
    private Double propVitaminas = 0.001380;

    @Column(nullable = false)
    private Double costoEmpaqueUnitario = 0.0;
}