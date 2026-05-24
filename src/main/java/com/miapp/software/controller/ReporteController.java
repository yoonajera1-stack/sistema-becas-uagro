package com.miapp.software.controller;

import com.miapp.software.model.Personal;
import com.miapp.software.service.ReporteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    // ─────────────────────────────────────────────────
    // GET /reportes → Mostrar dashboard de estadísticas
    // Muestra TODAS las estadísticas en pantalla de una vez
    // sin necesidad de seleccionar tipo ni descargar archivos
    // ─────────────────────────────────────────────────
    @GetMapping
    public String mostrarReportes(HttpSession session, Model model) {
        Personal usuario = (Personal) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        model.addAttribute("usuario", usuario);

        // ── Totales generales ──
        model.addAttribute("totalEstudiantes",  reporteService.totalEstudiantes());
        model.addAttribute("totalSolicitudes",  reporteService.totalSolicitudes());
        model.addAttribute("totalAprobadas",    reporteService.totalPorEstado("Aprobada"));
        model.addAttribute("totalPendientes",   reporteService.totalPorEstado("Pendiente"));
        model.addAttribute("totalRechazadas",   reporteService.totalPorEstado("Rechazada"));
        model.addAttribute("totalBecasActivas", reporteService.totalBecasActivas());

        // ── Por escuela ──
        model.addAttribute("porEscuela", reporteService.solicitudesPorEscuela());

        // ── Por género ──
        model.addAttribute("porGenero", reporteService.estudiantesPorGenero());

        // ── Por nivel educativo ──
        model.addAttribute("porNivel", reporteService.solicitudesPorNivel());

        // ── Por instancia que otorga ──
        model.addAttribute("porInstitucion", reporteService.solicitudesPorInstitucion());

        // ── Población vulnerable ──
        model.addAttribute("totalVulnerables",    reporteService.totalVulnerables());
        model.addAttribute("porTipoVulnerabilidad", reporteService.porTipoVulnerabilidad());

        // ── Aprobadas por escuela ──
        model.addAttribute("aprobadasPorEscuela", reporteService.aprobadasPorEscuela());

        return "reportes/generar";
    }
}