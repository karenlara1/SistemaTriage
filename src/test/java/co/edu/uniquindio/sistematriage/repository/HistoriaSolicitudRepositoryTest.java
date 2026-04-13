package co.edu.uniquindio.sistematriage.repository;

import co.edu.uniquindio.sistematriage.domain.enums.Estado;
import co.edu.uniquindio.sistematriage.domain.enums.Prioridad;
import co.edu.uniquindio.sistematriage.domain.enums.TipoSolicitud;
import co.edu.uniquindio.sistematriage.domain.enums.RolUsuario;
import co.edu.uniquindio.sistematriage.domain.model.HistorialSolicitud;
import co.edu.uniquindio.sistematriage.domain.model.Solicitud;
import co.edu.uniquindio.sistematriage.domain.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
class HistoriaSolicitudRepositoryTest {

    @Autowired
    private HistoriaSolicitudRepository historiaSolicitudRepository;

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void findBySolicitudIdReturnsOrderedHistory() {
        Usuario solicitante = Usuario.builder()
                .nombre("Gabriel García")
                .correo("gabriel@example.com")
                .activo(true)
                .rol(RolUsuario.ESTUDIANTE)
                .build();
        usuarioRepository.save(solicitante);

        Solicitud solicitud = Solicitud.builder()
                .nombre("Homologación de asignatura")
                .descripcion("Solicitud de homologación de materia de otro programa")
                .canalOrigen(co.edu.uniquindio.sistematriage.domain.enums.Canal.EMAIL)
                .solicitante(solicitante)
                .estado(Estado.REGISTRADA)
                .prioridad(Prioridad.MEDIA)
                .tipoSolicitud(TipoSolicitud.HOMOLOGACION)
                .build();
        solicitudRepository.save(solicitud);

        HistorialSolicitud primer = HistorialSolicitud.builder()
                .solicitud(solicitud)
                .usuario(solicitante)
                .accion("REGISTRO")
                .observacion("Solicitud registrada")
                .estadoAnterior(null)
                .estadoNuevo(Estado.REGISTRADA)
                .fechaHoraAccion(LocalDateTime.now().minusMinutes(2))
                .build();

        HistorialSolicitud segundo = HistorialSolicitud.builder()
                .solicitud(solicitud)
                .usuario(solicitante)
                .accion("CLASIFICACION")
                .observacion("Solicitud clasificada")
                .estadoAnterior(Estado.REGISTRADA)
                .estadoNuevo(Estado.CLASIFICADA)
                .fechaHoraAccion(LocalDateTime.now().minusMinutes(1))
                .build();

        historiaSolicitudRepository.save(primer);
        historiaSolicitudRepository.save(segundo);

        List<HistorialSolicitud> resultados = historiaSolicitudRepository
                .findBySolicitud_IdSolicitudOrderByFechaHoraAccionAsc(solicitud.getIdSolicitud());

        assertThat(resultados).hasSize(2);
        assertThat(resultados.get(0).getAccion()).isEqualTo("REGISTRO");
        assertThat(resultados.get(1).getAccion()).isEqualTo("CLASIFICACION");
    }
}
