package co.edu.uniquindio.sistematriage.domain.model;


import co.edu.uniquindio.sistematriage.domain.enums.TipoSolicitud;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Solicitud")
@Getter
@Setter

public class Solicitud {

    @Id
    @GeneratedValue
    @Column(name = "idSolicitud", nullable = false, unique = true)
    private UUID idSolicitud;

    @Column(name = "nombre", nullable = false, length = 30 )
    private String nombre;

    @Column(name = "descripcion", length = 100)
    private String descripcion;

    @Column(name = "fechaHoraRegistro", nullable = false)
    private LocalDateTime fechaHoraRegistro;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipoSolicitud")
    private TipoSolicitud tipoSolicitud;

    @Column(name = "justificacionPrioridad", length = 100)
    private String justificacionPrioridad;

    @OneToMany(mappedBy = "solicitud", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<HistorialSolicitud> historiales = new ArrayList<>();




}
