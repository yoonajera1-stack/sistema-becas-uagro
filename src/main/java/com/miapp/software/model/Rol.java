package com.miapp.software.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "rol")
@Data
@NoArgsConstructor
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Integer idRol;

    @Column(name = "nombre_rol", length = 50, unique = true, nullable = false)
    private String nombreRol;  // ← camelCase en Java

    @Column(name = "descripcion", length = 200)
    private String descripcion;

    @Column(name = "permisos", columnDefinition = "TEXT")
    private String permisos;

    @Column(name = "estatus", nullable = false)
    private Boolean estatus = true;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }
}