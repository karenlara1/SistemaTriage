package co.edu.uniquindio.sistematriage.repository;

import co.edu.uniquindio.sistematriage.domain.enums.Estado;
import co.edu.uniquindio.sistematriage.domain.enums.Prioridad;
import co.edu.uniquindio.sistematriage.domain.enums.TipoSolicitud;
import co.edu.uniquindio.sistematriage.domain.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SolicitudRepository extends JpaRepository<Solicitud, UUID> {

    List<Solicitud> findByEstado (Estado estado);
    List<Solicitud> findByTipo (TipoSolicitud tipoSolicitud);
    List<Solicitud> findByPrioridad (Prioridad prioridad);
    List<Solicitud> findByResponsable (UUID idUsuario);
    List<Solicitud> findBySolicitante (UUID idUsuario);

}
