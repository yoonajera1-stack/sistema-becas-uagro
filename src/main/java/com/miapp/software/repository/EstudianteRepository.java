package com.miapp.software.repository;

import com.miapp.software.model.Estudiante;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EstudianteRepository extends JpaRepository<Estudiante, Integer> {

    Optional<Estudiante> findByCurp(String curp);
    Optional<Estudiante> findByMatricula(String matricula);

    @EntityGraph(attributePaths = {"escuela"})
    List<Estudiante> findByEstatusTrue();

    // ✅ NUEVO: filtrar por escuela
    @EntityGraph(attributePaths = {"escuela"})
    List<Estudiante> findByEstatusTrueAndEscuela_IdEscuela(Integer idEscuela);
}