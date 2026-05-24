package com.miapp.software.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "beca")
@Data
@NoArgsConstructor
public class Beca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_beca")
    private Integer idBeca;

    // ✅ EAGER: evita LazyInitializationException al mostrar listas
    // RF-01: La instancia es EXTERNA (SEP, Bienestar, Santander, etc.) — la UAGRO no otorga la beca
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_institucion", nullable = false)
    private Institucion institucion;

    // RF-01: Nombre de la convocatoria
    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    // RF-01: Tipo de beca (Económica, Académica, Deportiva, Cultural, Mixta)
    @Column(name = "tipo", length = 50, nullable = false)
    private String tipo;

    // RF-01: Total del apoyo económico
    @Column(name = "monto", precision = 10, scale = 2, nullable = false)
    private BigDecimal monto;

    // RF-02: Requisitos generales de la convocatoria
    @Column(name = "requisitos", columnDefinition = "TEXT", nullable = false)
    private String requisitos;

    // RF-01: Nivel educativo al que aplica (Medio Superior, Superior, Ambos)
    // ✅ NUEVO: campo exigido por RF-01 del documento
    @Column(name = "nivel_educativo", length = 50)
    private String nivelEducativo;

    // RF-01: Vigencia de la convocatoria
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    // RF-01 / RF-05: Estatus activa o inactiva
    @Column(name = "estatus", nullable = false)
    private Boolean estatus = true;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }
}