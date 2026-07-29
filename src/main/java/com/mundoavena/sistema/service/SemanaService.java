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

        // 2. Crear la semana nueva (arranca el día siguiente a como terminó la anterior)
        Semana semanaNueva = new Semana();
        semanaNueva.setFechaInicio(semanaActual.getFechaFin().plusDays(1));
        semanaNueva.setFechaFin(semanaActual.getFechaFin().plusDays(7));
        semanaNueva.setCerrada(false);
        semanaRepository.save(semanaNueva);

        // 3. Arrastrar saldos: por cada lote de la semana que cierra con saldo > 0,
        //    crear su equivalente en la semana nueva con cantidadInicial = saldoActual del anterior
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
    public MovimientoLote registrarMovimiento(LoteSemana lote, MovimientoLote.TipoMovimiento tipo,
                                              double cantidad, LocalDate fecha,
                                              String ticketBascula, String registradoPor) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }

        if (tipo == MovimientoLote.TipoMovimiento.SALIDA && cantidad > lote.getSaldoActual()) {
            throw new IllegalStateException(
                    "No se puede registrar una salida de " + cantidad + " " +
                            lote.getProducto().getUnidadMedida() + ": el lote solo tiene " +
                            lote.getSaldoActual() + " disponibles."
            );
        }

        // 1. Actualizar el saldo del lote
        double nuevoSaldo = tipo == MovimientoLote.TipoMovimiento.ENTRADA
                ? lote.getSaldoActual() + cantidad
                : lote.getSaldoActual() - cantidad;
        lote.setSaldoActual(nuevoSaldo);

        // 2. Actualizar el estado según el saldo resultante
        if (nuevoSaldo <= 0) {
            lote.setEstado(LoteSemana.EstadoLote.AGOTADO);
        } else if (lote.getCantidadInicial() > 0 && nuevoSaldo < lote.getCantidadInicial() * 0.15) {
            lote.setEstado(LoteSemana.EstadoLote.SALDO_BAJO);
        } else {
            lote.setEstado(LoteSemana.EstadoLote.ACTIVO);
        }
        loteSemanaRepository.save(lote);

        // 3. Registrar el movimiento
        MovimientoLote movimiento = new MovimientoLote();
        movimiento.setLoteSemana(lote);
        movimiento.setFecha(fecha);
        movimiento.setTipo(tipo);
        movimiento.setCantidad(cantidad);
        movimiento.setTicketBascula(ticketBascula);
        movimiento.setRegistradoPor(registradoPor);

        return movimientoLoteRepository.save(movimiento);
    }
}