package com.mundoavena.sistema.service;

import com.mundoavena.sistema.model.MonitoreoPCC2;
import com.mundoavena.sistema.model.Usuario;
import com.mundoavena.sistema.repository.MonitoreoPCC2Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class MonitoreoPCC2Service {

    @Autowired
    private MonitoreoPCC2Repository repository;

    public MonitoreoPCC2 guardar(MonitoreoPCC2 registro) {
        return repository.save(registro);
    }

    public List<MonitoreoPCC2> listarPorUsuario(Usuario usuario) {
        return repository.findByUsuario(usuario);
    }

    public List<MonitoreoPCC2> listarPorFecha(LocalDate fecha) {
        return repository.findByFechaProduccion(fecha);
    }

    public List<MonitoreoPCC2> listarTodos() {
        return repository.findAll();
    }

    public List<MonitoreoPCC2> listarPorRango(LocalDate inicio, LocalDate fin) {
        return repository.findByFechaProduccionBetween(inicio, fin);
    }
}