package com.mundoavena.sistema.service;

import com.mundoavena.sistema.model.ControlPesosGranel;
import com.mundoavena.sistema.model.Usuario;
import com.mundoavena.sistema.repository.ControlPesosGranelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class ControlPesosGranelService {

    @Autowired
    private ControlPesosGranelRepository repository;

    public ControlPesosGranel guardar(ControlPesosGranel registro) {
        return repository.save(registro);
    }

    public List<ControlPesosGranel> listarPorUsuario(Usuario usuario) {
        return repository.findByUsuario(usuario);
    }

    public List<ControlPesosGranel> listarPorFecha(LocalDate fecha) {
        return repository.findByFecha(fecha);
    }

    public List<ControlPesosGranel> listarTodos() {
        return repository.findAll();
    }

    public List<ControlPesosGranel> listarPorRango(LocalDate inicio, LocalDate fin) {
        return repository.findByFechaBetween(inicio, fin);
    }
}