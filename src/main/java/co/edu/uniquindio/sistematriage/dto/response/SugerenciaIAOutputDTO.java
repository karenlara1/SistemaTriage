package co.edu.uniquindio.sistematriage.dto.response;

import co.edu.uniquindio.sistematriage.domain.enums.Prioridad;
import co.edu.uniquindio.sistematriage.domain.enums.TipoSolicitud;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SugerenciaIAOutputDTO {
    private TipoSolicitud tipoSugerido;
    private Prioridad prioridadSugerida;
    private String justificacion;
}