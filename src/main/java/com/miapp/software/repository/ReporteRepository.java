package com.miapp.software.repository;

import com.miapp.software.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReporteRepository extends JpaRepository<Reporte, Integer> {
    List<Reporte> findByPersonal_IdPersonal(Integer idPersonal);
}