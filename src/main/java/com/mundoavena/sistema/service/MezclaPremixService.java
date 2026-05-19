package com.mundoavena.sistema.service;

import com.mundoavena.sistema.model.MezclaPremix;
import com.mundoavena.sistema.model.Usuario;
import com.mundoavena.sistema.repository.MezclaPremixRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class MezclaPremixService {

    @Autowired
    private MezclaPremixRepository repository;

    public MezclaPremix guardar(MezclaPremix registro) {
        return repository.save(registro);
    }

    public List<MezclaPremix> listarPorUsuario(Usuario usuario) {
        return repository.findByUsuario(usuario);
    }

    public List<MezclaPremix> listarPorFecha(LocalDate fecha) {
        return repository.findByFecha(fecha);
    }

    public List<MezclaPremix> listarTodos() {
        return repository.findAll();
    }

    public List<MezclaPremix> listarPorRango(LocalDate inicio, LocalDate fin) {
        return repository.findByFechaBetween(inicio, fin);
    }
}