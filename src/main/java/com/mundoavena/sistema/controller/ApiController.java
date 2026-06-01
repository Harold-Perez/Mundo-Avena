package com.mundoavena.sistema.controller;

import com.mundoavena.sistema.repository.OperadoresPlantaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private OperadoresPlantaRepository operadoresRepository;

    @GetMapping("/usuarios/buscar")
    public List<String> buscarOperadores(@RequestParam String q) {
        return operadoresRepository.findByActivoTrue().stream()
                .filter(o -> o.getNombreCompleto().toLowerCase().contains(q.toLowerCase()))
                .map(o -> o.getNombreCompleto())
                .collect(Collectors.toList());
    }
}