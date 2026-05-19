package com.mundoavena.sistema.service;

import com.mundoavena.sistema.model.ControlDiarioPT;
import com.mundoavena.sistema.model.Usuario;
import com.mundoavena.sistema.repository.ControlDiarioPTRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class ControlDiarioPTService {

    @Autowired
    private ControlDiarioPTRepository repository;

    public ControlDiarioPT guardar(ControlDiarioPT registro) {
        return repository.save(registro);
    }

    public List<ControlDiarioPT> listarPorUsuario(Usuario usuario) {
        return repository.findByUsuario(usuario);
    }

    public List<ControlDiarioPT> listarPorFecha(LocalDate fecha) {
        return repository.findByFecha(fecha);
    }

    public List<ControlDiarioPT> listarTodos() {
        return repository.findAll();
    }

    public List<ControlDiarioPT> listarPorRango(LocalDate inicio, LocalDate fin) {
        return repository.findByFechaBetween(inicio, fin);
    }
}