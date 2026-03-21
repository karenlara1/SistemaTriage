package co.edu.uniquindio.sistematriage.repository;

import co.edu.uniquindio.sistematriage.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
}
