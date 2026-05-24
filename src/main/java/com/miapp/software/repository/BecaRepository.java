package com.miapp.software.repository;

import com.miapp.software.model.Beca;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BecaRepository extends JpaRepository<Beca, Integer> {
    // Buscar becas activas (RF-04)
    List<Beca> findByEstatusTrue();

    // Buscar por nombre (RF-04)
    List<Beca> findByNombreContainingIgnoreCase(String nombre);
}