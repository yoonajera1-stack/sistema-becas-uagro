package com.miapp.software.repository;

import com.miapp.software.model.Escuela;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EscuelaRepository extends JpaRepository<Escuela, Integer> {
    List<Escuela> findByEstatusTrue();
}