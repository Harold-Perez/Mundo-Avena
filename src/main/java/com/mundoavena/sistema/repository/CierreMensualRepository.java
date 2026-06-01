package com.mundoavena.sistema.repository;

import com.mundoavena.sistema.model.CierreMensual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CierreMensualRepository extends JpaRepository<CierreMensual, Long> {
    Optional<CierreMensual> findByMesAndAnio(Integer mes, Integer anio);
    List<CierreMensual> findAllByOrderByAnioDescMesDesc();
}