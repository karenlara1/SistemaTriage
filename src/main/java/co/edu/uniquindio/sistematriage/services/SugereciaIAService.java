package co.edu.uniquindio.sistematriage.services;

import co.edu.uniquindio.sistematriage.domain.enums.Prioridad;
import co.edu.uniquindio.sistematriage.domain.enums.TipoSolicitud;
import co.edu.uniquindio.sistematriage.domain.model.Solicitud;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SugereciaIAService {

   public Map<String, Object> obtenerSugerenciaSimulada(Solicitud solicitud) {
    Map<String, Object> sugerencia = new HashMap<>();

    // Usa datos reales de la solicitud para simular una respuesta contextual
    TipoSolicitud tipo = solicitud.getTipoSolicitud() != null
            ? solicitud.getTipoSolicitud()
            : TipoSolicitud.CONSULTA;

    Prioridad prioridad = (solicitud.getDescripcion() != null &&
            solicitud.getDescripcion().toLowerCase().contains("urgente"))
            ? Prioridad.ALTA
            : Prioridad.MEDIA;

    sugerencia.put("tipo", tipo);
    sugerencia.put("prioridad", prioridad);
    sugerencia.put("justificacion",
            "Sugerencia simulada basada en descripción: " + solicitud.getDescripcion());

    return sugerencia;
}
}
