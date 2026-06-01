package com.mundoavena.sistema.repository;

import com.mundoavena.sistema.model.ProporccionesProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProporccionesProductoRepository extends JpaRepository<ProporccionesProducto, Long> {
    Optional<ProporccionesProducto> findByNombreProducto(String nombreProducto);
}