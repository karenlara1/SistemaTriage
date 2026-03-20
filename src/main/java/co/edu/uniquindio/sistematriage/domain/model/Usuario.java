package co.edu.uniquindio.sistematriage.domain.model;

import co.edu.uniquindio.sistematriage.domain.enums.RolUsuario;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Usuario")
public class Usuario {

    @Id
    @GeneratedValue
    @Column(name = "idUsuario", unique = true, nullable = false)
    private UUID idUsuario;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "correo", length = 200, unique = true)
    private String correo;

    @Column(name = "activo")
    private boolean activo;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false, length = 30)
    private RolUsuario rol;

    @OneToMany(mappedBy = "solicitante", fetch = FetchType.LAZY)
    private List<Solicitud> solicitudesRealizadas = new ArrayList<>();

    @OneToMany(mappedBy = "responsable", fetch = FetchType.LAZY)
    private List<Solicitud> solicitudesAsignadas = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<HistorialSolicitud> accionesRealizadas = new ArrayList<>();
}

