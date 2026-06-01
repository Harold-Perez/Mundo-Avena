package com.mundoavena.sistema.repository;

import com.mundoavena.sistema.model.MonitoreoPCC2;
import com.mundoavena.sistema.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MonitoreoPCC2Repository extends JpaRepository<MonitoreoPCC2, Long> {
    List<MonitoreoPCC2> findByUsuario(Usuario usuario);
    List<MonitoreoPCC2> findByFechaProduccion(LocalDate fecha);
    List<MonitoreoPCC2> findByFechaProduccionBetween(LocalDate inicio, LocalDate fin);
}