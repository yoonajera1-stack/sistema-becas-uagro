package com.miapp.software.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reporte")
@Data
@NoArgsConstructor
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_reporte;

    @ManyToOne
    @JoinColumn(name = "id_personal", nullable = false)
    private Personal personal;

    @Column(name = "tipo_reporte", nullable = false, length = 50)
    private String tipoReporte; // "Por Escuela", "Por Género", "Por Nivel", "Por Institución", "Población Vulnerable"

    @Column(name = "fecha_generacion", nullable = false)
    private LocalDateTime fechaGeneracion;

    @Column(name = "periodo_inicio", nullable = false)
    private LocalDate periodoInicio;

    @Column(name = "periodo_fin", nullable = false)
    private LocalDate periodoFin;

    @Column(name = "filtro_aplicado", columnDefinition = "TEXT")
    private String filtroAplicado; // JSON con filtros aplicados

    @Column(name = "formato_salida", nullable = false, length = 20)
    private String formatoSalida; // "PDF", "Excel", "CSV"

    @Column(name = "ruta_archivo", nullable = false, length = 255)
    private String rutaArchivo;

    @Column(length = 100)
    private String destinatario; // "Rectoría", "Planeación", "CCITI"

    // Método para establecer fecha automáticamente
    @PrePersist
    protected void onCreate() {
        fechaGeneracion = LocalDateTime.now();
    }
}
