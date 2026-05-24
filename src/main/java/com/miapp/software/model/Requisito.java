package com.miapp.software.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "requisito")
@Data  // ✅ ESTO GENERA AUTOMÁTICAMENTE: setIdRequisito(), getIdRequisito(), etc.
@NoArgsConstructor
public class Requisito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_requisito")
    private Integer idRequisito;  // ✅ Nombre en Java (camelCase)

    @ManyToOne
    @JoinColumn(name = "id_beca", nullable = false)
    private Beca beca;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name = "tipo", length = 20, nullable = false)
    private String tipo;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "estatus", nullable = false)
    private Boolean estatus = true;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }
}