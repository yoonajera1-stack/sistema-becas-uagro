package com.miapp.software.service;

import com.miapp.software.model.Estudiante;
import com.miapp.software.model.Solicitud;
import com.miapp.software.repository.EstudianteRepository;
import com.miapp.software.repository.BecaRepository;
import com.miapp.software.repository.SolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private BecaRepository becaRepository;

    // ─────────────────────────────────────────────────
    // Método auxiliar: ordena un Map<String,Long> de mayor a menor
    // ✅ CORRECCIÓN: reemplaza toLinkedHashMap (no existe en Java)
    //    por el collector correcto con LinkedHashMap::new
    // ─────────────────────────────────────────────────
    private Map<String, Long> ordenarDescendente(Map<String, Long> mapa) {
        return mapa.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    // ─────────────────────────────────────────────────
    // TOTALES GENERALES
    // ─────────────────────────────────────────────────
    public long totalEstudiantes() {
        return estudianteRepository.findByEstatusTrue().size();
    }

    public long totalSolicitudes() {
        return solicitudRepository.count();
    }

    public long totalPorEstado(String estado) {
        return solicitudRepository.findByEstado(estado).size();
    }

    public long totalBecasActivas() {
        return becaRepository.findByEstatusTrue().size();
    }

    // ─────────────────────────────────────────────────
    // SOLICITUDES POR ESCUELA
    // ─────────────────────────────────────────────────
    public Map<String, Long> solicitudesPorEscuela() {
        Map<String, Long> mapa = solicitudRepository.findAll().stream()
                .filter(s -> s.getEstudiante() != null
                        && s.getEstudiante().getEscuela() != null)
                .collect(Collectors.groupingBy(
                        s -> s.getEstudiante().getEscuela().getNombre(),
                        Collectors.counting()
                ));
        return ordenarDescendente(mapa);
    }

    // ─────────────────────────────────────────────────
    // ESTUDIANTES POR GÉNERO
    // ─────────────────────────────────────────────────
    public Map<String, Long> estudiantesPorGenero() {
        Map<String, Long> mapa = estudianteRepository.findByEstatusTrue().stream()
                .filter(e -> e.getGenero() != null)
                .collect(Collectors.groupingBy(
                        Estudiante::getGenero,
                        Collectors.counting()
                ));
        return ordenarDescendente(mapa);
    }

    // ─────────────────────────────────────────────────
    // SOLICITUDES POR NIVEL EDUCATIVO
    // ─────────────────────────────────────────────────
    public Map<String, Long> solicitudesPorNivel() {
        Map<String, Long> mapa = solicitudRepository.findAll().stream()
                .filter(s -> s.getEstudiante() != null
                        && s.getEstudiante().getNivelEducativo() != null)
                .collect(Collectors.groupingBy(
                        s -> s.getEstudiante().getNivelEducativo(),
                        Collectors.counting()
                ));
        return ordenarDescendente(mapa);
    }

    // ─────────────────────────────────────────────────
    // SOLICITUDES POR INSTANCIA QUE OTORGA LA BECA
    // ─────────────────────────────────────────────────
    public Map<String, Long> solicitudesPorInstitucion() {
        Map<String, Long> mapa = solicitudRepository.findAll().stream()
                .filter(s -> s.getBeca() != null
                        && s.getBeca().getInstitucion() != null)
                .collect(Collectors.groupingBy(
                        s -> s.getBeca().getInstitucion().getNombre(),
                        Collectors.counting()
                ));
        return ordenarDescendente(mapa);
    }

    // ─────────────────────────────────────────────────
    // POBLACIÓN VULNERABLE
    // ─────────────────────────────────────────────────
    public long totalVulnerables() {
        return estudianteRepository.findByEstatusTrue().stream()
                .filter(e -> Boolean.TRUE.equals(e.getVulnerabilidad()))
                .count();
    }

    public Map<String, Long> porTipoVulnerabilidad() {
        Map<String, Long> mapa = estudianteRepository.findByEstatusTrue().stream()
                .filter(e -> Boolean.TRUE.equals(e.getVulnerabilidad())
                        && e.getTipoVulnerabilidad() != null)
                .collect(Collectors.groupingBy(
                        Estudiante::getTipoVulnerabilidad,
                        Collectors.counting()
                ));
        return ordenarDescendente(mapa);
    }

    // ─────────────────────────────────────────────────
    // BECAS APROBADAS POR ESCUELA
    // ─────────────────────────────────────────────────
    public Map<String, Long> aprobadasPorEscuela() {
        Map<String, Long> mapa = solicitudRepository.findByEstado("Aprobada").stream()
                .filter(s -> s.getEstudiante() != null
                        && s.getEstudiante().getEscuela() != null)
                .collect(Collectors.groupingBy(
                        s -> s.getEstudiante().getEscuela().getNombre(),
                        Collectors.counting()
                ));
        return ordenarDescendente(mapa);
    }
}