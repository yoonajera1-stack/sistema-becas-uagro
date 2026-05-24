package com.miapp.software.repository;

import com.miapp.software.model.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DocumentoRepository extends JpaRepository<Documento, Integer> {

    // Listar todos los archivos subidos para una solicitud específica
    List<Documento> findBySolicitud_IdSolicitud(Integer idSolicitud);

    // Verificar si un requisito específico (ej. Acta de Nacimiento) ya fue subido
    List<Documento> findBySolicitud_IdSolicitudAndRequisito_IdRequisito(Integer idSolicitud, Integer idRequisito);
}