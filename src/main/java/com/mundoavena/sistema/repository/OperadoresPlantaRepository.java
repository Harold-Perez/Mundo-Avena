package com.mundoavena.sistema.repository;

import com.mundoavena.sistema.model.OperadoresPlanta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OperadoresPlantaRepository extends JpaRepository<OperadoresPlanta, Long> {
    List<OperadoresPlanta> findByActivoTrue();
}