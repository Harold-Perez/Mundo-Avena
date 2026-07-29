package com.mundoavena.sistema.repository;

import com.mundoavena.sistema.model.Bodega;
import com.mundoavena.sistema.model.LoteSemana;
import com.mundoavena.sistema.model.Producto;
import com.mundoavena.sistema.model.Semana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoteSemanaRepository extends JpaRepository<LoteSemana, Long> {
    List<LoteSemana> findBySemanaAndProducto_Bodega(Semana semana, Bodega bodega);
    Optional<LoteSemana> findBySemanaAndProductoAndNumeroLote(Semana semana, Producto producto, String numeroLote);
}