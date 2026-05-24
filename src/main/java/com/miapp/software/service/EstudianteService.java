package com.miapp.software.service;

import com.miapp.software.model.Estudiante;
import com.miapp.software.repository.EstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class EstudianteService {

    @Autowired
    private EstudianteRepository estudianteRepository;

    // RF-06 (MO-02): Registrar nuevo estudiante
    public Estudiante registrar(Estudiante estudiante) {
        validar(estudiante);

        if (estudianteRepository.findByCurp(estudiante.getCurp()).isPresent()) {
            throw new IllegalArgumentException(
                    "Ya existe un estudiante registrado con esa CURP.");
        }
        if (estudianteRepository.findByMatricula(estudiante.getMatricula()).isPresent()) {
            throw new IllegalArgumentException(
                    "Ya existe un estudiante registrado con esa matrícula.");
        }
        return estudianteRepository.save(estudiante);
    }

    // RF-06 + RF-08 + RF-09: Actualizar todos los campos
    public Estudiante actualizar(Integer id, Estudiante nuevo) {
        return estudianteRepository.findById(id).map(est -> {
            est.setNombre(nuevo.getNombre());
            est.setCorreo(nuevo.getCorreo());
            est.setTelefono(nuevo.getTelefono());
            est.setCurp(nuevo.getCurp());
            est.setFechaNacimiento(nuevo.getFechaNacimiento());
            est.setGenero(nuevo.getGenero());
            est.setEscuela(nuevo.getEscuela());
            est.setMatricula(nuevo.getMatricula());
            est.setNivelEducativo(nuevo.getNivelEducativo());
            est.setSemestre(nuevo.getSemestre());
            est.setPromedio(nuevo.getPromedio());
            est.setOcupacionPadre(nuevo.getOcupacionPadre());
            est.setOcupacionMadre(nuevo.getOcupacionMadre());
            est.setIngresosMensuales(nuevo.getIngresosMensuales());
            est.setSituacionVivienda(nuevo.getSituacionVivienda());
            est.setVulnerabilidad(nuevo.getVulnerabilidad());
            est.setTipoVulnerabilidad(nuevo.getTipoVulnerabilidad());
            return estudianteRepository.save(est);
        }).orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado: " + id));
    }

    // Listar todos los activos
    public List<Estudiante> listarTodos() {
        return estudianteRepository.findByEstatusTrue();
    }

    // ✅ NUEVO: listar por escuela — reemplaza módulo de Director de Escuela
    public List<Estudiante> listarPorEscuela(Integer idEscuela) {
        return estudianteRepository.findByEstatusTrueAndEscuela_IdEscuela(idEscuela);
    }

    public Optional<Estudiante> buscarPorId(Integer id) {
        return estudianteRepository.findById(id);
    }

    public Optional<Estudiante> buscarPorMatricula(String matricula) {
        return estudianteRepository.findByMatricula(matricula);
    }

    public Optional<Estudiante> buscarPorCurp(String curp) {
        return estudianteRepository.findByCurp(curp);
    }

    private void validar(Estudiante e) {
        if (e.getNombre()    == null || e.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre completo es obligatorio.");
        if (e.getCurp()      == null || e.getCurp().isBlank())
            throw new IllegalArgumentException("La CURP es obligatoria.");
        if (e.getMatricula() == null || e.getMatricula().isBlank())
            throw new IllegalArgumentException("La matrícula es obligatoria.");
        if (e.getCorreo()    == null || e.getCorreo().isBlank())
            throw new IllegalArgumentException("El correo electrónico es obligatorio.");
        if (e.getEscuela()   == null || e.getEscuela().getIdEscuela() == null)
            throw new IllegalArgumentException("Debe seleccionar la escuela del estudiante.");
        if (e.getPromedio()  == null)
            throw new IllegalArgumentException("El promedio general es obligatorio.");
        if (e.getFechaNacimiento() == null)
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria.");
    }
}