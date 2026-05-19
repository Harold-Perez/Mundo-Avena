package com.mundoavena.sistema.repository;

import com.mundoavena.sistema.model.TiemposProduccion;
import com.mundoavena.sistema.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TiemposProduccionRepository extends JpaRepository<TiemposProduccion, Long> {
    List<TiemposProduccion> findByUsuario(Usuario usuario);
    List<TiemposProduccion> findByFecha(LocalDate fecha);
    List<TiemposProduccion> findByFechaBetween(LocalDate inicio, LocalDate fin);
}