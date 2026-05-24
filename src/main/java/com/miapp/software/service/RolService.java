package com.miapp.software.service;

import com.miapp.software.model.Rol;
import com.miapp.software.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    // RF-25 - Listar todos los roles (para formulario de usuarios)
    public List<Rol> listarTodos() {
        return rolRepository.findAll();
    }

    // RF-25 - Listar roles activos
    public List<Rol> listarActivos() {
        return rolRepository.findByEstatusTrue();
    }

    // RF-25 - Buscar rol por ID
    public Optional<Rol> buscarPorId(Integer id) {
        return rolRepository.findById(id);
    }

    // RF-25 - Buscar rol por nombre (Administrador, Coordinador, etc.)
    public Optional<Rol> buscarPorNombre(String nombreRol) {
        return rolRepository.findByNombreRol(nombreRol);
    }

    // MO-06 - Registrar nuevo rol
    public Rol registrar(Rol rol) {
        // RNF-01: Validar campos obligatorios
        if (rol.getNombreRol() == null || rol.getNombreRol().isEmpty()) {
            throw new IllegalArgumentException("El nombre del rol es obligatorio");
        }

        // Validar que no exista un rol con el mismo nombre
        if (rolRepository.findByNombreRol(rol.getNombreRol()).isPresent()) {
            throw new IllegalArgumentException("El rol ya existe en el sistema");
        }

        return rolRepository.save(rol);
    }

    // MO-06 - Actualizar rol
    public Rol actualizar(Integer id, Rol rolActualizado) {
        return rolRepository.findById(id).map(rol -> {
            rol.setNombreRol(rolActualizado.getNombreRol());
            rol.setDescripcion(rolActualizado.getDescripcion());
            rol.setPermisos(rolActualizado.getPermisos());
            return rolRepository.save(rol);
        }).orElse(null);
    }

    // MO-06 - Desactivar rol
    public void desactivar(Integer id) {
        rolRepository.findById(id).ifPresent(rol -> {
            rol.setEstatus(false);
            rolRepository.save(rol);
        });
    }
}