package com.mundoavena.sistema.controller;

import com.mundoavena.sistema.model.Rol;
import com.mundoavena.sistema.model.Usuario;
import com.mundoavena.sistema.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("titulo", "Gestión de Usuarios");
        return "admin/usuarios/listar";
    }

    @GetMapping("/nuevo")
    public String formNuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", Rol.values());
        model.addAttribute("titulo", "Nuevo Usuario");
        return "admin/usuarios/form";
    }

    @PostMapping("/nuevo")
    public String crear(@ModelAttribute Usuario usuario,
                        RedirectAttributes redirectAttributes) {
        if (usuarioService.existeUsername(usuario.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "El usuario ya existe");
            return "redirect:/admin/usuarios/nuevo";
        }
        usuarioService.crear(usuario);
        redirectAttributes.addFlashAttribute("exito", "Usuario creado correctamente");
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/editar/{id}")
    public String formEditar(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", Rol.values());
        model.addAttribute("titulo", "Editar Usuario");
        return "admin/usuarios/form";
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id,
                             @ModelAttribute Usuario usuario,
                             @RequestParam(defaultValue = "false") boolean cambiarPassword,
                             RedirectAttributes redirectAttributes) {
        usuarioService.actualizar(id, usuario, cambiarPassword);
        redirectAttributes.addFlashAttribute("exito", "Usuario actualizado correctamente");
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/desactivar/{id}")
    public String desactivar(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        usuarioService.desactivar(id);
        redirectAttributes.addFlashAttribute("exito", "Usuario desactivado");
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/activar/{id}")
    public String activar(@PathVariable Long id,
                          RedirectAttributes redirectAttributes) {
        usuarioService.activar(id);
        redirectAttributes.addFlashAttribute("exito", "Usuario activado");
        return "redirect:/admin/usuarios";
    }
}