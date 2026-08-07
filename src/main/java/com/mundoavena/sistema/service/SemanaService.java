package com.mundoavena.sistema.service;

import com.mundoavena.sistema.model.*;
import com.mundoavena.sistema.repository.LoteSemanaRepository;
import com.mundoavena.sistema.repository.MovimientoLoteRepository;
import com.mundoavena.sistema.repository.SemanaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class SemanaService {

    @Autowired
    private SemanaRepository semanaRepository;

    @Autowired
    private LoteSemanaRepository loteSemanaRepository;

    @Autowired
    private MovimientoLoteRepository movimientoLoteRepository;

    /**
     * Devuelve la semana activa (la última que no está cerrada).
     * Si no existe ninguna (primera vez que arranca el sistema), crea la primera automáticamente.
     */
    public Semana obtenerSemanaActual() {
        return semanaRepository.findFirstByCerradaFalseOrderByFechaInicioDesc()
                .orElseGet(this::crearPrimeraSemana);
    }

    private Semana crearPrimeraSemana() {
        Semana semana = new Semana();
        semana.setFechaInicio(LocalDate.now());
        semana.setFechaFin(LocalDate.now().plusDays(6));
        semana.setCerrada(false);
        return semanaRepository.save(semana);
    }

    /**
     * Cierra la semana actual y crea la siguiente, arrastrando como cantidadInicial
     * el saldoActual de cada lote de TODAS las bodegas que no haya quedado en cero.
     */
    @Transactional
    public Semana crearNuevaSemana() {
        Semana semanaActual = obtenerSemanaActual();

        // 1. Cerrar la semana actual
        semanaActual.setCerrada(true);
        semanaRepository.save(semanaActual);

        // 2. Crear la semana nueva
        Semana semanaNueva = new Semana();
        semanaNueva.setFechaInicio(semanaActual.getFechaFin().plusDays(1));
        semanaNueva.setFechaFin(semanaActual.getFechaFin().plusDays(7));
        semanaNueva.setCerrada(false);
        semanaRepository.save(semanaNueva);

        // 3. Arrastrar saldos
        List<LoteSemana> lotesConSaldo = loteSemanaRepository.findAll().stream()
                .filter(l -> l.getSemana().getId().equals(semanaActual.getId()))
                .filter(l -> l.getSaldoActual() > 0)
                .toList();

        for (LoteSemana loteAnterior : lotesConSaldo) {
            LoteSemana loteNuevo = new LoteSemana();
            loteNuevo.setSemana(semanaNueva);
            loteNuevo.setProducto(loteAnterior.getProducto());
            loteNuevo.setNumeroLote(loteAnterior.getNumeroLote());
            loteNuevo.setUbicacion(loteAnterior.getUbicacion());
            loteNuevo.setCantidadInicial(loteAnterior.getSaldoActual());
            loteNuevo.setSaldoActual(loteAnterior.getSaldoActual());
            loteNuevo.setPesoInicialKg(loteAnterior.getPesoActualKg());
            loteNuevo.setPesoActualKg(loteAnterior.getPesoActualKg());
            loteNuevo.setEstado(LoteSemana.EstadoLote.ACTIVO);

            loteSemanaRepository.save(loteNuevo);
        }

        return semanaNueva;
    }

    /**
     * Registra un movimiento (entrada o salida) sobre un lote existente.
     * Valida que una salida no deje el saldo en negativo, y actualiza el estado del lote.
     */
    @Transactional
    public MovimientoLote registrarMovimiento(LoteSemana lote,
                                              MovimientoLote.TipoMovimiento tipo,
                                              double cantidad,
                                              Double pesoKg,
                                              LocalDate fecha,
                                              String ticketBascula,
                                              String registradoPor) {

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }

        if (tipo == MovimientoLote.TipoMovimiento.SALIDA
                && cantidad > lote.getSaldoActual()) {

            throw new IllegalStateException(
                    "No se puede registrar una salida de " + cantidad + " "
                            + lote.getProducto().getUnidadMedida()
                            + ": el lote solo tiene "
                            + lote.getSaldoActual() + " disponibles."
            );
        }

        if (pesoKg != null
                && tipo == MovimientoLote.TipoMovimiento.SALIDA
                && lote.getPesoActualKg() != null
                && pesoKg > lote.getPesoActualKg()) {

            throw new IllegalStateException(
                    "No se puede registrar una salida de "
                            + pesoKg
                            + " kg: el lote solo tiene "
                            + lote.getPesoActualKg()
                            + " kg disponibles."
            );
        }

        // Actualizar saldo
        double nuevoSaldo = tipo == MovimientoLote.TipoMovimiento.ENTRADA
                ? lote.getSaldoActual() + cantidad
                : lote.getSaldoActual() - cantidad;

        lote.setSaldoActual(nuevoSaldo);

        // Actualizar peso
        if (pesoKg != null) {
            double pesoBase = lote.getPesoActualKg() != null
                    ? lote.getPesoActualKg()
                    : 0;

            double nuevoPeso = tipo == MovimientoLote.TipoMovimiento.ENTRADA
                    ? pesoBase + pesoKg
                    : pesoBase - pesoKg;

            lote.setPesoActualKg(nuevoPeso);
        }

        // Estado
        if (nuevoSaldo <= 0) {
            lote.setEstado(LoteSemana.EstadoLote.AGOTADO);
        } else {
            lote.setEstado(LoteSemana.EstadoLote.ACTIVO);
        }

        loteSemanaRepository.save(lote);

        // Movimiento
        MovimientoLote movimiento = new MovimientoLote();
        movimiento.setLoteSemana(lote);
        movimiento.setFecha(fecha);
        movimiento.setTipo(tipo);
        movimiento.setCantidad(cantidad);
        movimiento.setPesoKg(pesoKg);
        movimiento.setTicketBascula(ticketBascula);
        movimiento.setRegistradoPor(registradoPor);

        return movimientoLoteRepository.save(movimiento);
    }

    /**
     * Registra un ajuste de inventario: fuerza el saldo del lote a un valor exacto
     * (normalmente tras un conteo físico), sin importar si eso implica subir o bajar
     * respecto al saldo actual. Queda registrado como un MovimientoLote tipo AJUSTE,
     * con la diferencia real como cantidad, para trazabilidad.
     */
    @Transactional
    public MovimientoLote registrarAjuste(LoteSemana lote,
                                          double nuevoSaldo,
                                          Double nuevoPesoKg,
                                          LocalDate fecha,
                                          String motivo,
                                          String registradoPor) {

        if (nuevoSaldo < 0) {
            throw new IllegalArgumentException("El nuevo saldo no puede ser negativo.");
        }

        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Todo ajuste necesita un motivo.");
        }

        double diferencia = nuevoSaldo - lote.getSaldoActual();

        lote.setSaldoActual(nuevoSaldo);

        if (nuevoPesoKg != null) {
            lote.setPesoActualKg(nuevoPesoKg);
        }

        if (nuevoSaldo <= 0) {
            lote.setEstado(LoteSemana.EstadoLote.AGOTADO);
        } else if (lote.getCantidadInicial() > 0
                && nuevoSaldo < lote.getCantidadInicial() * 0.15) {

            lote.setEstado(LoteSemana.EstadoLote.SALDO_BAJO);

        } else {

            lote.setEstado(LoteSemana.EstadoLote.ACTIVO);

        }

        loteSemanaRepository.save(lote);

        MovimientoLote movimiento = new MovimientoLote();
        movimiento.setLoteSemana(lote);
        movimiento.setFecha(fecha);
        movimiento.setTipo(MovimientoLote.TipoMovimiento.AJUSTE);
        movimiento.setCantidad(Math.abs(diferencia));
        movimiento.setPesoKg(nuevoPesoKg);
        movimiento.setMotivo(motivo);
        movimiento.setRegistradoPor(registradoPor);

        return movimientoLoteRepository.save(movimiento);
    }

}