package com.miapp.software.service;

import com.miapp.software.model.Beca;
import com.miapp.software.repository.BecaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BecaService {

    @Autowired
    private BecaRepository becaRepository;

    // RF-01 (MO-01): Registrar nueva convocatoria de instancia externa
    public Beca registrar(Beca beca) {
        validar(beca);
        return becaRepository.save(beca);
    }

    // RF-03 (MO-01): Editar convocatoria — ✅ incluye todos los campos del documento
    public Beca actualizar(Integer id, Beca becaActualizada) {
        return becaRepository.findById(id).map(beca -> {
            beca.setNombre(becaActualizada.getNombre());
            beca.setTipo(becaActualizada.getTipo());
            beca.setMonto(becaActualizada.getMonto());
            beca.setRequisitos(becaActualizada.getRequisitos());              // RF-02
            beca.setNivelEducativo(becaActualizada.getNivelEducativo());      // RF-01 ✅ CORREGIDO
            beca.setFechaInicio(becaActualizada.getFechaInicio());            // RF-01
            beca.setFechaFin(becaActualizada.getFechaFin());                  // RF-01
            beca.setInstitucion(becaActualizada.getInstitucion());            // RF-01
            return becaRepository.save(beca);
        }).orElseThrow(() -> new IllegalArgumentException("Convocatoria no encontrada: " + id));
    }

    // RF-04 (MO-01): Consultar convocatorias activas
    public List<Beca> listarActivas() {
        return becaRepository.findByEstatusTrue();
    }

    // RF-04 (MO-01): Consultar historial completo (activas e inactivas)
    public List<Beca> listarTodas() {
        return becaRepository.findAll();
    }

    public Optional<Beca> obtenerPorId(Integer id) {
        return becaRepository.findById(id);
    }

    // RF-05 (MO-01): Desactivar convocatoria (borrado lógico)
    public void desactivar(Integer id) {
        becaRepository.findById(id).ifPresent(beca -> {
            beca.setEstatus(false);
            becaRepository.save(beca);
        });
    }

    // RNF-01: Validar campos obligatorios antes de guardar
    private void validar(Beca beca) {
        if (beca.getNombre() == null || beca.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre de la convocatoria es obligatorio.");
        if (beca.getInstitucion() == null || beca.getInstitucion().getIdInstitucion() == null)
            throw new IllegalArgumentException("Debe seleccionar la instancia externa que otorga la beca.");
        if (beca.getTipo() == null || beca.getTipo().isBlank())
            throw new IllegalArgumentException("El tipo de beca es obligatorio.");
        if (beca.getMonto() == null)
            throw new IllegalArgumentException("El monto del apoyo es obligatorio.");
        if (beca.getFechaInicio() == null || beca.getFechaFin() == null)
            throw new IllegalArgumentException("Las fechas de vigencia son obligatorias.");
        if (beca.getFechaFin().isBefore(beca.getFechaInicio()))
            throw new IllegalArgumentException("La fecha de cierre no puede ser anterior a la fecha de inicio.");
        if (beca.getRequisitos() == null || beca.getRequisitos().isBlank())
            throw new IllegalArgumentException("Debe indicar los requisitos de la convocatoria.");
    }

    public List<Beca> buscarPorNombre(String nombre) {
        return becaRepository.findByNombreContainingIgnoreCase(nombre);
    }
}