package com.miapp.software.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "personal")
@Data
@NoArgsConstructor
public class Personal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_personal")
    private Integer idPersonal;

    // ✅ CORREGIDO: fetch = EAGER para que el Rol siempre se cargue junto con Personal
    // Evita LazyInitializationException cuando el objeto se guarda en sesión HTTP
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name = "correo", length = 100, unique = true, nullable = false)
    private String correo;

    @Column(name = "contraseña", length = 255, nullable = false)
    private String contraseña;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "departamento", length = 100)
    private String departamento;

    @Column(name = "estatus", nullable = false)
    private Boolean estatus = true;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    @PrePersist
    protected void onCreate() {
        this.fechaRegistro = LocalDateTime.now();
    }
}