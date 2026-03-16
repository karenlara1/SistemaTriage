package co.edu.uniquindio.sistematriage.domain.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Historial")

public class HistorialSolicitud {

    @Id
    @Column(name = "idHistorial", nullable = false, unique = true)
    private String idHistorial;

    @Column(name = "fechaHoraAccion", nullable = false)
    private LocalDateTime fechaHoraAccion;

    @Column(name = "accion", length = 50)
    private String accion;

    @Column(name = "observacion", length = 200)
    private String observacion;

    //--RELACIONES--
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idSolicitud", nullable = false)
    private Solicitud solicitud;

    @PrePersist
    public void prePersist(){
        if (fechaHoraAccion == null){
            fechaHoraAccion = LocalDateTime.now();
        }
    }
}
