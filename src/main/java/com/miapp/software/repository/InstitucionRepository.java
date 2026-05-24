package com.miapp.software.repository;

import com.miapp.software.model.Institucion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InstitucionRepository extends JpaRepository<Institucion, Integer> {
    List<Institucion> findByEstatusTrue();  // ✅ ESTE MÉTODO ES OBLIGATORIO
}