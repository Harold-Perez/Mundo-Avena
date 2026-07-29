package com.mundoavena.sistema.repository;

import com.mundoavena.sistema.model.Bodega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BodegaRepository extends JpaRepository<Bodega, Long> {
    Optional<Bodega> findBySlug(String slug);
}