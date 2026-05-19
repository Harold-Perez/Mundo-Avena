package com.mundoavena.sistema.service;

import com.mundoavena.sistema.model.ProduccionGranel;
import com.mundoavena.sistema.model.Usuario;
import com.mundoavena.sistema.repository.ProduccionGranelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class ProduccionGranelService {

    @Autowired
    private ProduccionGranelRepository repository;

    public ProduccionGranel guardar(ProduccionGranel registro) {
        return repository.save(registro);
    }

    public List<ProduccionGranel> listarPorUsuario(Usuario usuario) {
        return repository.findByUsuario(usuario);
    }

    public List<ProduccionGranel> listarPorFecha(LocalDate fecha) {
        return repository.findByFecha(fecha);
    }

    public List<ProduccionGranel> listarTodos() {
        return repository.findAll();
    }

    public List<ProduccionGranel> listarPorRango(LocalDate inicio, LocalDate fin) {
        return repository.findByFechaBetween(inicio, fin);
    }
}