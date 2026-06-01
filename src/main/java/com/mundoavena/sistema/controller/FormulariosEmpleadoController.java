package com.mundoavena.sistema.controller;

import com.mundoavena.sistema.model.*;
import com.mundoavena.sistema.repository.UsuarioRepository;
import com.mundoavena.sistema.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/empleado")
public class FormulariosEmpleadoController {

    @Autowired private TiemposProduccionService tiemposService;
    @Autowired private ProduccionGranelService granelService;
    @Autowired private ConciliacionMaterialesService conciliacionService;
    @Autowired private MezclaPremixService mezclaPremixService;
    @Autowired private ControlDiarioPTService controlDiarioPTService;
    @Autowired private ControlPesosGranelService controlPesosService;
    @Autowired private MonitoreoPCC2Service monitoreoPCC2Service;
    @Autowired private AuditoriaService auditoriaService;
    @Autowired private UsuarioRepository usuarioRepository;

    // ── DASHBOARD ──
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("usuario", usuario);
        model.addAttribute("registrosTiempos", tiemposService.listarPorUsuario(usuario));
        model.addAttribute("registrosGranel", granelService.listarPorUsuario(usuario));
        model.addAttribute("registrosConciliacion", conciliacionService.listarPorUsuario(usuario));
        model.addAttribute("registrosPremix", mezclaPremixService.listarPorUsuario(usuario));
        model.addAttribute("registrosPT", controlDiarioPTService.listarPorUsuario(usuario));
        model.addAttribute("registrosPesos", controlPesosService.listarPorUsuario(usuario));
        model.addAttribute("registrosMonitoreo", monitoreoPCC2Service.listarPorUsuario(usuario));
        return "empleado/dashboard";
    }

    // ── TIEMPOS ──
    @GetMapping("/tiempos")
    public String formTiempos(Model model, Authentication auth) {
        model.addAttribute("tiempos", new TiemposProduccion());
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "empleado/tiempos";
    }

    @PostMapping("/tiempos")
    public String guardarTiempos(@ModelAttribute TiemposProduccion tiempos,
                                 Authentication auth, HttpServletRequest request,
                                 RedirectAttributes ra) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        tiempos.setUsuario(usuario);
        TiemposProduccion guardado = tiemposService.guardar(tiempos);
        auditoriaService.registrar(usuario, "CREAR", "TIEMPOS_PRODUCCION",
                guardado.getId(), "Registro de tiempos: " + tiempos.getFecha(),
                request.getRemoteAddr());
        ra.addFlashAttribute("exito", "Registro de tiempos guardado correctamente");
        return "redirect:/empleado/dashboard";
    }

    // ── GRANEL ──
    @GetMapping("/granel")
    public String formGranel(Model model, Authentication auth) {
        model.addAttribute("granel", new ProduccionGranel());
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "empleado/granel";
    }

    @PostMapping("/granel")
    public String guardarGranel(@ModelAttribute ProduccionGranel granel,
                                Authentication auth, HttpServletRequest request,
                                RedirectAttributes ra) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        granel.setUsuario(usuario);
        ProduccionGranel guardado = granelService.guardar(granel);
        auditoriaService.registrar(usuario, "CREAR", "PRODUCCION_GRANEL",
                guardado.getId(), "Producción granel: " + granel.getFecha() + " turno " + granel.getTurno(),
                request.getRemoteAddr());
        ra.addFlashAttribute("exito", "Producción granel guardada correctamente");
        return "redirect:/empleado/dashboard";
    }

    // ── CONCILIACION ──
    @GetMapping("/conciliacion")
    public String formConciliacion(Model model, Authentication auth) {
        model.addAttribute("conciliacion", new ConciliacionMateriales());
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "empleado/conciliacion";
    }

    @PostMapping("/conciliacion")
    public String guardarConciliacion(@ModelAttribute ConciliacionMateriales conciliacion,
                                      Authentication auth, HttpServletRequest request,
                                      RedirectAttributes ra) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        conciliacion.setUsuario(usuario);
        ConciliacionMateriales guardado = conciliacionService.guardar(conciliacion);
        auditoriaService.registrar(usuario, "CREAR", "CONCILIACION_MATERIALES",
                guardado.getId(), "Conciliación: " + conciliacion.getFecha() + " turno " + conciliacion.getTurno(),
                request.getRemoteAddr());
        ra.addFlashAttribute("exito", "Conciliación guardada correctamente");
        return "redirect:/empleado/dashboard";
    }

    // ── PREMIX ──
    @GetMapping("/premix")
    public String formPremix(Model model, Authentication auth) {
        model.addAttribute("premix", new MezclaPremix());
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "empleado/premix";
    }

    @PostMapping("/premix")
    public String guardarPremix(@ModelAttribute MezclaPremix premix,
                                Authentication auth, HttpServletRequest request,
                                RedirectAttributes ra) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        premix.setUsuario(usuario);
        MezclaPremix guardado = mezclaPremixService.guardar(premix);
        auditoriaService.registrar(usuario, "CREAR", "MEZCLA_PREMIX",
                guardado.getId(), "Mezcla premix: " + premix.getFecha(),
                request.getRemoteAddr());
        ra.addFlashAttribute("exito", "Mezcla Premix guardada correctamente");
        return "redirect:/empleado/dashboard";
    }

    // ── CONTROL PT ──
    @GetMapping("/control-pt")
    public String formControlPT(Model model, Authentication auth) {
        model.addAttribute("controlPT", new ControlDiarioPT());
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "empleado/control-pt";
    }

    @PostMapping("/control-pt")
    public String guardarControlPT(@ModelAttribute ControlDiarioPT controlPT,
                                   Authentication auth, HttpServletRequest request,
                                   RedirectAttributes ra) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        controlPT.setUsuario(usuario);
        ControlDiarioPT guardado = controlDiarioPTService.guardar(controlPT);
        auditoriaService.registrar(usuario, "CREAR", "CONTROL_DIARIO_PT",
                guardado.getId(), "Control PT: " + controlPT.getProducto() + " " + controlPT.getFecha(),
                request.getRemoteAddr());
        ra.addFlashAttribute("exito", "Control Diario PT guardado correctamente");
        return "redirect:/empleado/dashboard";
    }

    // ── PESOS ──
    @GetMapping("/pesos")
    public String formPesos(Model model, Authentication auth) {
        model.addAttribute("pesos", new ControlPesosGranel());
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "empleado/pesos";
    }

    @PostMapping("/pesos")
    public String guardarPesos(@ModelAttribute ControlPesosGranel pesos,
                               Authentication auth, HttpServletRequest request,
                               RedirectAttributes ra) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        pesos.setUsuario(usuario);
        double total = 0; int sacos = 0;
        java.lang.reflect.Field[] fields = pesos.getClass().getDeclaredFields();
        for (java.lang.reflect.Field field : fields) {
            if (field.getName().startsWith("saco")) {
                field.setAccessible(true);
                try {
                    Double val = (Double) field.get(pesos);
                    if (val != null && val > 0) { total += val; sacos++; }
                } catch (Exception e) { }
            }
        }
        pesos.setTotalKg(total);
        pesos.setTotalSacos(sacos);
        ControlPesosGranel guardado = controlPesosService.guardar(pesos);
        auditoriaService.registrar(usuario, "CREAR", "CONTROL_PESOS_GRANEL",
                guardado.getId(), "Control pesos: " + pesos.getProducto() + " " + sacos + " sacos",
                request.getRemoteAddr());
        ra.addFlashAttribute("exito", "Control de pesos guardado correctamente");
        return "redirect:/empleado/dashboard";
    }

    // ── MONITOREO ──
    @GetMapping("/monitoreo")
    public String formMonitoreo(Model model, Authentication auth) {
        model.addAttribute("monitoreo", new MonitoreoPCC2());
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "empleado/monitoreo";
    }

    @PostMapping("/monitoreo")
    public String guardarMonitoreo(@ModelAttribute MonitoreoPCC2 monitoreo,
                                   Authentication auth, HttpServletRequest request,
                                   RedirectAttributes ra) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        monitoreo.setUsuario(usuario);
        if (monitoreo.getMpCantidadKg() != null && monitoreo.getMpCantidadKg() > 0) {
            double rendMP = (monitoreo.getMpProductoTerminadoKg() / monitoreo.getMpCantidadKg()) * 100;
            monitoreo.setMpRendimientoPct(Math.round(rendMP * 100.0) / 100.0);
            monitoreo.setMpTotalKg(monitoreo.getMpCantidadKg());
        }
        if (monitoreo.getEmpCantidadKg() != null && monitoreo.getEmpCantidadKg() > 0) {
            double rendEmp = (monitoreo.getEmpProductoTerminadoKg() / monitoreo.getEmpCantidadKg()) * 100;
            monitoreo.setEmpRendimientoPct(Math.round(rendEmp * 100.0) / 100.0);
            monitoreo.setEmpTotalKg(monitoreo.getEmpCantidadKg());
        }
        MonitoreoPCC2 guardado = monitoreoPCC2Service.guardar(monitoreo);
        auditoriaService.registrar(usuario, "CREAR", "MONITOREO_PCC2",
                guardado.getId(), "Monitoreo PCC2: " + monitoreo.getProducto(),
                request.getRemoteAddr());
        ra.addFlashAttribute("exito", "Monitoreo PCC2 guardado correctamente");
        return "redirect:/empleado/dashboard";
    }
}