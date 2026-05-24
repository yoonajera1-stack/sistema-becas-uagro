package com.miapp.software.service;

import com.miapp.software.model.Personal;
import com.miapp.software.model.Rol;
import com.miapp.software.repository.PersonalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PersonalService {

    @Autowired
    private PersonalRepository personalRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ─────────────────────────────────────────────────
    // C.U.1 — Autenticación (RF-26)
    // ✅ CORREGIDO: ahora soporta tanto texto plano (datos de prueba)
    //    como BCrypt (usuarios creados desde el sistema)
    // ─────────────────────────────────────────────────
    public Optional<Personal> autenticar(String correo, String contraseña) {
        Optional<Personal> personal = personalRepository.findByCorreo(correo);

        if (personal.isPresent() && personal.get().getEstatus()) {
            String hash = personal.get().getContraseña();

            // Intentar con BCrypt primero (usuarios creados desde el sistema)
            boolean valida = false;
            try {
                valida = passwordEncoder.matches(contraseña, hash);
            } catch (Exception ignored) {}

            // Si no coincide con BCrypt, comparar en texto plano (datos de prueba del SQL)
            if (!valida) {
                valida = hash.equals(contraseña);
            }

            if (valida) return personal;
        }
        return Optional.empty();
    }

    // ─────────────────────────────────────────────────
    // MO-06 — Guardar nuevo usuario (RF-25)
    // FA_001 del C.U.6: correo duplicado
    // ─────────────────────────────────────────────────
    public Personal guardar(Personal personal) {
        if (personalRepository.existsByCorreo(personal.getCorreo())) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario registrado con ese correo institucional."
            );
        }
        // ✅ Encriptar contraseña con BCrypt antes de guardar
        if (personal.getContraseña() != null && !personal.getContraseña().isBlank()) {
            personal.setContraseña(passwordEncoder.encode(personal.getContraseña()));
        } else {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }
        return personalRepository.save(personal);
    }

    // ─────────────────────────────────────────────────
    // MO-06 — Actualizar usuario existente (RF-27)
    // ✅ CORREGIDO: antes solo actualizaba departamento y estatus.
    //    Ahora actualiza todos los campos relevantes.
    // ─────────────────────────────────────────────────
    public Personal actualizarPermisos(Integer id, Personal actualizado) {
        return personalRepository.findById(id).map(personal -> {

            // Datos generales
            personal.setNombre(actualizado.getNombre());
            personal.setCorreo(actualizado.getCorreo());
            personal.setTelefono(actualizado.getTelefono());
            personal.setDepartamento(actualizado.getDepartamento());

            // Rol
            if (actualizado.getRol() != null) {
                personal.setRol(actualizado.getRol());
            }

            // Estatus (RF-27: habilitar / deshabilitar)
            personal.setEstatus(actualizado.getEstatus());

            // Contraseña: solo se actualiza si se ingresó una nueva
            if (actualizado.getContraseña() != null && !actualizado.getContraseña().isBlank()) {
                personal.setContraseña(passwordEncoder.encode(actualizado.getContraseña()));
            }

            return personalRepository.save(personal);
        }).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));
    }

    // ─────────────────────────────────────────────────
    // MO-06 — Deshabilitar usuario (RF-27)
    // FA_002 del C.U.6: no se puede deshabilitar el propio usuario
    // ─────────────────────────────────────────────────
    public void deshabilitar(Integer id, Integer idUsuarioActual) {
        if (id.equals(idUsuarioActual)) {
            throw new IllegalArgumentException(
                    "No puedes deshabilitar tu propio usuario."
            );
        }
        personalRepository.findById(id).ifPresent(personal -> {
            personal.setEstatus(false);
            personalRepository.save(personal);
        });
    }

    // Sobrecarga sin validación de autodeshabilitación (compatibilidad)
    public void deshabilitar(Integer id) {
        personalRepository.findById(id).ifPresent(personal -> {
            personal.setEstatus(false);
            personalRepository.save(personal);
        });
    }

    // ─────────────────────────────────────────────────
    // Consultas
    // ─────────────────────────────────────────────────
    public List<Personal> listarTodos() {
        return personalRepository.findAll();
    }

    public Optional<Personal> buscarPorId(Integer id) {
        return personalRepository.findById(id);
    }
}