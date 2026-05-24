package com.miapp.software.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "documento")
@Data
@NoArgsConstructor
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_documento;

    @ManyToOne
    @JoinColumn(name = "id_solicitud", nullable = false)
    private Solicitud solicitud;

    @ManyToOne
    @JoinColumn(name = "id_requisito", nullable = false)
    private Requisito requisito;

    @Column(name = "tipo_documento", nullable = false, length = 100)
    private String tipoDocumento;

    @Column(name = "nombre_archivo", nullable = false, length = 255)
    private String nombreArchivo;

    @Column(name = "ruta_archivo", nullable = false, length = 255)
    private String rutaArchivo;

    @Column(nullable = false, length = 10)
    private String formato; // "PDF", "JPG", "PNG"

    @Column(name = "tamaño_bytes", nullable = false)
    private int tamañoBytes;

    @Column(name = "fecha_subida", nullable = false)
    private LocalDateTime fechaSubida;

    @Column(nullable = false)
    private boolean validado = false;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    // Método para establecer fecha automáticamente
    @PrePersist
    protected void onCreate() {
        fechaSubida = LocalDateTime.now();
    }
}