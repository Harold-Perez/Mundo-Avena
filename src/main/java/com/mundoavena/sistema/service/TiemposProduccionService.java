package com.mundoavena.sistema.service;

import com.mundoavena.sistema.model.TiemposProduccion;
import com.mundoavena.sistema.model.Usuario;
import com.mundoavena.sistema.repository.TiemposProduccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class TiemposProduccionService {

    @Autowired
    private TiemposProduccionRepository repository;

    public TiemposProduccion guardar(TiemposProduccion registro) {
        return repository.save(registro);
    }

    public List<TiemposProduccion> listarPorUsuario(Usuario usuario) {
        return repository.findByUsuario(usuario);
    }

    public List<TiemposProduccion> listarPorFecha(LocalDate fecha) {
        return repository.findByFecha(fecha);
    }

    public List<TiemposProduccion> listarPorRango(LocalDate inicio, LocalDate fin) {
        return repository.findByFechaBetween(inicio, fin);
    }

    public List<TiemposProduccion> listarTodos() {
        return repository.findAll();
    }
}