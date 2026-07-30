package com.mundoavena.sistema.controller;

import com.mundoavena.sistema.dto.inventario.BodegaResumenDTO;
import com.mundoavena.sistema.dto.inventario.LoteResumenDTO;
import com.mundoavena.sistema.dto.inventario.MovimientoRecienteDTO;
import com.mundoavena.sistema.dto.inventario.SiloResumenDTO;
import com.mundoavena.sistema.model.*;
import com.mundoavena.sistema.repository.BodegaRepository;
import com.mundoavena.sistema.repository.LoteSemanaRepository;
import com.mundoavena.sistema.repository.MovimientoLoteRepository;
import com.mundoavena.sistema.repository.ProductoRepository;
import com.mundoavena.sistema.service.SemanaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/inventario")
public class InventarioController {

    @Autowired
    private BodegaRepository bodegaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private LoteSemanaRepository loteSemanaRepository;

    @Autowired
    private MovimientoLoteRepository movimientoLoteRepository;

    @Autowired
    private SemanaService semanaService;

    // ---------- DETALLE DE GRANO ----------
    @GetMapping("/grano")
    public String grano(Model model) {
        Semana semana = semanaService.obtenerSemanaActual();
        Bodega groat = bodegaRepository.findBySlug("grano-groat").orElseThrow();
        Bodega avena = bodegaRepository.findBySlug("grano-avena").orElseThrow();

        SiloResumenDTO siloGroat = construirSilo(groat, semana);
        SiloResumenDTO siloAvena = construirSilo(avena, semana);

        List<LoteResumenDTO> lotesGroat = construirLotes(groat, semana);
        List<LoteResumenDTO> lotesAvena = construirLotes(avena, semana);
        List<LoteResumenDTO> lotesCombinados = new ArrayList<>();
        lotesCombinados.addAll(lotesGroat);
        lotesCombinados.addAll(lotesAvena);

        model.addAttribute("silos", List.of(siloGroat, siloAvena));
        model.addAttribute("lotes", lotesCombinados);
        model.addAttribute("semanaActual", formatearSemana(semana));
        model.addAttribute("ultimoTicket", obtenerUltimoTicket(groat, avena));
        model.addAttribute("movimientosRecientes", construirMovimientosRecientes(groat, avena));
        model.addAttribute("subinventariosAlimentados", construirBodegasResumen(semana));
        model.addAttribute("productosGroat", productoRepository.findByBodegaAndActivoTrue(groat));
        model.addAttribute("productosAvena", productoRepository.findByBodegaAndActivoTrue(avena));

        return "inventario/grano";
    }

    // ---------- PLANTILLA GENÉRICA DE SUBINVENTARIO ----------
    @GetMapping("/subinventario/{slug}")
    public String subinventario(@PathVariable String slug, Model model) {
        Semana semana = semanaService.obtenerSemanaActual();
        Bodega bodega = bodegaRepository.findBySlug(slug).orElseThrow();

        BodegaResumenDTO resumen = construirResumenBodega(bodega, semana);
        model.addAttribute("bodega", resumen);
        model.addAttribute("todasLasBodegas", construirBodegasResumen(semana));
        model.addAttribute("lotesPorCategoria", !bodega.getTipoAgrupacion().equals("sin-lotes") ? agruparLotes(bodega, semana) : new LinkedHashMap<>());
        model.addAttribute("semanaActual", formatearSemana(semana));
        model.addAttribute("productos", productoRepository.findByBodegaAndActivoTrue(bodega));

        return "inventario/subinventario";
    }

    // ---------- REGISTRAR MOVIMIENTO (entrada o salida) ----------
    // ---------- REGISTRAR MOVIMIENTO (entrada o salida) ----------
    @PostMapping("/movimiento")
    public String registrarMovimiento(@RequestParam Long productoId,
                                      @RequestParam String numeroLote,
                                      @RequestParam(required = false) String ubicacion,
                                      @RequestParam MovimientoLote.TipoMovimiento tipo,
                                      @RequestParam double cantidad,
                                      @RequestParam(required = false) String ticketBascula,
                                      @RequestParam(required = false) String fecha,
                                      Authentication authentication,
                                      RedirectAttributes redirectAttributes) {

        Semana semana = semanaService.obtenerSemanaActual();
        Producto producto = productoRepository.findById(productoId).orElseThrow();
        Bodega bodega = producto.getBodega();

        LoteSemana lote = loteSemanaRepository.findBySemanaAndProductoAndNumeroLote(semana, producto, numeroLote)
                .orElseGet(() -> {
                    LoteSemana nuevo = new LoteSemana();
                    nuevo.setSemana(semana);
                    nuevo.setProducto(producto);
                    nuevo.setNumeroLote(numeroLote);
                    nuevo.setUbicacion(ubicacion);
                    nuevo.setCantidadInicial(0);
                    nuevo.setSaldoActual(0);
                    return loteSemanaRepository.save(nuevo);
                });

        LocalDate fechaFinal = (fecha != null && !fecha.isBlank()) ? LocalDate.parse(fecha) : LocalDate.now();

        try {
            semanaService.registrarMovimiento(lote, tipo, cantidad, fechaFinal,
                    ticketBascula, authentication.getName());
            redirectAttributes.addFlashAttribute("exito", "Movimiento registrado correctamente.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        boolean esGrano = bodega.getSlug().startsWith("grano");
        return "redirect:" + (esGrano ? "/inventario/grano" : "/inventario/subinventario/" + bodega.getSlug());
    }

    // ---------- NUEVA SEMANA ----------
    @PostMapping("/nueva-semana")
    public String nuevaSemana(@RequestParam(required = false) String volverA, RedirectAttributes redirectAttributes) {
        semanaService.crearNuevaSemana();
        redirectAttributes.addFlashAttribute("exito", "Nueva semana creada. Los saldos se arrastraron correctamente.");
        return "redirect:" + (volverA != null ? volverA : "/inventario/grano");
    }

    // ---------- HELPERS ----------

    private SiloResumenDTO construirSilo(Bodega bodega, Semana semana) {
        List<LoteSemana> lotes = loteSemanaRepository.findBySemanaAndProducto_Bodega(semana, bodega);
        long saldoTotal = (long) lotes.stream().mapToDouble(LoteSemana::getSaldoActual).sum();
        long capacidad = bodega.getCapacidadKg() != null ? bodega.getCapacidadKg() : 0L;
        int porcentaje = capacidad > 0 ? (int) Math.round((saldoTotal * 100.0) / capacidad) : 0;
        return new SiloResumenDTO(bodega.getNombre(), saldoTotal, capacidad, porcentaje);
    }

    private List<LoteResumenDTO> construirLotes(Bodega bodega, Semana semana) {
        List<LoteSemana> lotes = loteSemanaRepository.findBySemanaAndProducto_Bodega(semana, bodega);
        List<LoteResumenDTO> resultado = new ArrayList<>();
        for (LoteSemana l : lotes) {
            List<MovimientoLote> movimientos = movimientoLoteRepository.findByLoteSemanaOrderByFechaDesc(l);
            double entradas = movimientos.stream().filter(m -> m.getTipo() == MovimientoLote.TipoMovimiento.ENTRADA).mapToDouble(MovimientoLote::getCantidad).sum();
            double salidas = movimientos.stream().filter(m -> m.getTipo() == MovimientoLote.TipoMovimiento.SALIDA).mapToDouble(MovimientoLote::getCantidad).sum();

            String agrupador = switch (bodega.getTipoAgrupacion()) {
                case "categoria" -> l.getProducto().getCategoria();
                case "ubicacion" -> l.getUbicacion();
                default -> null;
            };

            resultado.add(new LoteResumenDTO(l.getNumeroLote(), agrupador, l.getProducto().getNombre(),
                    l.getCantidadInicial(), entradas, salidas, l.getSaldoActual(), l.getProducto().getUnidadMedida(),
                    LoteResumenDTO.EstadoLote.valueOf(l.getEstado().name())));
        }
        return resultado;
    }

    private Map<String, List<LoteResumenDTO>> agruparLotes(Bodega bodega, Semana semana) {
        List<LoteResumenDTO> lotes = construirLotes(bodega, semana);
        Map<String, List<LoteResumenDTO>> mapa = new LinkedHashMap<>();

        if (bodega.getTipoAgrupacion().equals("plana")) {
            mapa.put("__plana__", lotes);
            return mapa;
        }

        for (LoteResumenDTO l : lotes) {
            String llave = l.getCategoria() != null ? l.getCategoria() : "Sin asignar";
            mapa.computeIfAbsent(llave, k -> new ArrayList<>()).add(l);
        }
        return mapa;
    }

    private BodegaResumenDTO construirResumenBodega(Bodega bodega, Semana semana) {
        List<LoteSemana> lotes = loteSemanaRepository.findBySemanaAndProducto_Bodega(semana, bodega);
        int lotesActivos = (int) lotes.stream().filter(l -> l.getEstado() != LoteSemana.EstadoLote.AGOTADO).count();
        double saldoTotal = lotes.stream().mapToDouble(LoteSemana::getSaldoActual).sum();
        boolean alertaBajo = lotes.stream().anyMatch(l -> l.getEstado() == LoteSemana.EstadoLote.SALDO_BAJO);
        String unidad = lotes.isEmpty() ? "" : lotes.get(0).getProducto().getUnidadMedida();
        String saldoFmt = formatearNumero(saldoTotal) + (unidad.isEmpty() ? "" : " " + unidad);

        return new BodegaResumenDTO(bodega.getSlug(), bodega.getNombre(), bodega.getIcono(),
                lotesActivos, saldoFmt, bodega.isManejaLotes(), alertaBajo, bodega.getTipoAgrupacion());
    }

    private List<BodegaResumenDTO> construirBodegasResumen(Semana semana) {
        List<Bodega> bodegas = bodegaRepository.findAll().stream()
                .filter(b -> !b.getSlug().startsWith("grano"))
                .toList();
        return bodegas.stream().map(b -> construirResumenBodega(b, semana)).collect(Collectors.toList());
    }

    private List<MovimientoRecienteDTO> construirMovimientosRecientes(Bodega groat, Bodega avena) {
        List<MovimientoLote> movs = new ArrayList<>();
        movs.addAll(movimientoLoteRepository.findTop10ByLoteSemana_Producto_BodegaOrderByFechaDescFechaCreacionDesc(groat));
        movs.addAll(movimientoLoteRepository.findTop10ByLoteSemana_Producto_BodegaOrderByFechaDescFechaCreacionDesc(avena));
        movs.sort(Comparator.comparing(MovimientoLote::getFecha).reversed());

        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd MMM").withLocale(new Locale("es", "GT"));

        List<MovimientoRecienteDTO> resultado = new ArrayList<>();
        for (MovimientoLote m : movs.stream().limit(10).toList()) {
            String signo = m.getTipo() == MovimientoLote.TipoMovimiento.ENTRADA ? "+" : "-";
            String cantidadFmt = signo + formatearNumero(m.getCantidad()) + " " + m.getLoteSemana().getProducto().getUnidadMedida();
            resultado.add(new MovimientoRecienteDTO(
                    m.getFecha().format(formatoFecha),
                    m.getLoteSemana().getProducto().getNombre(),
                    m.getTipo().name(),
                    cantidadFmt,
                    m.getTicketBascula() != null ? m.getTicketBascula() : "—",
                    formatearNumero(m.getLoteSemana().getSaldoActual()) + " " + m.getLoteSemana().getProducto().getUnidadMedida()
            ));
        }
        return resultado;
    }

    private String obtenerUltimoTicket(Bodega groat, Bodega avena) {
        List<MovimientoLote> movs = new ArrayList<>();
        movs.addAll(movimientoLoteRepository.findTop10ByLoteSemana_Producto_BodegaOrderByFechaDescFechaCreacionDesc(groat));
        movs.addAll(movimientoLoteRepository.findTop10ByLoteSemana_Producto_BodegaOrderByFechaDescFechaCreacionDesc(avena));
        return movs.stream()
                .filter(m -> m.getTicketBascula() != null)
                .max(Comparator.comparing(MovimientoLote::getFecha))
                .map(MovimientoLote::getTicketBascula)
                .orElse("—");
    }

    private String formatearSemana(Semana semana) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return "DEL " + semana.getFechaInicio().format(f) + " AL " + semana.getFechaFin().format(f);
    }

    private String formatearNumero(double numero) {
        return String.format("%,.0f", numero);
    }
}