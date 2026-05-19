package com.mundoavena.sistema.repository;

import com.mundoavena.sistema.model.ConciliacionMateriales;
import com.mundoavena.sistema.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ConciliacionMaterialesRepository extends JpaRepository<ConciliacionMateriales, Long> {
    List<ConciliacionMateriales> findByUsuario(Usuario usuario);
    List<ConciliacionMateriales> findByFecha(LocalDate fecha);
    List<ConciliacionMateriales> findByFechaBetween(LocalDate inicio, LocalDate fin);
}