package com.mundoavena.sistema.controller;

import com.mundoavena.sistema.model.Rol;
import com.mundoavena.sistema.model.Usuario;
import com.mundoavena.sistema.repository.*;
import com.mundoavena.sistema.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UsuarioService usuarioService;
    @Autowired private TiemposProduccionService tiemposService;
    @Autowired private ProduccionGranelService granelService;
    @Autowired private ControlDiarioPTService controlPTService;
    @Autowired private ConciliacionMaterialesService conciliacionService;
    @Autowired private MezclaPremixService mezclaPremixService;
    @Autowired private ControlPesosGranelService controlPesosService;
    @Autowired private MonitoreoPCC2Service monitoreoPCC2Service;
    @Autowired private CierreMensualService cierreService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        LocalDate hoy = LocalDate.now();

        List<Usuario> usuarios = usuarioService.listarTodos();
        long totalUsuarios = usuarios.size();
        long usuariosActivos = usuarios.stream().filter(u -> u.isActivo()).count();
        long totalEmpleados = usuarios.stream().filter(u -> u.getRol() == Rol.EMPLEADO).count();
        long totalGerencia = usuarios.stream().filter(u -> u.getRol() == Rol.GERENCIA).count();

        long tiemposHoy = tiemposService.listarPorFecha(hoy).size();
        long granelHoy = granelService.listarPorFecha(hoy).size();
        long ptHoy = controlPTService.listarPorFecha(hoy).size();
        long conciliacionHoy = conciliacionService.listarPorFecha(hoy).size();

        long totalRegistros = tiemposService.listarTodos().size()
                + granelService.listarTodos().size()
                + controlPTService.listarTodos().size()
                + conciliacionService.listarTodos().size()
                + mezclaPremixService.listarTodos().size()
                + controlPesosService.listarTodos().size()
                + monitoreoPCC2Service.listarTodos().size();

        long totalCierres = cierreService.listarTodos().size();

        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("usuariosActivos", usuariosActivos);
        model.addAttribute("totalEmpleados", totalEmpleados);
        model.addAttribute("totalGerencia", totalGerencia);
        model.addAttribute("tiemposHoy", tiemposHoy);
        model.addAttribute("granelHoy", granelHoy);
        model.addAttribute("ptHoy", ptHoy);
        model.addAttribute("conciliacionHoy", conciliacionHoy);
        model.addAttribute("totalRegistros", totalRegistros);
        model.addAttribute("totalCierres", totalCierres);
        model.addAttribute("hoy", hoy);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("cierres", cierreService.listarTodos());
        return "admin/dashboard";
    }
}