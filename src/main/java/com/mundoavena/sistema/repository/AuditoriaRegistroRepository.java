package com.mundoavena.sistema.repository;

import com.mundoavena.sistema.model.AuditoriaRegistro;
import com.mundoavena.sistema.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditoriaRegistroRepository extends JpaRepository<AuditoriaRegistro, Long> {
    List<AuditoriaRegistro> findByUsuario(Usuario usuario);
    List<AuditoriaRegistro> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);
    List<AuditoriaRegistro> findAllByOrderByFechaHoraDesc();
    List<AuditoriaRegistro> findByTipoFormulario(String tipoFormulario);
}