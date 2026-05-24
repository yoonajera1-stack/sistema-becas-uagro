package com.miapp.software.service;

import com.miapp.software.model.Escuela;
import com.miapp.software.repository.EscuelaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EscuelaService {

    @Autowired
    private EscuelaRepository escuelaRepository;

    public List<Escuela> listarActivas() {
        return escuelaRepository.findByEstatusTrue();
    }
}