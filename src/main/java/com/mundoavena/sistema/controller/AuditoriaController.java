package com.mundoavena.sistema.controller;

import com.mundoavena.sistema.service.AuditoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/auditoria")
public class AuditoriaController {

    @Autowired
    private AuditoriaService auditoriaService;

    @GetMapping
    public String listar(Model model,
                         @RequestParam(required = false) String tipo) {
        if (tipo != null && !tipo.isEmpty()) {
            model.addAttribute("registros", auditoriaService.listarPorTipo(tipo));
            model.addAttribute("tipoFiltro", tipo);
        } else {
            model.addAttribute("registros", auditoriaService.listarTodos());
            model.addAttribute("tipoFiltro", "");
        }
        return "admin/auditoria";
    }
}