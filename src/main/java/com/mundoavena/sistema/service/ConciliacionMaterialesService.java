package com.mundoavena.sistema.service;

import com.mundoavena.sistema.model.ConciliacionMateriales;
import com.mundoavena.sistema.model.Usuario;
import com.mundoavena.sistema.repository.ConciliacionMaterialesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class ConciliacionMaterialesService {

    @Autowired
    private ConciliacionMaterialesRepository repository;

    public ConciliacionMateriales guardar(ConciliacionMateriales registro) {
        return repository.save(registro);
    }

    public List<ConciliacionMateriales> listarPorUsuario(Usuario usuario) {
        return repository.findByUsuario(usuario);
    }

    public List<ConciliacionMateriales> listarPorFecha(LocalDate fecha) {
        return repository.findByFecha(fecha);
    }

    public List<ConciliacionMateriales> listarTodos() {
        return repository.findAll();
    }

    public List<ConciliacionMateriales> listarPorRango(LocalDate inicio, LocalDate fin) {
        return repository.findByFechaBetween(inicio, fin);
    }
}