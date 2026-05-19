package com.mundoavena.sistema.repository;

import com.mundoavena.sistema.model.RegistroProduccion;
import com.mundoavena.sistema.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface RegistroProduccionRepository extends JpaRepository<RegistroProduccion, Long> {
    List<RegistroProduccion> findByUsuario(Usuario usuario);
    List<RegistroProduccion> findByFecha(LocalDate fecha);
    List<RegistroProduccion> findByUsuarioAndFecha(Usuario usuario, LocalDate fecha);
    List<RegistroProduccion> findByFechaBetween(LocalDate inicio, LocalDate fin);
}