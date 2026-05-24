package com.miapp.software.service;


import com.miapp.software.model.Requisito;
import com.miapp.software.repository.RequisitoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RequisitoService {

    @Autowired
    private RequisitoRepository requisitoRepository;

    // RF-02 - Listar requisitos de una beca específica
    public List<Requisito> listarPorBeca(Integer idBeca) {
        return requisitoRepository.findByBeca_IdBeca(idBeca);
    }

    // RF-02 - Listar requisitos obligatorios de una beca
    public List<Requisito> listarObligatoriosPorBeca(Integer idBeca) {
        return requisitoRepository.findByBeca_IdBecaAndTipo(idBeca, "Obligatorio");
    }

    // RF-02 - Listar requisitos opcionales de una beca
    public List<Requisito> listarOpcionalesPorBeca(Integer idBeca) {
        return requisitoRepository.findByBeca_IdBecaAndTipo(idBeca, "Opcional");
    }

    // RF-02 - Listar todos los requisitos activos
    public List<Requisito> listarActivos() {
        return requisitoRepository.findByEstatusTrue();
    }

    // RF-02 - Buscar requisito por ID
    public Optional<Requisito> buscarPorId(Integer id) {
        return requisitoRepository.findById(id);
    }

    // MO-01 - Registrar nuevo requisito para una convocatoria
    public Requisito registrar(Requisito requisito) {
        // RNF-01: Validar campos obligatorios
        if (requisito.getNombre() == null || requisito.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre del requisito es obligatorio");
        }
        if (requisito.getBeca() == null || requisito.getBeca().getIdBeca() == null) {
            throw new IllegalArgumentException("La beca asociada es obligatoria");
        }
        if (requisito.getTipo() == null || requisito.getTipo().isEmpty()) {
            throw new IllegalArgumentException("El tipo de requisito (Obligatorio/Opcional) es obligatorio");
        }
        return requisitoRepository.save(requisito);
    }

    // MO-01 - Actualizar requisito
    public Requisito actualizar(Integer id, Requisito requisitoActualizado) {
        return requisitoRepository.findById(id).map(requisito -> {
            requisito.setNombre(requisitoActualizado.getNombre());
            requisito.setTipo(requisitoActualizado.getTipo());
            requisito.setDescripcion(requisitoActualizado.getDescripcion());
            return requisitoRepository.save(requisito);
        }).orElse(null);
    }

    // MO-01 - Eliminar requisito
    public void eliminar(Integer id) {
        requisitoRepository.deleteById(id);
    }

    // MO-01 - Desactivar requisito
    public void desactivar(Integer id) {
        requisitoRepository.findById(id).ifPresent(requisito -> {
            requisito.setEstatus(false);
            requisitoRepository.save(requisito);
        });
    }

    // RNF-06 - Búsqueda rápida de requisitos
    public List<Requisito> buscarPorNombre(String nombre) {
        long inicio = System.currentTimeMillis();
        List<Requisito> resultados = requisitoRepository.findByNombreContainingIgnoreCase(nombre);
        long fin = System.currentTimeMillis();
        System.out.println("Tiempo de búsqueda de requisitos: " + (fin - inicio) + "ms");
        return resultados;
    }
}