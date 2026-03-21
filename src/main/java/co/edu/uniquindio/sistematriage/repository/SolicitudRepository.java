package co.edu.uniquindio.sistematriage.repository;

import co.edu.uniquindio.sistematriage.domain.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SolicitudRepository extends JpaRepository<Solicitud, UUID> {

}
