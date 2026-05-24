package com.miapp.software.controller;

import com.miapp.software.model.Personal;
import com.miapp.software.model.Estudiante;
import com.miapp.software.model.Escuela;
import com.miapp.software.service.EstudianteService;
import com.miapp.software.service.EscuelaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/estudiantes")
public class EstudianteController {

    @Autowired
    private EstudianteService estudianteService;

    @Autowired
    private EscuelaService escuelaService;

    // ─────────────────────────────────────────────────
    // LISTA — filtro por escuela y búsqueda por nombre
    // ─────────────────────────────────────────────────
    @GetMapping
    public String listarEstudiantes(
            @RequestParam(required = false) Integer idEscuela,
            @RequestParam(required = false) String buscar,
            HttpSession session,
            Model model) {

        Personal usuario = (Personal) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        List<Estudiante> estudiantes = (idEscuela != null)
                ? estudianteService.listarPorEscuela(idEscuela)
                : estudianteService.listarTodos();

        // Filtro por nombre/matrícula/correo
        if (buscar != null && !buscar.isBlank()) {
            String termino = buscar.toLowerCase();
            estudiantes = estudiantes.stream()
                    .filter(e -> e.getNombre().toLowerCase().contains(termino)
                            || e.getMatricula().toLowerCase().contains(termino)
                            || e.getCorreo().toLowerCase().contains(termino))
                    .toList();
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("estudiantes", estudiantes);
        model.addAttribute("escuelas", escuelaService.listarActivas());
        model.addAttribute("idEscuelaSeleccionada", idEscuela);
        model.addAttribute("buscar", buscar);
        model.addAttribute("totalMostrados", estudiantes.size());
        return "estudiantes/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(HttpSession session, Model model) {
        Personal usuario = (Personal) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        model.addAttribute("usuario", usuario);
        model.addAttribute("escuelas", escuelaService.listarActivas());
        model.addAttribute("estudiante", new Estudiante());
        model.addAttribute("modo", "crear");
        return "estudiantes/formulario";
    }

    @PostMapping("/guardar")
    public String guardarEstudiante(
            @ModelAttribute Estudiante estudiante,
            @RequestParam(required = false) Integer idEscuela,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            if (idEscuela != null) {
                Escuela escuela = new Escuela();
                escuela.setIdEscuela(idEscuela);
                estudiante.setEscuela(escuela);
            }
            if (estudiante.getEscuela() == null || estudiante.getEscuela().getIdEscuela() == null) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar una escuela.");
                return "redirect:/estudiantes/nuevo";
            }
            estudianteService.registrar(estudiante);
            redirectAttributes.addFlashAttribute("mensaje", "✅ Estudiante registrado correctamente.");
            return "redirect:/estudiantes";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/estudiantes/nuevo";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
            return "redirect:/estudiantes/nuevo";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarEditar(@PathVariable Integer id, HttpSession session, Model model) {
        Personal usuario = (Personal) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        Estudiante estudiante = estudianteService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado"));

        model.addAttribute("usuario", usuario);
        model.addAttribute("escuelas", escuelaService.listarActivas());
        model.addAttribute("estudiante", estudiante);
        model.addAttribute("modo", "editar");
        return "estudiantes/formulario";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarEstudiante(
            @PathVariable Integer id,
            @ModelAttribute Estudiante estudianteActualizado,
            @RequestParam(required = false) Integer idEscuela,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            if (idEscuela != null) {
                Escuela escuela = new Escuela();
                escuela.setIdEscuela(idEscuela);
                estudianteActualizado.setEscuela(escuela);
            }
            estudianteService.actualizar(id, estudianteActualizado);
            redirectAttributes.addFlashAttribute("mensaje", "✅ Estudiante actualizado correctamente.");
            return "redirect:/estudiantes";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/estudiantes/editar/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar: " + e.getMessage());
            return "redirect:/estudiantes/editar/" + id;
        }
    }
}