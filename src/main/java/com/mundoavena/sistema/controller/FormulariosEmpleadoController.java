package com.mundoavena.sistema.controller;

import com.mundoavena.sistema.model.*;
import com.mundoavena.sistema.repository.UsuarioRepository;
import com.mundoavena.sistema.service.*;
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
        return "empleado/dashboard";
    }

    // ── TIEMPOS DE PRODUCCION ──
    @GetMapping("/tiempos")
    public String formTiempos(Model model) {
        model.addAttribute("tiempos", new TiemposProduccion());
        return "empleado/tiempos";
    }

    @PostMapping("/tiempos")
    public String guardarTiempos(@ModelAttribute TiemposProduccion tiempos,
                                 Authentication auth, RedirectAttributes ra) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        tiempos.setUsuario(usuario);
        tiemposService.guardar(tiempos);
        ra.addFlashAttribute("exito", "Registro de tiempos guardado correctamente");
        return "redirect:/empleado/dashboard";
    }

    // ── PRODUCCION GRANEL ──
    @GetMapping("/granel")
    public String formGranel(Model model) {
        model.addAttribute("granel", new ProduccionGranel());
        return "empleado/granel";
    }

    @PostMapping("/granel")
    public String guardarGranel(@ModelAttribute ProduccionGranel granel,
                                Authentication auth, RedirectAttributes ra) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        granel.setUsuario(usuario);
        granelService.guardar(granel);
        ra.addFlashAttribute("exito", "Producción granel guardada correctamente");
        return "redirect:/empleado/dashboard";
    }

    // ── CONCILIACION DE MATERIALES ──
    @GetMapping("/conciliacion")
    public String formConciliacion(Model model) {
        model.addAttribute("conciliacion", new ConciliacionMateriales());
        return "empleado/conciliacion";
    }

    @PostMapping("/conciliacion")
    public String guardarConciliacion(@ModelAttribute ConciliacionMateriales conciliacion,
                                      Authentication auth, RedirectAttributes ra) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        conciliacion.setUsuario(usuario);
        conciliacionService.guardar(conciliacion);
        ra.addFlashAttribute("exito", "Conciliación de materiales guardada correctamente");
        return "redirect:/empleado/dashboard";
    }

    // ── MEZCLA PREMIX ──
    @GetMapping("/premix")
    public String formPremix(Model model) {
        model.addAttribute("premix", new MezclaPremix());
        return "empleado/premix";
    }

    @PostMapping("/premix")
    public String guardarPremix(@ModelAttribute MezclaPremix premix,
                                Authentication auth, RedirectAttributes ra) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        premix.setUsuario(usuario);
        mezclaPremixService.guardar(premix);
        ra.addFlashAttribute("exito", "Mezcla Premix guardada correctamente");
        return "redirect:/empleado/dashboard";
    }

    // ── CONTROL DIARIO PT ──
    @GetMapping("/control-pt")
    public String formControlPT(Model model) {
        model.addAttribute("controlPT", new ControlDiarioPT());
        return "empleado/control-pt";
    }

    @PostMapping("/control-pt")
    public String guardarControlPT(@ModelAttribute ControlDiarioPT controlPT,
                                   Authentication auth, RedirectAttributes ra) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        controlPT.setUsuario(usuario);
        controlDiarioPTService.guardar(controlPT);
        ra.addFlashAttribute("exito", "Control Diario PT guardado correctamente");
        return "redirect:/empleado/dashboard";
    }

    // ── CONTROL DE PESOS GRANEL ──
    @GetMapping("/pesos")
    public String formPesos(Model model) {
        model.addAttribute("pesos", new ControlPesosGranel());
        return "empleado/pesos";
    }

    @PostMapping("/pesos")
    public String guardarPesos(@ModelAttribute ControlPesosGranel pesos,
                               Authentication auth, RedirectAttributes ra) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        pesos.setUsuario(usuario);
        // Calcular total kg y total sacos
        double total = 0;
        int sacos = 0;
        // Sumar todos los sacos que tengan valor
        java.lang.reflect.Field[] fields = pesos.getClass().getDeclaredFields();
        for (java.lang.reflect.Field field : fields) {
            if (field.getName().startsWith("saco")) {
                field.setAccessible(true);
                try {
                    Double val = (Double) field.get(pesos);
                    if (val != null && val > 0) {
                        total += val;
                        sacos++;
                    }
                } catch (Exception e) { }
            }
        }
        pesos.setTotalKg(total);
        pesos.setTotalSacos(sacos);
        controlPesosService.guardar(pesos);
        ra.addFlashAttribute("exito", "Control de pesos guardado correctamente");
        return "redirect:/empleado/dashboard";
    }
}