package com.miapp.software.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "estudiante")
@Data
@NoArgsConstructor
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estudiante")
    private Integer idEstudiante;

    // ✅ EAGER: evita LazyInitializationException en listas
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_escuela", nullable = false)
    private Escuela escuela;

    // ─────────────────────────────────────────
    // RF-06: Datos personales
    // ─────────────────────────────────────────
    @Column(name = "nombre", length = 150, nullable = false)
    private String nombre;

    @Column(name = "matricula", length = 20, unique = true, nullable = false)
    private String matricula;

    @Column(name = "correo", length = 100, unique = true, nullable = false)
    private String correo;

    @Column(name = "curp", length = 18, nullable = false)
    private String curp;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(name = "genero", length = 20, nullable = false)
    private String genero;

    // ─────────────────────────────────────────
    // RF-06: Datos académicos
    // ─────────────────────────────────────────
    @Column(name = "promedio", precision = 4, scale = 2, nullable = false)
    private BigDecimal promedio;

    @Column(name = "semestre", nullable = false)
    private Integer semestre;

    @Column(name = "nivel_educativo", length = 50, nullable = false)
    private String nivelEducativo;

    // ─────────────────────────────────────────
    // RF-08: Datos socioeconómicos
    // "El sistema permitirá registrar la información socioeconómica:
    //  ocupación del padre, la madre o tutor, ingresos familiares
    //  mensuales y situación de vivienda (propia o rentada)"
    // ─────────────────────────────────────────
    @Column(name = "ocupacion_padre", length = 100)
    private String ocupacionPadre;

    @Column(name = "ocupacion_madre", length = 100)
    private String ocupacionMadre;

    @Column(name = "ingresos_mensuales", precision = 10, scale = 2)
    private BigDecimal ingresosMensuales;

    @Column(name = "situacion_vivienda", length = 50)
    private String situacionVivienda; // "Propia", "Rentada", "Prestada", "Otro"

    // ─────────────────────────────────────────
    // RF-09: Población vulnerable
    // ─────────────────────────────────────────
    @Column(name = "vulnerabilidad", nullable = false)
    private Boolean vulnerabilidad = false;

    @Column(name = "tipo_vulnerabilidad", length = 100)
    private String tipoVulnerabilidad;
    // "Indígena", "Hablante de lengua indígena", "Discapacidad", "Afromexicano"

    // ─────────────────────────────────────────
    // Control
    // ─────────────────────────────────────────
    @Column(name = "estatus", nullable = false)
    private Boolean estatus = true;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        this.fechaRegistro      = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}