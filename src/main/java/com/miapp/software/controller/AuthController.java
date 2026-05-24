package com.miapp.software.controller;

import com.miapp.software.model.Personal;
import com.miapp.software.service.PersonalService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private PersonalService personalService;

    @GetMapping("/login")
    public String mostrarLogin(HttpSession session) {
        if (session.getAttribute("usuario") != null) return "redirect:/dashboard";
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(
            @RequestParam String correo,
            @RequestParam String contraseña,
            HttpSession session,
            Model model) {

        Personal personal = personalService.autenticar(correo, contraseña).orElse(null);

        if (personal != null) {
            session.setAttribute("usuario", personal);

            // CLAVE: guardamos el nombre del rol como String con la clave "rolNombre"
            // El AdminController lo lee con session.getAttribute("rolNombre")
            // Ambos deben usar la misma clave — antes AuthController usaba "rol" y
            // AdminController buscaba "rolNombre", por eso nunca encontraba al Administrador
            String rolNombre = (personal.getRol() != null)
                    ? personal.getRol().getNombreRol()
                    : "Sin Rol";
            session.setAttribute("rolNombre", rolNombre);
            session.setAttribute("nombre", personal.getNombre());

            // Actualizar último acceso
            personal.setUltimoAcceso(java.time.LocalDateTime.now());

            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Correo o contraseña incorrectos");
            return "login";
        }
    }

    @GetMapping("/dashboard")
    public String mostrarDashboard(HttpSession session, Model model) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";

        Personal usuario = (Personal) session.getAttribute("usuario");
        model.addAttribute("usuario", usuario);
        return "dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}