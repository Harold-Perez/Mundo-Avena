package com.mundoavena.sistema.repository;

import com.mundoavena.sistema.model.MezclaPremix;
import com.mundoavena.sistema.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MezclaPremixRepository extends JpaRepository<MezclaPremix, Long> {
    List<MezclaPremix> findByUsuario(Usuario usuario);
    List<MezclaPremix> findByFechaInicioBetween(LocalDate inicio, LocalDate fin);
    List<MezclaPremix> findByFechaInicioOrderByFechaInicioDesc(LocalDate fecha);
}