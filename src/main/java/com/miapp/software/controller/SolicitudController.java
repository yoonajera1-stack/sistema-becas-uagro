package com.miapp.software.controller;

import com.miapp.software.model.Personal;
import com.miapp.software.model.Solicitud;
import com.miapp.software.model.Estudiante;
import com.miapp.software.model.Beca;
import com.miapp.software.service.SolicitudService;
import com.miapp.software.service.EstudianteService;
import com.miapp.software.service.BecaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/solicitudes")
public class SolicitudController {

    @Autowired
    private SolicitudService solicitudService;

    @Autowired
    private EstudianteService estudianteService;

    @Autowired
    private BecaService becaService;

    // ------------------------------------
    // LISTA - con filtro por pestaña
    // ------------------------------------
    @GetMapping
    public String listarSolicitudes(
            @RequestParam(defaultValue = "activas") String vista,
            HttpSession session,
            Model model) {

        Personal usuario = (Personal) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        // Cargar solicitudes según la pestaña seleccionada
        switch (vista) {
            case "pendientes"  -> model.addAttribute("solicitudes",
                    solicitudService.listarPorEstado(SolicitudService.ESTADO_PENDIENTE));
            case "completas"   -> model.addAttribute("solicitudes",
                    solicitudService.listarPorEstado(SolicitudService.ESTADO_DOC_COMPLETA));
            case "enviadas"    -> model.addAttribute("solicitudes",
                    solicitudService.listarEnviadas());
            case "finalizadas" -> model.addAttribute("solicitudes",
                    solicitudService.listarFinalizadas());
            default            -> model.addAttribute("solicitudes",
                    solicitudService.listarTodos());
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("vistaActual", vista);
        return "solicitudes/lista";
    }

    // ------------------------------------
    // NUEVA SOLICITUD
    // ------------------------------------
    @GetMapping("/nueva")
    public String mostrarFormularioNueva(HttpSession session, Model model) {
        Personal usuario = (Personal) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        model.addAttribute("usuario", usuario);
        model.addAttribute("estudiantes", estudianteService.listarTodos());
        model.addAttribute("becas", becaService.listarActivas());
        model.addAttribute("solicitud", new Solicitud());
        return "solicitudes/formulario";
    }

    @PostMapping("/guardar")
    public String guardarSolicitud(
            @ModelAttribute Solicitud solicitud,
            @RequestParam Integer idEstudiante,
            @RequestParam Integer idBeca,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            Estudiante estudiante = new Estudiante();
            estudiante.setIdEstudiante(idEstudiante);
            solicitud.setEstudiante(estudiante);

            Beca beca = new Beca();
            beca.setIdBeca(idBeca);
            solicitud.setBeca(beca);

            solicitud.setPersonalRegistro((Personal) session.getAttribute("usuario"));
            if (solicitud.getPeriodo() == null || solicitud.getPeriodo().isBlank()) {
                solicitud.setPeriodo("2026-A");
            }

            solicitudService.registrar(solicitud);
            redirectAttributes.addFlashAttribute("mensaje",
                    "✅ Solicitud registrada correctamente. El folio fue generado.");
            return "redirect:/solicitudes";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
            return "redirect:/solicitudes/nueva";
        }
    }

    // ------------------------------------
    // AVANZAR ESTADO (C.U.4)
    // El personal valida y avanza el flujo.
    // La UAGRO NO aprueba ni rechaza — solo la instancia externa lo hace.
    // ------------------------------------
    @PostMapping("/avanzar-estado/{id}")
    public String avanzarEstado(
            @PathVariable Integer id,
            @RequestParam String nuevoEstado,
            @RequestParam(required = false) String observaciones,
            @RequestParam(required = false) String respuestaInstancia,
            @RequestParam(required = false) String vistaActual,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            Personal usuario = (Personal) session.getAttribute("usuario");

            // Si viene respuesta de la instancia externa, registrarla
            solicitudService.buscarPorId(id).ifPresent(sol -> {
                if (respuestaInstancia != null && !respuestaInstancia.isBlank()) {
                    sol.setRespuestaInstancia(respuestaInstancia);
                }
            });

            solicitudService.avanzarEstado(id, nuevoEstado, observaciones, usuario);

            String etiqueta = switch (nuevoEstado) {
                case "Documentación Completa" -> "✅ Documentación marcada como completa.";
                case "Enviada a Instancia"    -> "📤 Solicitud enviada a la instancia externa.";
                case "Aprobada"               -> "🎉 Beca registrada como Aprobada por la instancia.";
                case "Rechazada"              -> "❌ Solicitud registrada como Rechazada por la instancia.";
                default                       -> "Estado actualizado.";
            };

            redirectAttributes.addFlashAttribute("mensaje", etiqueta);

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        String tab = (vistaActual != null) ? vistaActual : "all";
        return "redirect:/solicitudes?vista=" + tab;
    }

    // ------------------------------------
    // VER DETALLE de una solicitud
    // ------------------------------------
    @GetMapping("/detalle/{id}")
    public String verDetalle(
            @PathVariable Integer id,
            HttpSession session,
            Model model) {

        Personal usuario = (Personal) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        Solicitud solicitud = solicitudService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        model.addAttribute("usuario", usuario);
        model.addAttribute("solicitud", solicitud);
        model.addAttribute("siguienteEstado",
                solicitudService.siguienteEstado(solicitud.getEstado()));
        return "solicitudes/detalle";
    }
}