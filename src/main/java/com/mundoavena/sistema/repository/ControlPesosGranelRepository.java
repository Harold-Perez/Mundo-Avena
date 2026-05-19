package com.mundoavena.sistema.repository;

import com.mundoavena.sistema.model.ControlPesosGranel;
import com.mundoavena.sistema.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ControlPesosGranelRepository extends JpaRepository<ControlPesosGranel, Long> {
    List<ControlPesosGranel> findByUsuario(Usuario usuario);
    List<ControlPesosGranel> findByFecha(LocalDate fecha);
    List<ControlPesosGranel> findByFechaBetween(LocalDate inicio, LocalDate fin);
}