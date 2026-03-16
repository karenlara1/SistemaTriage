package co.edu.uniquindio.sistematriage.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "Usuario")
public class Usuario {

    @Id
    @Column(name = "idUsuario", unique = true, nullable = false)
    private UUID idUsuario;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "correo", length = 200, unique = true)
    private String correo;

    @Column(name = "activo")
    private boolean activo;


}
