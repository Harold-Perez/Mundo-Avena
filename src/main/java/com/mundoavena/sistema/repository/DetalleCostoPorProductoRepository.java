package com.mundoavena.sistema.repository;

import com.mundoavena.sistema.model.CierreMensual;
import com.mundoavena.sistema.model.DetalleCostoPorProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DetalleCostoPorProductoRepository extends JpaRepository<DetalleCostoPorProducto, Long> {
    List<DetalleCostoPorProducto> findByCierre(CierreMensual cierre);
}