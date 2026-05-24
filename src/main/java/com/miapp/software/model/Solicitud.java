package com.miapp.software.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitud")
@Data
@NoArgsConstructor
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Integer idSolicitud;

    @ManyToOne
    @JoinColumn(name = "id_estudiante", nullable = false)
    private Estudiante estudiante;

    @ManyToOne
    @JoinColumn(name = "id_beca", nullable = false)
    private Beca beca;

    @Column(name = "folio", length = 20, unique = true, nullable = false)
    private String folio;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;

    // ✅ ESTE ES EL CAMPO CORRECTO: 'estado', NO 'estatus'
    @Column(name = "estado", length = 50, nullable = false)
    private String estado;  // "Pendiente", "Documentación Completa", "Enviada a Instancia", etc.

    @Column(name = "periodo", length = 20, nullable = false)
    private String periodo;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @ManyToOne
    @JoinColumn(name = "id_personal_registro")
    private Personal personalRegistro;

    @ManyToOne
    @JoinColumn(name = "id_personal_validacion")
    private Personal personalValidacion;

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    @Column(name = "respuesta_instancia", columnDefinition = "TEXT")
    private String respuestaInstancia;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        this.fechaSolicitud = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}