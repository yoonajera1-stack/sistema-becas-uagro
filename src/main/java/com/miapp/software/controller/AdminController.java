package com.miapp.software.controller;

import com.miapp.software.model.Personal;
import com.miapp.software.model.Rol;
import com.miapp.software.service.PersonalService;
import com.miapp.software.service.RolService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private PersonalService personalService;

    @Autowired
    private RolService rolService;

    // En lugar de leer el rol del objeto Personal en sesión
    // (que puede estar detached y lanzar NullPointerException),
    // leemos directamente el String "rolNombre" de la sesión.
    // AuthController lo guarda como session.setAttribute("rol", ...)
    // así que lo leemos con la misma clave "rol"
    // ─────────────────────────────────────────────────
    private Personal validarAdmin(HttpSession session) {
        Personal usuario = (Personal) session.getAttribute("usuario");
        if (usuario == null) return null;

        // Leer el rol como String simple desde la sesión — nunca falla
        String rolEnSesion = (String) session.getAttribute("rol");

        // Si por algún motivo no está en sesión, intentar con el objeto directamente
        if (rolEnSesion == null && usuario.getRol() != null) {
            rolEnSesion = usuario.getRol().getNombreRol();
        }

        if (!"Administrador".equals(rolEnSesion)) return null;
        return usuario;
    }

    private String redirigirSiNoAutorizado(HttpSession session) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";
        return "redirect:/dashboard?accesoDenegado=true";
    }

    // ─────────────────────────────────────────────────
    // PANEL PRINCIPAL
    // ─────────────────────────────────────────────────
    @GetMapping
    public String mostrarPanelAdmin(HttpSession session, Model model) {
        Personal usuario = validarAdmin(session);
        if (usuario == null) return redirigirSiNoAutorizado(session);

        model.addAttribute("usuario", usuario);
        model.addAttribute("personales", personalService.listarTodos());
        return "admin/panel";
    }

    // ─────────────────────────────────────────────────
    // NUEVO USUARIO
    // ─────────────────────────────────────────────────
    @GetMapping("/usuarios/nuevo")
    public String mostrarNuevoUsuario(HttpSession session, Model model) {
        Personal usuario = validarAdmin(session);
        if (usuario == null) return redirigirSiNoAutorizado(session);

        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", rolService.listarActivos());
        model.addAttribute("personal", new Personal());
        model.addAttribute("modo", "crear");
        return "admin/usuario-formulario";
    }

    // ─────────────────────────────────────────────────
    // GUARDAR NUEVO USUARIO
    // ─────────────────────────────────────────────────
    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(
            @ModelAttribute Personal personal,
            @RequestParam Integer idRol,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (validarAdmin(session) == null) return "redirect:/login";

        try {
            Rol rol = new Rol();
            rol.setIdRol(idRol);
            personal.setRol(rol);

            personalService.guardar(personal);
            redirectAttributes.addFlashAttribute("mensaje", "✅ Usuario registrado correctamente.");
            return "redirect:/admin";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/usuarios/nuevo";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
            return "redirect:/admin/usuarios/nuevo";
        }
    }

    // ─────────────────────────────────────────────────
    // EDITAR USUARIO
    // ─────────────────────────────────────────────────
    @GetMapping("/usuarios/editar/{id}")
    public String mostrarEditarUsuario(
            @PathVariable Integer id,
            HttpSession session,
            Model model) {

        Personal usuario = validarAdmin(session);
        if (usuario == null) return redirigirSiNoAutorizado(session);

        Personal personal = personalService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));

        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", rolService.listarActivos());
        model.addAttribute("personal", personal);
        model.addAttribute("modo", "editar");
        return "admin/usuario-formulario";
    }

    // ─────────────────────────────────────────────────
    // ACTUALIZAR USUARIO
    // ─────────────────────────────────────────────────
    @PostMapping("/usuarios/actualizar/{id}")
    public String actualizarUsuario(
            @PathVariable Integer id,
            @ModelAttribute Personal personalActualizado,
            @RequestParam Integer idRol,
            @RequestParam(required = false) String nuevaContrasena,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (validarAdmin(session) == null) return "redirect:/login";

        try {
            Rol rol = new Rol();
            rol.setIdRol(idRol);
            personalActualizado.setRol(rol);

            personalActualizado.setContraseña(
                    (nuevaContrasena != null && !nuevaContrasena.isBlank()) ? nuevaContrasena : null
            );

            personalService.actualizarPermisos(id, personalActualizado);
            redirectAttributes.addFlashAttribute("mensaje", "✅ Usuario actualizado correctamente.");
            return "redirect:/admin";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/usuarios/editar/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar: " + e.getMessage());
            return "redirect:/admin/usuarios/editar/" + id;
        }
    }

    // ─────────────────────────────────────────────────
    // DESHABILITAR USUARIO
    // ─────────────────────────────────────────────────
    @GetMapping("/usuarios/deshabilitar/{id}")
    public String deshabilitarUsuario(
            @PathVariable Integer id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Personal usuario = validarAdmin(session);
        if (usuario == null) return "redirect:/login";

        try {
            personalService.deshabilitar(id, usuario.getIdPersonal());
            redirectAttributes.addFlashAttribute("mensaje", "✅ Usuario deshabilitado correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin";
    }

    // ─────────────────────────────────────────────────
    // BITÁCORA
    // ─────────────────────────────────────────────────
    @GetMapping("/bitacora")
    public String mostrarBitacora(HttpSession session, Model model) {
        Personal usuario = validarAdmin(session);
        if (usuario == null) return redirigirSiNoAutorizado(session);

        model.addAttribute("usuario", usuario);
        return "admin/bitacora";
    }
}