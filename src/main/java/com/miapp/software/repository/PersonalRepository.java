package com.miapp.software.repository;

import com.miapp.software.model.Personal;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PersonalRepository extends JpaRepository<Personal, Integer> {

    // ✅ CORREGIDO: @EntityGraph fuerza que el Rol se cargue junto con Personal
    // Sin esto, el Rol queda "lazy" y truena en el panel de admin al listar usuarios
    @EntityGraph(attributePaths = {"rol"})
    Optional<Personal> findByCorreo(String correo);

    // ✅ CORREGIDO: también en findAll para que el panel admin no truene
    @EntityGraph(attributePaths = {"rol"})
    List<Personal> findAll();

    // ✅ CORREGIDO: también en findById para editar usuario
    @EntityGraph(attributePaths = {"rol"})
    Optional<Personal> findById(Integer id);

    boolean existsByCorreo(String correo);
}