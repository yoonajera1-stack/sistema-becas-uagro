package com.miapp.software.service;


import com.miapp.software.model.Institucion;
import com.miapp.software.repository.InstitucionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class InstitucionService {

    @Autowired
    private InstitucionRepository institucionRepository;

    // ✅ Este es el método que espera BecaController.listarActivas()
    public List<Institucion> listarActivas() {
        return institucionRepository.findByEstatusTrue();
    }

    // Método adicional: listar todas (por si lo necesitas después)
    public List<Institucion> listarTodas() {
        return institucionRepository.findAll();
    }

    // Método adicional: buscar por ID
    public Institucion buscarPorId(Integer id) {
        return institucionRepository.findById(id).orElse(null);
    }
}