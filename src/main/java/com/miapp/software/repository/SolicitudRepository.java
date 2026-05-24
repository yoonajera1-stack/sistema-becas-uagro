package com.miapp.software.repository;

import com.miapp.software.model.Solicitud;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SolicitudRepository extends JpaRepository<Solicitud, Integer> {

    // Buscar por estado individual
    @EntityGraph(attributePaths = {"estudiante", "beca", "beca.institucion", "personalRegistro"})
    List<Solicitud> findByEstado(String estado);

    // ✅ NUEVO: buscar por múltiples estados (para vistas filtradas)
    @EntityGraph(attributePaths = {"estudiante", "beca", "beca.institucion", "personalRegistro"})
    List<Solicitud> findByEstadoIn(List<String> estados);

    // ✅ NUEVO: listar todas con relaciones cargadas (evita LazyInitializationException)
    @EntityGraph(attributePaths = {"estudiante", "beca", "beca.institucion", "personalRegistro"})
    List<Solicitud> findAll();
}