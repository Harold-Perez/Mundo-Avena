package com.mundoavena.sistema.repository;

import com.mundoavena.sistema.model.ProduccionGranel;
import com.mundoavena.sistema.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProduccionGranelRepository extends JpaRepository<ProduccionGranel, Long> {
    List<ProduccionGranel> findByUsuario(Usuario usuario);
    List<ProduccionGranel> findByFecha(LocalDate fecha);
    List<ProduccionGranel> findByFechaBetween(LocalDate inicio, LocalDate fin);
}