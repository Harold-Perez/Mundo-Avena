package com.mundoavena.sistema.repository;

import com.mundoavena.sistema.model.Semana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SemanaRepository extends JpaRepository<Semana, Long> {
    Optional<Semana> findFirstByCerradaFalseOrderByFechaInicioDesc();
}