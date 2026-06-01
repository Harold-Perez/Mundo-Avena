package com.mundoavena.sistema.service;

import com.mundoavena.sistema.model.AuditoriaRegistro;
import com.mundoavena.sistema.model.Usuario;
import com.mundoavena.sistema.repository.AuditoriaRegistroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AuditoriaService {

    @Autowired
    private AuditoriaRegistroRepository repository;

    public void registrar(Usuario usuario, String accion,
                          String tipoFormulario, Long registroId,
                          String detalle, String ip) {
        AuditoriaRegistro auditoria = new AuditoriaRegistro();
        auditoria.setUsuario(usuario);
        auditoria.setAccion(accion);
        auditoria.setTipoFormulario(tipoFormulario);
        auditoria.setRegistroId(registroId);
        auditoria.setDetalle(detalle);
        auditoria.setIpAddress(ip);
        repository.save(auditoria);
    }

    public List<AuditoriaRegistro> listarTodos() {
        return repository.findAllByOrderByFechaHoraDesc();
    }

    public List<AuditoriaRegistro> listarPorUsuario(Usuario usuario) {
        return repository.findByUsuario(usuario);
    }

    public List<AuditoriaRegistro> listarPorTipo(String tipo) {
        return repository.findByTipoFormulario(tipo);
    }
}