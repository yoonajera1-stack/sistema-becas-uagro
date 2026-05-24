package com.miapp.software.repository;

import com.miapp.software.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Integer> {
    List<Rol> findByEstatusTrue();
    Optional<Rol> findByNombreRol(String nombreRol);
}