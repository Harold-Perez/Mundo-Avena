package com.mundoavena.sistema.controller;

import com.mundoavena.sistema.dto.inventario.BodegaResumenDTO;
import com.mundoavena.sistema.dto.inventario.LoteResumenDTO;
import com.mundoavena.sistema.dto.inventario.MovimientoRecienteDTO;
import com.mundoavena.sistema.dto.inventario.SiloResumenDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/inventario")
public class InventarioController {

    // ---------- DASHBOARD GENERAL (huérfano por ahora, futuro dashboard de Gerencia) ----------
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        SiloResumenDTO groat = new SiloResumenDTO("Silo Groat", 412_300L, 600_000L, calcularPorcentaje(412_300L, 600_000L));
        SiloResumenDTO avena = new SiloResumenDTO("Silo Avena con Cáscara", 528_900L, 600_000L, calcularPorcentaje(528_900L, 600_000L));

        model.addAttribute("silos", List.of(groat, avena));
        model.addAttribute("saldoCombinadoKg", groat.getSaldoActualKg() + avena.getSaldoActualKg());
        model.addAttribute("capacidadCombinadaKg", groat.getCapacidadKg() + avena.getCapacidadKg());

        model.addAttribute("subinventarios", subinventariosMock());
        model.addAttribute("semanaActual", "DEL 20 AL 26-07-2026");

        return "inventario/dashboard";
    }

    // ---------- DETALLE DE GRANO ----------
    @GetMapping("/grano")
    public String grano(Model model) {
        SiloResumenDTO groat = new SiloResumenDTO("Silo Groat", 412_300L, 600_000L, calcularPorcentaje(412_300L, 600_000L));
        SiloResumenDTO avena = new SiloResumenDTO("Silo Avena con Cáscara", 528_900L, 600_000L, calcularPorcentaje(528_900L, 600_000L));

        model.addAttribute("silos", List.of(groat, avena));
        model.addAttribute("lotes", lotesGranoMock());
        model.addAttribute("semanaActual", "DEL 20 AL 26-07-2026");
        model.addAttribute("ultimoTicket", "4821");
        model.addAttribute("movimientosRecientes", movimientosRecientesMock());
        model.addAttribute("subinventariosAlimentados", subinventariosMock());

        return "inventario/grano";
    }

    // ---------- PLANTILLA GENÉRICA DE SUBINVENTARIO ----------
    @GetMapping("/subinventario/{slug}")
    public String subinventario(@PathVariable String slug, Model model) {
        BodegaResumenDTO bodega = subinventariosMock().stream()
                .filter(b -> b.getSlug().equals(slug))
                .findFirst()
                .orElse(subinventariosMock().get(0));

        model.addAttribute("bodega", bodega);
        model.addAttribute("todasLasBodegas", subinventariosMock());
        model.addAttribute("lotesPorCategoria", bodega.isManejaLotes() ? lotesGenericosMock(slug) : new LinkedHashMap<>());
        model.addAttribute("semanaActual", "DEL 20 AL 26-07-2026");

        return "inventario/subinventario";
    }

    // ---------- MOCK DATA (temporal, hasta generalizar el modelo real) ----------
    private int calcularPorcentaje(long actual, long capacidad) {
        return (int) Math.round((actual * 100.0) / capacidad);
    }

    private List<BodegaResumenDTO> subinventariosMock() {
        List<BodegaResumenDTO> lista = new ArrayList<>();
        lista.add(new BodegaResumenDTO("descascarado", "Descascarado", "🌰", 8, "3,240 sacos", true, false, "plana"));
        lista.add(new BodegaResumenDTO("cascara", "Cáscara", "🍂", 0, "1,850 kg", false, false, "sin-lotes"));
        lista.add(new BodegaResumenDTO("bodega-b", "Bodega B", "🐄", 12, "6,120 kg", true, true, "ubicacion"));
        lista.add(new BodegaResumenDTO("harina-otw", "Harina OTW", "🥣", 5, "980 sacos", true, false, "plana"));
        lista.add(new BodegaResumenDTO("producto-terminado", "Producto Terminado", "📦", 21, "14,760 unidades", true, true, "categoria"));
        return lista;
    }

    private List<LoteResumenDTO> lotesGranoMock() {
        List<LoteResumenDTO> lista = new ArrayList<>();
        lista.add(new LoteResumenDTO("C-041", null, "", 45_000, 0, 12_300, 32_700, "kg", LoteResumenDTO.EstadoLote.ACTIVO));
        lista.add(new LoteResumenDTO("C-042", null, "", 38_500, 0, 38_500, 0, "kg", LoteResumenDTO.EstadoLote.AGOTADO));
        lista.add(new LoteResumenDTO("C-045", null, "", 0, 51_200, 3_100, 48_100, "kg", LoteResumenDTO.EstadoLote.ACTIVO));
        lista.add(new LoteResumenDTO("C-046", null, "", 9_683, 0, 8_900, 783, "kg", LoteResumenDTO.EstadoLote.SALDO_BAJO));
        return lista;
    }

    private List<MovimientoRecienteDTO> movimientosRecientesMock() {
        List<MovimientoRecienteDTO> lista = new ArrayList<>();
        lista.add(new MovimientoRecienteDTO("22 jul, 10:32", "Groat", "ENTRADA", "+18.2 t", "4821", "412.3 t"));
        lista.add(new MovimientoRecienteDTO("22 jul, 08:15", "Avena c/c", "SALIDA", "-6.5 t", "4820", "528.9 t"));
        lista.add(new MovimientoRecienteDTO("21 jul, 16:40", "Groat", "SALIDA", "-12.0 t", "4818", "394.1 t"));
        return lista;
    }

    private Map<String, List<LoteResumenDTO>> lotesGenericosMock(String slug) {
        Map<String, List<LoteResumenDTO>> mapa = new LinkedHashMap<>();

        switch (slug) {
            case "descascarado" -> {
                // Plana — sin agrupación real en el Excel, cada producto es su propia fila
                mapa.put("__plana__", List.of(
                        new LoteResumenDTO("4725AC24", null, "Puntilla", 93, 21, 79, 35, "sacos", LoteResumenDTO.EstadoLote.ACTIVO),
                        new LoteResumenDTO("1025AC04", null, "Avenilla", 110, 0, 36, 74, "sacos", LoteResumenDTO.EstadoLote.ACTIVO),
                        new LoteResumenDTO("2225AC09", null, "Avenilla", 40, 0, 0, 40, "sacos", LoteResumenDTO.EstadoLote.ACTIVO),
                        new LoteResumenDTO("4025AC018", null, "Avenilla", 39, 0, 39, 0, "sacos", LoteResumenDTO.EstadoLote.AGOTADO),
                        new LoteResumenDTO("S/N", null, "Fibra", 28, 0, 0, 28, "sacos", LoteResumenDTO.EstadoLote.ACTIVO),
                        new LoteResumenDTO("S/N", null, "Maxis Sacos", 2, 0, 0, 2, "sacos", LoteResumenDTO.EstadoLote.SALDO_BAJO)
                ));
            }
            case "harina-otw" -> {
                // Plana — todos los lotes son el mismo producto "Harina OTW"
                mapa.put("__plana__", List.of(
                        new LoteResumenDTO("3525HF253", null, "Harina OTW", 227, 0, 0, 227, "sacos", LoteResumenDTO.EstadoLote.ACTIVO),
                        new LoteResumenDTO("2925HF201", null, "Harina OTW", 172, 0, 0, 172, "sacos", LoteResumenDTO.EstadoLote.ACTIVO),
                        new LoteResumenDTO("2825HF195", null, "Harina OTW", 147, 0, 0, 147, "sacos", LoteResumenDTO.EstadoLote.ACTIVO),
                        new LoteResumenDTO("3025HF214", null, "Harina OTW", 97, 0, 0, 97, "sacos", LoteResumenDTO.EstadoLote.ACTIVO),
                        new LoteResumenDTO("2925HF209", null, "Harina OTW", 45, 0, 0, 45, "sacos", LoteResumenDTO.EstadoLote.SALDO_BAJO)
                ));
            }
            case "bodega-b" -> {
                // Agrupado por UBICACIÓN física, no por producto
                mapa.put("Ubicación 1", List.of(
                        new LoteResumenDTO("46224HM184", "Ubicación 1", "Hojuela Mosh Consumo Animal", 24, 0, 0, 24, "sacos", LoteResumenDTO.EstadoLote.ACTIVO),
                        new LoteResumenDTO("0525HM49", "Ubicación 1", "Hojuela Mosh Consumo Animal", 32, 0, 0, 32, "sacos", LoteResumenDTO.EstadoLote.ACTIVO),
                        new LoteResumenDTO("1125HM99", "Ubicación 1", "Hojuela Mosh Consumo Animal", 37, 0, 0, 37, "sacos", LoteResumenDTO.EstadoLote.ACTIVO)
                ));
                mapa.put("Ubicación 2", List.of(
                        new LoteResumenDTO("4025HM256", "Ubicación 2", "Hojuela Mosh Consumo Animal", 15, 0, 0, 15, "sacos", LoteResumenDTO.EstadoLote.SALDO_BAJO),
                        new LoteResumenDTO("3024PX96", "Ubicación 2", "Hojuela Mosh Consumo Animal", 30, 0, 0, 30, "sacos", LoteResumenDTO.EstadoLote.ACTIVO)
                ));
                mapa.put("Ubicación 3", List.of(
                        new LoteResumenDTO("0325HM13", "Ubicación 3", "Hojuela Mosh Consumo Animal", 40, 0, 0, 40, "sacos", LoteResumenDTO.EstadoLote.ACTIVO),
                        new LoteResumenDTO("0325HM20", "Ubicación 3", "Hojuela Mosh Consumo Animal", 5, 0, 0, 5, "sacos", LoteResumenDTO.EstadoLote.SALDO_BAJO)
                ));
            }
            case "producto-terminado" -> {
                // Agrupado por CATEGORÍA de producto
                mapa.put("Avena Mosh", List.of(
                        new LoteResumenDTO("4225ME79", "Avena Mosh", "Avena Mosh 600g", 600, 0, 450, 150, "sacos", LoteResumenDTO.EstadoLote.ACTIVO),
                        new LoteResumenDTO("4825ME89", "Avena Mosh", "Avena Mosh 900g", 900, 0, 900, 0, "sacos", LoteResumenDTO.EstadoLote.AGOTADO),
                        new LoteResumenDTO("4225ME81", "Avena Mosh", "Avena Mosh 1200g", 1200, 0, 226, 974, "sacos", LoteResumenDTO.EstadoLote.ACTIVO)
                ));
                mapa.put("Bobina Mosh Quaquer", List.of(
                        new LoteResumenDTO("4225ME81", "Bobina Mosh Quaquer", "Bobina 600 gramos", 1632, 0, 1632, 0, "unidades", LoteResumenDTO.EstadoLote.AGOTADO),
                        new LoteResumenDTO("4825ME90", "Bobina Mosh Quaquer", "Bobina 900 gramos", 1218, 0, 1135, 83, "unidades", LoteResumenDTO.EstadoLote.SALDO_BAJO)
                ));
                mapa.put("Bobina Avena para Frescos", List.of(
                        new LoteResumenDTO("4225ME79", "Bobina Avena para Frescos", "Bobina 600 gramos", 346, 0, 304, 42, "unidades", LoteResumenDTO.EstadoLote.ACTIVO)
                ));
                mapa.put("Rico Mosh", List.of(
                        new LoteResumenDTO("S/N", "Rico Mosh", "Rico Mosh", 0, 0, 0, 0, "unidades", LoteResumenDTO.EstadoLote.AGOTADO)
                ));
            }
            default -> mapa.put("__plana__", List.of());
        }

        return mapa;
    }
}