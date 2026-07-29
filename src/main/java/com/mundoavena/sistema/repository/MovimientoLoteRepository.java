package com.mundoavena.sistema.repository;

import com.mundoavena.sistema.model.Bodega;
import com.mundoavena.sistema.model.LoteSemana;
import com.mundoavena.sistema.model.MovimientoLote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MovimientoLoteRepository extends JpaRepository<MovimientoLote, Long> {
    List<MovimientoLote> findByLoteSemanaOrderByFechaDesc(LoteSemana loteSemana);
    List<MovimientoLote> findTop10ByLoteSemana_Producto_BodegaOrderByFechaDescFechaCreacionDesc(Bodega bodega);
}