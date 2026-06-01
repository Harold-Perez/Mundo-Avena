package com.mundoavena.sistema.service;

import com.mundoavena.sistema.model.*;
import com.mundoavena.sistema.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CierreMensualService {

    @Autowired
    private CierreMensualRepository cierreRepository;

    @Autowired
    private DetalleCostoPorProductoRepository detalleRepository;

    @Autowired
    private ControlDiarioPTRepository controlPTRepository;

    @Autowired
    private ProporccionesProductoRepository proporccionesRepository;

    public List<CierreMensual> listarTodos() {
        return cierreRepository.findAllByOrderByAnioDescMesDesc();
    }

    public Optional<CierreMensual> buscarPorMesAnio(Integer mes, Integer anio) {
        return cierreRepository.findByMesAndAnio(mes, anio);
    }

    public List<DetalleCostoPorProducto> obtenerDetalles(CierreMensual cierre) {
        return detalleRepository.findByCierre(cierre);
    }

    private double getCostoEmpaque(String producto, int unidades, CierreMensual cierre) {
        Optional<ProporccionesProducto> props = proporccionesRepository.findByNombreProducto(producto);
        System.out.println(">>> getCostoEmpaque: producto=" + producto
                + " found=" + props.isPresent()
                + (props.isPresent() ? " empaque=" + props.get().getCostoEmpaqueUnitario() : ""));
        if (props.isPresent()
                && props.get().getCostoEmpaqueUnitario() != null
                && props.get().getCostoEmpaqueUnitario() > 0) {
            return props.get().getCostoEmpaqueUnitario() * unidades;
        }
        // Fallback por keywords
        String p = producto.toLowerCase();
        double costoUnit = 0.0;
        if (p.contains("1200"))                          costoUnit = cierre.getEmpNutremas1200g();
        else if (p.contains("rtd"))                      costoUnit = cierre.getEmpNutremasRTD900g();
        else if (p.contains("fresco"))                   costoUnit = cierre.getEmpNutremasFrescos600g();
        else if (p.contains("900"))                      costoUnit = cierre.getEmpNutremas900g();
        else if (p.contains("600"))                      costoUnit = cierre.getEmpNutremas600g();
        else if (p.contains("mosh") || p.contains("360")) costoUnit = cierre.getEmpRicoMosh360g();
        else if (p.contains("estrella") || p.contains("50lb")) costoUnit = cierre.getEmpAvenaEstrella50lb();
        return costoUnit * unidades;
    }

    private double[] getProporciones(String producto, CierreMensual cierre) {
        Optional<ProporccionesProducto> props = proporccionesRepository.findByNombreProducto(producto);
        if (props.isPresent()) {
            return new double[]{
                    props.get().getPropHarina(),
                    props.get().getPropCarbonato(),
                    props.get().getPropVitaminas()
            };
        }
        return new double[]{
                cierre.getPropHarinaKg(),
                cierre.getPropCarbonatoKg(),
                cierre.getPropVitaminasKg()
        };
    }

    public CierreMensual calcularCierre(Integer mes, Integer anio,
                                        Double costoAvenaKg, Double costoHarinaKg,
                                        Double costoCarbonatoKg, Double costoVitaminasKg,
                                        Double costoEEKg, Double costoVaporKg,
                                        Double costoMOKg, Double tipoCambio,
                                        Double propHarina, Double propCarbonato,
                                        Double propVitaminas,
                                        Double empNutremas1200g, Double empNutremas900g,
                                        Double empNutremasRTD900g, Double empNutremasFrescos600g,
                                        Double empNutremas600g, Double empRicoMosh360g,
                                        Double empAvenaEstrella50lb,
                                        Usuario usuario) {

        CierreMensual cierre = cierreRepository.findByMesAndAnio(mes, anio)
                .orElse(new CierreMensual());

        cierre.setMes(mes);
        cierre.setAnio(anio);
        cierre.setCostoAvenaKg(costoAvenaKg);
        cierre.setCostoHarinaKg(costoHarinaKg);
        cierre.setCostoCarbonatoKg(costoCarbonatoKg);
        cierre.setCostoVitaminasKg(costoVitaminasKg);
        cierre.setCostoEEPorKg(costoEEKg);
        cierre.setCostoVaporPorKg(costoVaporKg);
        cierre.setCostoMOPorKg(costoMOKg);
        cierre.setTipoCambio(tipoCambio);
        cierre.setPropHarinaKg(propHarina);
        cierre.setPropCarbonatoKg(propCarbonato);
        cierre.setPropVitaminasKg(propVitaminas);
        cierre.setEmpNutremas1200g(empNutremas1200g);
        cierre.setEmpNutremas900g(empNutremas900g);
        cierre.setEmpNutremasRTD900g(empNutremasRTD900g);
        cierre.setEmpNutremasFrescos600g(empNutremasFrescos600g);
        cierre.setEmpNutremas600g(empNutremas600g);
        cierre.setEmpRicoMosh360g(empRicoMosh360g);
        cierre.setEmpAvenaEstrella50lb(empAvenaEstrella50lb);
        cierre.setCalculadoPor(usuario);
        cierre.setEstado(EstadoCierre.BORRADOR);
        cierre = cierreRepository.save(cierre);

        // Borrar detalles anteriores
        List<DetalleCostoPorProducto> anteriores = detalleRepository.findByCierre(cierre);
        detalleRepository.deleteAll(anteriores);

        // Obtener registros PT del mes
        List<ControlDiarioPT> registrosPT = controlPTRepository.findAll().stream()
                .filter(r -> r.getFecha() != null
                        && r.getFecha().getMonthValue() == mes
                        && r.getFecha().getYear() == anio)
                .toList();

        // Agrupar por producto
        Map<String, List<ControlDiarioPT>> porProducto = new HashMap<>();
        for (ControlDiarioPT pt : registrosPT) {
            porProducto.computeIfAbsent(pt.getProducto(), k -> new ArrayList<>()).add(pt);
        }

        for (Map.Entry<String, List<ControlDiarioPT>> entry : porProducto.entrySet()) {
            String producto = entry.getKey();
            List<ControlDiarioPT> registros = entry.getValue();

            int totalUnidades = registros.stream()
                    .mapToInt(r -> r.getTotalUnidades() != null ? r.getTotalUnidades() : 0).sum();
            double totalKg = registros.stream()
                    .mapToDouble(r -> r.getTotalProductoEmpacadoKg() != null ? r.getTotalProductoEmpacadoKg() : 0).sum();
            double totalMPKg = registros.stream()
                    .mapToDouble(r -> r.getMateriaPrimaUtilizadaKg() != null ? r.getMateriaPrimaUtilizadaKg() : 0).sum();

            if (totalUnidades == 0 || totalKg == 0) continue;

            double[] props = getProporciones(producto, cierre);
            double propH = props[0];
            double propC = props[1];
            double propV = props[2];

            double cAvena     = totalMPKg * costoAvenaKg;
            double cHarina    = totalKg * propH * costoHarinaKg;
            double cCarbonato = totalKg * propC * costoCarbonatoKg;
            double cVitaminas = totalKg * propV * costoVitaminasKg;
            double cEmpaque   = getCostoEmpaque(producto, totalUnidades, cierre);
            double cEE        = totalKg * costoEEKg;
            double cVapor     = totalKg * costoVaporKg;
            double cMO        = totalKg * costoMOKg;

            double costoTotal = cAvena + cHarina + cCarbonato + cVitaminas
                    + cEmpaque + cEE + cVapor + cMO;
            double costoUnitQ   = costoTotal / totalUnidades;
            double costoUnitUSD = costoUnitQ / tipoCambio;

            System.out.println(">>> Producto: " + producto
                    + " | cAvena=" + cAvena
                    + " | cEmpaque=" + cEmpaque
                    + " | costoTotal=" + costoTotal
                    + " | Q/u=" + costoUnitQ);

            DetalleCostoPorProducto detalle = new DetalleCostoPorProducto();
            detalle.setCierre(cierre);
            detalle.setProducto(producto);
            detalle.setPresentacion("");
            detalle.setUnidadesProducidas(totalUnidades);
            detalle.setKgProducidos(totalKg);
            detalle.setCostoAvena(cAvena);
            detalle.setCostoHarina(cHarina);
            detalle.setCostoCarbonato(cCarbonato);
            detalle.setCostoVitaminas(cVitaminas);
            detalle.setCostoEmpaque(cEmpaque);
            detalle.setCostoEE(cEE);
            detalle.setCostoVapor(cVapor);
            detalle.setCostoMO(cMO);
            detalle.setCostoTotalQ(Math.round(costoTotal * 100.0) / 100.0);
            detalle.setCostoUnitarioQ(Math.round(costoUnitQ * 10000.0) / 10000.0);
            detalle.setCostoUnitarioUSD(Math.round(costoUnitUSD * 10000.0) / 10000.0);
            detalleRepository.save(detalle);
        }

        return cierre;
    }

    public CierreMensual confirmar(Long id) {
        CierreMensual cierre = cierreRepository.findById(id).orElseThrow();
        cierre.setEstado(EstadoCierre.CONFIRMADO);
        return cierreRepository.save(cierre);
    }
}