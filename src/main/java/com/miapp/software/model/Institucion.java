package com.miapp.software.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "institucion")
@Data
@NoArgsConstructor
public class Institucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_institucion")  // ← Nombre exacto en la BD
    private Integer idInstitucion;     // ← Nombre en Java (camelCase)

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name = "tipo", length = 50, nullable = false)
    private String tipo;  // "Gubernamental", "Privada", "Fundación"

    @Column(name = "contacto", length = 200)
    private String contacto;

    @Column(name = "pagina_web", length = 200)
    private String paginaWeb;

    @Column(name = "estatus", nullable = false)
    private Boolean estatus = true;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }
}