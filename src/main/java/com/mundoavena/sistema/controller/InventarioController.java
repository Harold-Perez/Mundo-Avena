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


    // =========================================================
    // DETALLE DE GRANO
    // =========================================================

    @GetMapping("/grano")
    public String grano(Model model) {

        Semana semana = semanaService.obtenerSemanaActual();

        Bodega groat = bodegaRepository
                .findBySlug("grano-groat")
                .orElseThrow();

        Bodega avena = bodegaRepository
                .findBySlug("grano-avena")
                .orElseThrow();


        SiloResumenDTO siloGroat =
                construirSilo(groat, semana);

        SiloResumenDTO siloAvena =
                construirSilo(avena, semana);


        List<LoteResumenDTO> lotesGroat =
                construirLotes(groat, semana);

        List<LoteResumenDTO> lotesAvena =
                construirLotes(avena, semana);


        List<LoteResumenDTO> lotesCombinados =
                new ArrayList<>();

        lotesCombinados.addAll(lotesGroat);
        lotesCombinados.addAll(lotesAvena);


        model.addAttribute(
                "silos",
                List.of(siloGroat, siloAvena)
        );

        model.addAttribute(
                "lotes",
                lotesCombinados
        );

        model.addAttribute(
                "semanaActual",
                formatearSemana(semana)
        );

        model.addAttribute(
                "ultimoTicket",
                obtenerUltimoTicket(groat, avena)
        );

        model.addAttribute(
                "movimientosRecientes",
                construirMovimientosRecientes(groat, avena)
        );

        model.addAttribute(
                "subinventariosAlimentados",
                construirBodegasResumen(semana)
        );

        model.addAttribute(
                "productosGroat",
                productoRepository.findByBodegaAndActivoTrue(groat)
        );

        model.addAttribute(
                "productosAvena",
                productoRepository.findByBodegaAndActivoTrue(avena)
        );


        return "inventario/grano";
    }


    // =========================================================
    // SUBINVENTARIO
    // =========================================================

    @GetMapping("/subinventario/{slug}")
    public String subinventario(
            @PathVariable String slug,
            Model model) {

        Semana semana =
                semanaService.obtenerSemanaActual();

        Bodega bodega =
                bodegaRepository
                        .findBySlug(slug)
                        .orElseThrow();


        // DEBUG TEMPORAL
        System.out.println("=================================");
        System.out.println("BODEGA: " + bodega.getNombre());
        System.out.println("SLUG: " + bodega.getSlug());
        System.out.println("MANEJA LOTES: " + bodega.isManejaLotes());
        System.out.println("TIPO AGRUPACION: " + bodega.getTipoAgrupacion());
        System.out.println("=================================");


        BodegaResumenDTO resumen =
                construirResumenBodega(
                        bodega,
                        semana
                );


        model.addAttribute(
                "bodega",
                resumen
        );


        model.addAttribute(
                "todasLasBodegas",
                construirBodegasResumen(semana)
        );


        /*
         * IMPORTANTE:
         *
         * Cáscara:
         * manejaLotes = true
         * tipoAgrupacion = "sin-lotes"
         *
         * Por eso NO queremos mostrar la tabla
         * de lotes para Cáscara.
         *
         * Pero sí queremos que internamente
         * trabaje con LoteSemana.
         */

        Map<String, List<LoteResumenDTO>> lotesPorCategoria;

        if ("sin-lotes".equals(bodega.getTipoAgrupacion())) {

            lotesPorCategoria =
                    new LinkedHashMap<>();

        } else {

            lotesPorCategoria =
                    agruparLotes(bodega, semana);
        }


        model.addAttribute(
                "lotesPorCategoria",
                lotesPorCategoria
        );


        model.addAttribute(
                "semanaActual",
                formatearSemana(semana)
        );


        List<Producto> productosActivos = productoRepository.findByBodegaAndActivoTrue(bodega);

        Map<String, List<Producto>> productosPorCategoria = new LinkedHashMap<>();
        for (Producto p : productosActivos) {
            String llave = p.getCategoria() != null ? p.getCategoria() : "Sin categoría";
            productosPorCategoria.computeIfAbsent(llave, k -> new ArrayList<>()).add(p);
        }

        model.addAttribute("productos", productosActivos);
        model.addAttribute("productosPorCategoria", productosPorCategoria);

        return "inventario/subinventario";
    }

    // ---------- CREAR PRODUCTO (nuevo, con categoría nueva o existente) ----------
    @PostMapping("/producto")
    public String crearProducto(@RequestParam String bodegaSlug,
                                @RequestParam String nombre,
                                @RequestParam(required = false) String categoria,
                                @RequestParam String unidadMedida,
                                RedirectAttributes redirectAttributes) {

        Bodega bodega = bodegaRepository.findBySlug(bodegaSlug).orElseThrow();

        Producto producto = new Producto();
        producto.setBodega(bodega);
        producto.setNombre(nombre);
        producto.setCategoria(categoria != null && !categoria.isBlank() ? categoria.trim() : null);
        producto.setUnidadMedida(unidadMedida);
        producto.setActivo(true);

        productoRepository.save(producto);

        redirectAttributes.addFlashAttribute(
                "exito",
                "Producto \"" + nombre + "\" creado correctamente."
        );

        return "redirect:/inventario/subinventario/" + bodegaSlug;
    }

    // ---------- DESACTIVAR PRODUCTO ----------
    @PostMapping("/producto/{id}/desactivar")
    public String desactivarProducto(@PathVariable Long id,
                                     RedirectAttributes redirectAttributes) {

        Producto producto = productoRepository.findById(id).orElseThrow();
        String slug = producto.getBodega().getSlug();

        producto.setActivo(false);
        productoRepository.save(producto);

        redirectAttributes.addFlashAttribute(
                "exito",
                "Producto \"" + producto.getNombre() + "\" desactivado."
        );

        return "redirect:/inventario/subinventario/" + slug;
    }


    // ---------- REGISTRAR MOVIMIENTO ----------
    @PostMapping("/movimiento")
    public String registrarMovimiento(
            @RequestParam Long productoId,
            @RequestParam(required = false) String numeroLote,
            @RequestParam(required = false) String ubicacion,
            @RequestParam MovimientoLote.TipoMovimiento tipo,
            @RequestParam double cantidad,
            @RequestParam(required = false) Double pesoKg,
            @RequestParam(required = false) String ticketBascula,
            @RequestParam(required = false) String fecha,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Semana semana = semanaService.obtenerSemanaActual();

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow();

        Bodega bodega = producto.getBodega();

        /*
         * CÁSCARA:
         * No necesita que el usuario escriba un número de lote.
         * Internamente usamos un único lote llamado "SEMANAL".
         */
        if ("sin-lotes".equals(bodega.getTipoAgrupacion())) {
            numeroLote = "SEMANAL";
        }

        // Java necesita que esta variable sea efectivamente final
        final String numeroLoteFinal = numeroLote;

        LoteSemana lote = loteSemanaRepository
                .findBySemanaAndProductoAndNumeroLote(
                        semana,
                        producto,
                        numeroLoteFinal
                )
                .orElseGet(() -> {

                    LoteSemana nuevo = new LoteSemana();

                    nuevo.setSemana(semana);
                    nuevo.setProducto(producto);
                    nuevo.setNumeroLote(numeroLoteFinal);
                    nuevo.setUbicacion(ubicacion);

                    nuevo.setCantidadInicial(0);
                    nuevo.setSaldoActual(0);
                    nuevo.setPesoInicialKg(0.0);
                    nuevo.setPesoActualKg(0.0);

                    return loteSemanaRepository.save(nuevo);
                });

        LocalDate fechaFinal =
                (fecha != null && !fecha.isBlank())
                        ? LocalDate.parse(fecha)
                        : LocalDate.now();

        try {

            semanaService.registrarMovimiento(
                    lote,
                    tipo,
                    cantidad,
                    pesoKg,
                    fechaFinal,
                    ticketBascula,
                    authentication.getName()
            );

            redirectAttributes.addFlashAttribute(
                    "exito",
                    "Movimiento registrado correctamente."
            );

        } catch (IllegalStateException | IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        boolean esGrano = bodega.getSlug().startsWith("grano");

        if (esGrano) {
            return "redirect:/inventario/grano";
        }

        return "redirect:/inventario/subinventario/" + bodega.getSlug();
    }


    // =========================================================
    // OBTENER SLUG PARA REDIRECCIÓN DE ERROR
    // =========================================================

    private String obtenerSlugProducto(Long productoId) {

        try {

            Producto producto =
                    productoRepository
                            .findById(productoId)
                            .orElseThrow();

            return producto.getBodega().getSlug();

        } catch (Exception e) {

            return "cascara";
        }
    }


    // =========================================================
    // NUEVA SEMANA
    // =========================================================

    @PostMapping("/nueva-semana")
    public String nuevaSemana(

            @RequestParam(required = false)
            String volverA,

            RedirectAttributes redirectAttributes) {


        semanaService.crearNuevaSemana();


        redirectAttributes.addFlashAttribute(
                "exito",
                "Nueva semana creada. Los saldos se arrastraron correctamente."
        );


        return "redirect:" +
                (volverA != null
                        ? volverA
                        : "/inventario/grano");
    }

    // ---------- REGISTRAR AJUSTE (corrección de saldo, no es entrada ni salida) ----------
    @PostMapping("/ajuste")
    public String registrarAjuste(@RequestParam Long productoId,
                                  @RequestParam(required = false) String numeroLote,
                                  @RequestParam(required = false) String ubicacion,
                                  @RequestParam double nuevoSaldo,
                                  @RequestParam(required = false) Double nuevoPesoKg,
                                  @RequestParam String motivo,
                                  @RequestParam(required = false) String fecha,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {

        Semana semana = semanaService.obtenerSemanaActual();
        Producto producto = productoRepository.findById(productoId).orElseThrow();
        Bodega bodega = producto.getBodega();

        if ("sin-lotes".equals(bodega.getTipoAgrupacion())) {
            numeroLote = "SEMANAL";
        }
        final String numeroLoteFinal = numeroLote;

        LoteSemana lote = loteSemanaRepository.findBySemanaAndProductoAndNumeroLote(semana, producto, numeroLoteFinal)
                .orElseGet(() -> {
                    LoteSemana nuevo = new LoteSemana();
                    nuevo.setSemana(semana);
                    nuevo.setProducto(producto);
                    nuevo.setNumeroLote(numeroLoteFinal);
                    nuevo.setUbicacion(ubicacion);
                    nuevo.setCantidadInicial(0);
                    nuevo.setSaldoActual(0);
                    nuevo.setPesoInicialKg(0.0);
                    nuevo.setPesoActualKg(0.0);
                    return loteSemanaRepository.save(nuevo);
                });

        LocalDate fechaFinal = (fecha != null && !fecha.isBlank()) ? LocalDate.parse(fecha) : LocalDate.now();

        try {
            semanaService.registrarAjuste(lote, nuevoSaldo, nuevoPesoKg, fechaFinal, motivo, authentication.getName());
            redirectAttributes.addFlashAttribute("exito", "Ajuste registrado correctamente.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        boolean esGrano = bodega.getSlug().startsWith("grano");
        return "redirect:" + (esGrano ? "/inventario/grano" : "/inventario/subinventario/" + bodega.getSlug());
    }


    // =========================================================
    // CONSTRUIR SILO
    // =========================================================

    private SiloResumenDTO construirSilo(
            Bodega bodega,
            Semana semana) {


        List<LoteSemana> lotes =
                loteSemanaRepository
                        .findBySemanaAndProducto_Bodega(
                                semana,
                                bodega
                        );


        long saldoTotal =
                (long) lotes.stream()
                        .mapToDouble(
                                LoteSemana::getSaldoActual
                        )
                        .sum();


        long capacidad =
                bodega.getCapacidadKg() != null
                        ? bodega.getCapacidadKg()
                        : 0L;


        int porcentaje =
                capacidad > 0
                        ? (int) Math.round(
                        (saldoTotal * 100.0)
                                / capacidad
                )
                        : 0;


        return new SiloResumenDTO(
                bodega.getNombre(),
                saldoTotal,
                capacidad,
                porcentaje
        );
    }


    // =========================================================
    // CONSTRUIR LOTES
    // =========================================================

    private List<LoteResumenDTO> construirLotes(
            Bodega bodega,
            Semana semana) {


        List<LoteSemana> lotes =
                loteSemanaRepository
                        .findBySemanaAndProducto_Bodega(
                                semana,
                                bodega
                        );


        List<LoteResumenDTO> resultado =
                new ArrayList<>();


        for (LoteSemana l : lotes) {


            List<MovimientoLote> movimientos =
                    movimientoLoteRepository
                            .findByLoteSemanaOrderByFechaDesc(l);


            double entradas =
                    movimientos.stream()
                            .filter(m ->
                                    m.getTipo()
                                            == MovimientoLote.TipoMovimiento.ENTRADA
                            )
                            .mapToDouble(
                                    MovimientoLote::getCantidad
                            )
                            .sum();


            double salidas =
                    movimientos.stream()
                            .filter(m ->
                                    m.getTipo()
                                            == MovimientoLote.TipoMovimiento.SALIDA
                            )
                            .mapToDouble(
                                    MovimientoLote::getCantidad
                            )
                            .sum();


            double entradasPeso =
                    movimientos.stream()
                            .filter(m ->
                                    m.getTipo() == MovimientoLote.TipoMovimiento.ENTRADA
                                            && m.getPesoKg() != null
                            )
                            .mapToDouble(MovimientoLote::getPesoKg)
                            .sum();


            double salidasPeso =
                    movimientos.stream()
                            .filter(m ->
                                    m.getTipo() == MovimientoLote.TipoMovimiento.SALIDA
                                            && m.getPesoKg() != null
                            )
                            .mapToDouble(MovimientoLote::getPesoKg)
                            .sum();


            String agrupador =
                    switch (bodega.getTipoAgrupacion()) {

                        case "categoria" ->
                                l.getProducto().getCategoria();

                        case "ubicacion" ->
                                l.getUbicacion();

                        default ->
                                null;
                    };


            resultado.add(
                    new LoteResumenDTO(

                            l.getNumeroLote(),

                            agrupador,

                            l.getProducto().getNombre(),

                            l.getCantidadInicial(),

                            entradas,

                            salidas,

                            l.getSaldoActual(),

                            l.getProducto()
                                    .getUnidadMedida(),

                            LoteResumenDTO.EstadoLote
                                    .valueOf(
                                            l.getEstado().name()
                                    ),

                            l.getPesoInicialKg(),
                            entradasPeso,
                            salidasPeso,
                            l.getPesoActualKg()
                    )
            );
        }


        return resultado;
    }


    // =========================================================
    // AGRUPAR LOTES
    // =========================================================

    private Map<String, List<LoteResumenDTO>> agruparLotes(
            Bodega bodega,
            Semana semana) {


        List<LoteResumenDTO> lotes =
                construirLotes(
                        bodega,
                        semana
                );


        Map<String, List<LoteResumenDTO>> mapa =
                new LinkedHashMap<>();


        if ("plana".equals(
                bodega.getTipoAgrupacion())) {

            mapa.put(
                    "__plana__",
                    lotes
            );

            return mapa;
        }


        for (LoteResumenDTO l : lotes) {

            String llave =
                    l.getCategoria() != null
                            ? l.getCategoria()
                            : "Sin asignar";


            mapa.computeIfAbsent(
                    llave,
                    k -> new ArrayList<>()
            ).add(l);
        }


        return mapa;
    }


    // =========================================================
    // RESUMEN DE BODEGA
    // =========================================================

    private BodegaResumenDTO construirResumenBodega(
            Bodega bodega,
            Semana semana) {

        List<LoteSemana> lotes =
                loteSemanaRepository
                        .findBySemanaAndProducto_Bodega(
                                semana,
                                bodega
                        );

        int lotesActivos =
                (int) lotes.stream()
                        .filter(l -> l.getEstado() != LoteSemana.EstadoLote.AGOTADO)
                        .count();

        double saldoTotal =
                lotes.stream()
                        .mapToDouble(LoteSemana::getSaldoActual)
                        .sum();

        double pesoTotal =
                lotes.stream()
                        .filter(l -> l.getPesoActualKg() != null)
                        .mapToDouble(LoteSemana::getPesoActualKg)
                        .sum();

        boolean alertaBajo = false;

        String unidad =
                lotes.isEmpty() ? "" : lotes.get(0).getProducto().getUnidadMedida();

        String saldoFmt =
                formatearNumero(saldoTotal) + (unidad.isEmpty() ? "" : " " + unidad);

        String pesoFmt = formatearNumero(pesoTotal) + " kg";

        return new BodegaResumenDTO(
                bodega.getSlug(),
                bodega.getNombre(),
                bodega.getIcono(),
                lotesActivos,
                saldoFmt,
                bodega.isManejaLotes(),
                alertaBajo,
                bodega.getTipoAgrupacion(),
                pesoFmt
        );
    }


    // =========================================================
    // RESUMEN DE TODAS LAS BODEGAS
    // =========================================================

    private List<BodegaResumenDTO> construirBodegasResumen(
            Semana semana) {


        List<Bodega> bodegas =
                bodegaRepository.findAll()
                        .stream()
                        .filter(b ->
                                !b.getSlug()
                                        .startsWith("grano"))
                        .toList();


        return bodegas.stream()
                .map(b ->
                        construirResumenBodega(
                                b,
                                semana
                        )
                )
                .collect(Collectors.toList());
    }


    // =========================================================
    // MOVIMIENTOS RECIENTES
    // =========================================================

    private List<MovimientoRecienteDTO> construirMovimientosRecientes(
            Bodega groat,
            Bodega avena) {


        List<MovimientoLote> movs =
                new ArrayList<>();


        movs.addAll(
                movimientoLoteRepository
                        .findTop10ByLoteSemana_Producto_BodegaOrderByFechaDescFechaCreacionDesc(
                                groat
                        )
        );


        movs.addAll(
                movimientoLoteRepository
                        .findTop10ByLoteSemana_Producto_BodegaOrderByFechaDescFechaCreacionDesc(
                                avena
                        )
        );


        movs.sort(
                Comparator
                        .comparing(
                                MovimientoLote::getFecha
                        )
                        .reversed()
        );


        DateTimeFormatter formatoFecha =
                DateTimeFormatter
                        .ofPattern("dd MMM")
                        .withLocale(
                                new Locale("es", "GT")
                        );


        List<MovimientoRecienteDTO> resultado =
                new ArrayList<>();


        for (
                MovimientoLote m :
                movs.stream()
                        .limit(10)
                        .toList()
        ) {


            String signo =
                    m.getTipo()
                            == MovimientoLote.TipoMovimiento.ENTRADA
                            ? "+"
                            : "-";


            String cantidadFmt =
                    signo
                            + formatearNumero(
                            m.getCantidad()
                    )
                            + " "
                            + m.getLoteSemana()
                            .getProducto()
                            .getUnidadMedida();


            resultado.add(
                    new MovimientoRecienteDTO(

                            m.getFecha()
                                    .format(formatoFecha),

                            m.getLoteSemana()
                                    .getProducto()
                                    .getNombre(),

                            m.getTipo().name(),

                            cantidadFmt,

                            m.getTicketBascula() != null
                                    ? m.getTicketBascula()
                                    : "—",

                            formatearNumero(
                                    m.getLoteSemana()
                                            .getSaldoActual()
                            )
                                    + " "
                                    + m.getLoteSemana()
                                    .getProducto()
                                    .getUnidadMedida()
                    )
            );
        }


        return resultado;
    }


    // =========================================================
    // ÚLTIMO TICKET
    // =========================================================

    private String obtenerUltimoTicket(
            Bodega groat,
            Bodega avena) {


        List<MovimientoLote> movs =
                new ArrayList<>();


        movs.addAll(
                movimientoLoteRepository
                        .findTop10ByLoteSemana_Producto_BodegaOrderByFechaDescFechaCreacionDesc(
                                groat
                        )
        );


        movs.addAll(
                movimientoLoteRepository
                        .findTop10ByLoteSemana_Producto_BodegaOrderByFechaDescFechaCreacionDesc(
                                avena
                        )
        );


        return movs.stream()

                .filter(m ->
                        m.getTicketBascula() != null
                )

                .max(
                        Comparator.comparing(
                                MovimientoLote::getFecha
                        )
                )

                .map(
                        MovimientoLote::getTicketBascula
                )

                .orElse("—");
    }


    // =========================================================
    // FORMATEAR SEMANA
    // =========================================================

    private String formatearSemana(
            Semana semana) {


        DateTimeFormatter f =
                DateTimeFormatter
                        .ofPattern("dd-MM-yyyy");


        return "DEL "
                + semana.getFechaInicio()
                .format(f)
                + " AL "
                + semana.getFechaFin()
                .format(f);
    }


    // =========================================================
    // FORMATEAR NÚMERO
    // =========================================================

    private String formatearNumero(
            double numero) {

        return String.format(
                "%,.0f",
                numero
        );
    }
}