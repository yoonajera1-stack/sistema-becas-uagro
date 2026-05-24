package com.miapp.software.controller;

import com.miapp.software.model.Personal;
import com.miapp.software.model.Beca;
import com.miapp.software.model.Institucion;
import com.miapp.software.service.BecaService;
import com.miapp.software.service.InstitucionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/becas")
public class BecaController {

    @Autowired
    private BecaService becaService;

    @Autowired
    private InstitucionService institucionService;

    @GetMapping
    public String listarBecas(HttpSession session, Model model) {
        Personal usuario = (Personal) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        model.addAttribute("usuario", usuario);
        model.addAttribute("becas", becaService.listarActivas());
        return "becas/lista";
    }

    @GetMapping("/nueva")
    public String mostrarFormularioNueva(HttpSession session, Model model) {
        Personal usuario = (Personal) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        model.addAttribute("usuario", usuario);
        model.addAttribute("instituciones", institucionService.listarActivas());
        model.addAttribute("beca", new Beca());
        model.addAttribute("modo", "crear");
        return "becas/formulario";
    }

    @PostMapping("/guardar")
    public String guardarBeca(
            @ModelAttribute Beca beca,
            @RequestParam(required = false) Integer idInstitucion,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            if (idInstitucion != null) {
                Institucion institucion = new Institucion();
                institucion.setIdInstitucion(idInstitucion);
                beca.setInstitucion(institucion);
            }

            if (beca.getInstitucion() == null || beca.getInstitucion().getIdInstitucion() == null) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar una institución");
                return "redirect:/becas/nueva";
            }

            becaService.registrar(beca);
            redirectAttributes.addFlashAttribute("mensaje", "Convocatoria registrada correctamente");
            return "redirect:/becas";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/becas/nueva";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
            return "redirect:/becas/nueva";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarEditar(
            @PathVariable Integer id,
            HttpSession session,
            Model model) {

        Personal usuario = (Personal) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        model.addAttribute("usuario", usuario);

        Beca beca = becaService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Beca no encontrada"));

        model.addAttribute("instituciones", institucionService.listarActivas());
        model.addAttribute("beca", beca);
        model.addAttribute("modo", "editar");
        return "becas/formulario";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarBeca(
            @PathVariable Integer id,
            @ModelAttribute Beca becaActualizada,
            @RequestParam(required = false) Integer idInstitucion,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            if (idInstitucion != null) {
                Institucion institucion = new Institucion();
                institucion.setIdInstitucion(idInstitucion);
                becaActualizada.setInstitucion(institucion);
            }

            becaService.actualizar(id, becaActualizada);
            redirectAttributes.addFlashAttribute("mensaje", "Convocatoria actualizada correctamente");
            return "redirect:/becas";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/becas/editar/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar: " + e.getMessage());
            return "redirect:/becas/editar/" + id;
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarBeca(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        becaService.desactivar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Convocatoria desactivada correctamente");
        return "redirect:/becas";
    }

    @GetMapping("/historial")
    public String listarHistorial(HttpSession session, Model model) {
        Personal usuario = (Personal) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        model.addAttribute("usuario", usuario);
        model.addAttribute("becas", becaService.listarTodas());
        model.addAttribute("mostrarInactivas", true);
        return "becas/lista";
    }
}
