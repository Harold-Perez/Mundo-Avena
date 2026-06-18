package com.mundoavena.sistema.controller;

import com.mundoavena.sistema.model.*;
import com.mundoavena.sistema.repository.UsuarioRepository;
import com.mundoavena.sistema.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/gerencia")
public class GerenciaController {

    @Autowired private CierreMensualService cierreService;
    @Autowired private TiemposProduccionService tiemposService;
    @Autowired private ProduccionGranelService granelService;
    @Autowired private ConciliacionMaterialesService conciliacionService;
    @Autowired private ControlDiarioPTService controlPTService;
    @Autowired private ControlPesosGranelService controlPesosService;
    @Autowired private MezclaPremixService mezclaPremixService;
    @Autowired private UsuarioRepository usuarioRepository;

    // Formateador con locale US: coma para miles, punto para decimales
    private static final NumberFormat NF = NumberFormat.getNumberInstance(Locale.US);

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        LocalDate hoy = LocalDate.now();
        model.addAttribute("tiemposHoy", tiemposService.listarPorFecha(hoy));
        model.addAttribute("granelHoy", granelService.listarPorFecha(hoy));
        model.addAttribute("conciliacionHoy", conciliacionService.listarPorFecha(hoy));
        model.addAttribute("ptHoy", controlPTService.listarPorFecha(hoy));
        model.addAttribute("cierres", cierreService.listarTodos());
        model.addAttribute("hoy", hoy);
        return "gerencia/dashboard";
    }

    @GetMapping("/historial")
    public String historial(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio,
            Model model) {

        LocalDate hoy = LocalDate.now();
        if (fecha == null) fecha = hoy;
        if (mes == null) mes = hoy.getMonthValue();
        if (anio == null) anio = hoy.getYear();

        model.addAttribute("tiemposFecha", tiemposService.listarPorFecha(fecha));
        model.addAttribute("granelFecha", granelService.listarPorFecha(fecha));
        model.addAttribute("ptFecha", controlPTService.listarPorFecha(fecha));
        model.addAttribute("conciliacionFecha", conciliacionService.listarPorFecha(fecha));

        LocalDate inicioMes = LocalDate.of(anio, mes, 1);
        LocalDate finMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());
        model.addAttribute("tiemposMes", tiemposService.listarPorRango(inicioMes, finMes));
        model.addAttribute("granelMes", granelService.listarPorRango(inicioMes, finMes));
        model.addAttribute("ptMes", controlPTService.listarPorRango(inicioMes, finMes));
        model.addAttribute("conciliacionMes", conciliacionService.listarPorRango(inicioMes, finMes));

        model.addAttribute("fecha", fecha);
        model.addAttribute("mes", mes);
        model.addAttribute("anio", anio);
        return "gerencia/historial";
    }

    @GetMapping("/costos")
    public String formCostos(Model model) {
        LocalDate hoy = LocalDate.now();
        model.addAttribute("mesActual", hoy.getMonthValue());
        model.addAttribute("anioActual", hoy.getYear());
        model.addAttribute("cierres", cierreService.listarTodos());
        model.addAttribute("costoAvena", 3.1857);
        model.addAttribute("costoHarina", 5.19);
        model.addAttribute("costoCarbonato", 2.0089);
        model.addAttribute("costoVitaminas", 181.15);
        model.addAttribute("costoEE", 0.1640);
        model.addAttribute("costoVapor", 0.1324);
        model.addAttribute("costoMO", 0.5292);
        model.addAttribute("tipoCambio", 7.65);
        model.addAttribute("propHarina", 0.014218);
        model.addAttribute("propCarbonato", 0.036440);
        model.addAttribute("propVitaminas", 0.001380);
        model.addAttribute("empNutremas1200g", 0.60894);
        model.addAttribute("empNutremas900g", 0.41769);
        model.addAttribute("empNutremasRTD900g", 0.42);
        model.addAttribute("empNutremasFrescos600g", 0.34425);
        model.addAttribute("empNutremas600g", 0.34425);
        model.addAttribute("empRicoMosh360g", 0.23945);
        model.addAttribute("empAvenaEstrella50lb", 2.88949);
        return "gerencia/costos";
    }

    @PostMapping("/costos/calcular")
    public String calcular(
            @RequestParam Integer mes,
            @RequestParam Integer anio,
            @RequestParam Double costoAvenaKg,
            @RequestParam Double costoHarinaKg,
            @RequestParam Double costoCarbonatoKg,
            @RequestParam Double costoVitaminasKg,
            @RequestParam Double costoEEKg,
            @RequestParam Double costoVaporKg,
            @RequestParam Double costoMOKg,
            @RequestParam Double tipoCambio,
            @RequestParam Double propHarina,
            @RequestParam Double propCarbonato,
            @RequestParam Double propVitaminas,
            @RequestParam Double empNutremas1200g,
            @RequestParam Double empNutremas900g,
            @RequestParam Double empNutremasRTD900g,
            @RequestParam Double empNutremasFrescos600g,
            @RequestParam Double empNutremas600g,
            @RequestParam Double empRicoMosh360g,
            @RequestParam Double empAvenaEstrella50lb,
            Authentication auth,
            RedirectAttributes ra) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        CierreMensual cierre = cierreService.calcularCierre(
                mes, anio, costoAvenaKg, costoHarinaKg, costoCarbonatoKg,
                costoVitaminasKg, costoEEKg, costoVaporKg, costoMOKg, tipoCambio,
                propHarina, propCarbonato, propVitaminas,
                empNutremas1200g, empNutremas900g, empNutremasRTD900g,
                empNutremasFrescos600g, empNutremas600g, empRicoMosh360g,
                empAvenaEstrella50lb, usuario);
        ra.addFlashAttribute("exito", "Cálculo completado correctamente");
        return "redirect:/gerencia/costos/ver/" + cierre.getId();
    }

    @GetMapping("/costos/ver/{id}")
    public String verCierre(@PathVariable Long id, Model model) {
        CierreMensual cierre = cierreService.listarTodos().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst().orElseThrow();
        List<DetalleCostoPorProducto> detalles = cierreService.obtenerDetalles(cierre);

        // Formateo en Java con Locale.US — coma para miles, punto para decimales
        NumberFormat nf2 = NumberFormat.getNumberInstance(Locale.US);
        nf2.setMinimumFractionDigits(2);
        nf2.setMaximumFractionDigits(2);

        NumberFormat nf4 = NumberFormat.getNumberInstance(Locale.US);
        nf4.setMinimumFractionDigits(4);
        nf4.setMaximumFractionDigits(4);

        double totalCostoMes = detalles.stream()
                .mapToDouble(d -> d.getCostoTotalQ())
                .sum();

        model.addAttribute("cierre", cierre);
        model.addAttribute("detalles", detalles);
        model.addAttribute("totalCostoMes", "Q" + nf2.format(totalCostoMes));

        // Parámetros formateados
        model.addAttribute("costoAvenaFmt",    nf4.format(cierre.getCostoAvenaKg()));
        model.addAttribute("costoHarinaFmt",   nf4.format(cierre.getCostoHarinaKg()));
        model.addAttribute("costoCarbonatoFmt",nf4.format(cierre.getCostoCarbonatoKg()));
        model.addAttribute("costoVitaminasFmt",nf4.format(cierre.getCostoVitaminasKg()));
        model.addAttribute("costoEEFmt",       nf4.format(cierre.getCostoEEPorKg()));
        model.addAttribute("costoVaporFmt",    nf4.format(cierre.getCostoVaporPorKg()));
        model.addAttribute("costoMOFmt",       nf4.format(cierre.getCostoMOPorKg()));
        model.addAttribute("tipoCambioFmt",    "Q" + nf2.format(cierre.getTipoCambio()));

        // Detalles formateados
        for (DetalleCostoPorProducto d : detalles) {
            d.setCostoAvenaFmt(    "Q" + nf2.format(d.getCostoAvena()));
            d.setCostoEEFmt(       "Q" + nf2.format(d.getCostoEE()));
            d.setCostoVaporFmt(    "Q" + nf2.format(d.getCostoVapor()));
            d.setCostoMOFmt(       "Q" + nf2.format(d.getCostoMO()));
            d.setCostoTotalQFmt(   "Q" + nf2.format(d.getCostoTotalQ()));
            d.setCostoUnitarioQFmt("Q" + nf4.format(d.getCostoUnitarioQ()));
            d.setCostoUnitarioUSDFmt("$" + nf4.format(d.getCostoUnitarioUSD()));
            d.setKgProducidosFmt(  nf2.format(d.getKgProducidos()));
        }

        return "gerencia/ver-cierre";
    }

    @PostMapping("/costos/confirmar/{id}")
    public String confirmar(@PathVariable Long id, RedirectAttributes ra) {
        cierreService.confirmar(id);
        ra.addFlashAttribute("exito", "Cierre mensual confirmado");
        return "redirect:/gerencia/costos";
    }
}