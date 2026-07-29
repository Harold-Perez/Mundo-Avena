package com.mundoavena.sistema.repository;

import com.mundoavena.sistema.model.Bodega;
import com.mundoavena.sistema.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByBodegaAndActivoTrue(Bodega bodega);
}