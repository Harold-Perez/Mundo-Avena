package com.mundoavena.sistema.repository;

import com.mundoavena.sistema.model.ControlDiarioPT;
import com.mundoavena.sistema.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ControlDiarioPTRepository extends JpaRepository<ControlDiarioPT, Long> {
    List<ControlDiarioPT> findByUsuario(Usuario usuario);
    List<ControlDiarioPT> findByFecha(LocalDate fecha);
    List<ControlDiarioPT> findByFechaBetween(LocalDate inicio, LocalDate fin);
}