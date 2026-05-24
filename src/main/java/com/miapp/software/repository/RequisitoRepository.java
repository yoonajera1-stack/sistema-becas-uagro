package com.miapp.software.repository;

import com.miapp.software.model.Requisito;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RequisitoRepository extends JpaRepository<Requisito, Integer> {
    List<Requisito> findByBeca_IdBeca(Integer idBeca);
    List<Requisito> findByBeca_IdBecaAndTipo(Integer idBeca, String tipo);
    List<Requisito> findByEstatusTrue();
    List<Requisito> findByNombreContainingIgnoreCase(String nombre);
}