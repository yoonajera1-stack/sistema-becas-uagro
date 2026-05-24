package com.miapp.software.service;

import com.miapp.software.model.Personal;
import com.miapp.software.model.Solicitud;
import com.miapp.software.repository.SolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SolicitudService {

    @Autowired
    private SolicitudRepository solicitudRepository;

    /*
     * FLUJO DE ESTADOS según C.U.4 del documento:
     *
     * La UAGRO NO aprueba ni rechaza becas, solo valida documentación y envía a la instancia.
     *
     *  [Pendiente]
     *      ↓  (Personal valida documentos)
     *  [Documentación Completa]
     *      ↓  (Personal marca como enviada a la instancia externa)
     *  [Enviada a Instancia]
     *      ↓  (La instancia externa decide y el personal registra el resultado)
     *  [Aprobada] o [Rechazada]
     */

    // Estados válidos en orden
    public static final String ESTADO_PENDIENTE             = "Pendiente";
    public static final String ESTADO_DOC_COMPLETA          = "Documentación Completa";
    public static final String ESTADO_ENVIADA               = "Enviada a Instancia";
    public static final String ESTADO_APROBADA              = "Aprobada";
    public static final String ESTADO_RECHAZADA             = "Rechazada";

    // ------------------------------------
    // Registrar nueva solicitud
    // ------------------------------------
    public Solicitud registrar(Solicitud solicitud) {
        // Folio: BECAS-2026-000001
        String folio = "BECAS-" + LocalDateTime.now().getYear()
                + "-" + String.format("%06d", solicitudRepository.count() + 1);
        solicitud.setFolio(folio);
        solicitud.setEstado(ESTADO_PENDIENTE);
        solicitud.setFechaSolicitud(LocalDateTime.now());
        return solicitudRepository.save(solicitud);
    }

    // ------------------------------------
    // Avanzar al siguiente estado del flujo
    // Valida que la transición sea legal según el documento
    // ------------------------------------
    public Solicitud avanzarEstado(Integer id, String nuevoEstado,
                                   String observaciones, Personal personalValidacion) {

        return solicitudRepository.findById(id).map(solicitud -> {

            if (!transicionValida(solicitud.getEstado(), nuevoEstado)) {
                throw new IllegalArgumentException(
                        "Transición no permitida: de '" + solicitud.getEstado()
                                + "' a '" + nuevoEstado + "'"
                );
            }

            solicitud.setEstado(nuevoEstado);

            if (observaciones != null && !observaciones.isBlank()) {
                solicitud.setObservaciones(observaciones);
            }

            // Registrar quién validó
            if (personalValidacion != null) {
                solicitud.setPersonalValidacion(personalValidacion);
            }

            // Si es resultado final, registrar fecha de respuesta
            if (nuevoEstado.equals(ESTADO_APROBADA) || nuevoEstado.equals(ESTADO_RECHAZADA)) {
                solicitud.setFechaRespuesta(LocalDateTime.now());
            }

            return solicitudRepository.save(solicitud);

        }).orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada: " + id));
    }

    // Mantener compatibilidad con código existente
    public Solicitud actualizarEstado(Integer id, String nuevoEstado, String observaciones) {
        return avanzarEstado(id, nuevoEstado, observaciones, null);
    }

    // ------------------------------------
    // Reglas de transición según C.U.4
    // ------------------------------------
    private boolean transicionValida(String estadoActual, String nuevoEstado) {
        return switch (estadoActual) {
            case "Pendiente"               -> nuevoEstado.equals(ESTADO_DOC_COMPLETA)
                    || nuevoEstado.equals(ESTADO_RECHAZADA);
            case "Documentación Completa"  -> nuevoEstado.equals(ESTADO_ENVIADA)
                    || nuevoEstado.equals(ESTADO_PENDIENTE);
            case "Enviada a Instancia"     -> nuevoEstado.equals(ESTADO_APROBADA)
                    || nuevoEstado.equals(ESTADO_RECHAZADA);
            default -> false; // Aprobada y Rechazada son estados finales
        };
    }

    // Obtener el siguiente estado posible (para mostrar botón correcto en la vista)
    public String siguienteEstado(String estadoActual) {
        return switch (estadoActual) {
            case "Pendiente"              -> ESTADO_DOC_COMPLETA;
            case "Documentación Completa" -> ESTADO_ENVIADA;
            case "Enviada a Instancia"    -> ESTADO_APROBADA;
            default -> null;
        };
    }

    // ------------------------------------
    // Consultas
    // ------------------------------------
    public List<Solicitud> listarTodos() {
        return solicitudRepository.findAll();
    }

    public List<Solicitud> listarPorEstado(String estado) {
        return solicitudRepository.findByEstado(estado);
    }

    public List<Solicitud> listarPorEstados(List<String> estados) {
        return solicitudRepository.findByEstadoIn(estados);
    }

    public List<Solicitud> listarPendientesYCompletas() {
        return solicitudRepository.findByEstadoIn(
                List.of(ESTADO_PENDIENTE, ESTADO_DOC_COMPLETA)
        );
    }

    public List<Solicitud> listarEnviadas() {
        return solicitudRepository.findByEstado(ESTADO_ENVIADA);
    }

    public List<Solicitud> listarFinalizadas() {
        return solicitudRepository.findByEstadoIn(
                List.of(ESTADO_APROBADA, ESTADO_RECHAZADA)
        );
    }

    public Optional<Solicitud> buscarPorId(Integer id) {
        return solicitudRepository.findById(id);
    }
}