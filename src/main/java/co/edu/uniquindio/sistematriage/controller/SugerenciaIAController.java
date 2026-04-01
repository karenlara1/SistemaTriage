package co.edu.uniquindio.sistematriage.controller;

import co.edu.uniquindio.sistematriage.domain.enums.Prioridad;
import co.edu.uniquindio.sistematriage.domain.enums.TipoSolicitud;
import co.edu.uniquindio.sistematriage.domain.model.Solicitud;
import co.edu.uniquindio.sistematriage.dto.request.SugerenciaIAInputDTO;
import co.edu.uniquindio.sistematriage.dto.response.SugerenciaIAOutputDTO;
import co.edu.uniquindio.sistematriage.services.SugerenciaIAService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller REST que expone el endpoint de sugerencia automática mediante IA simulada.
 * Analiza la descripción ingresada y sugiere tipo de solicitud y prioridad (RF-10).
 */
@RestController
@RequestMapping("/solicitudes")
@RequiredArgsConstructor
public class SugerenciaIAController {

    private final SugerenciaIAService sugerenciaIAService;

    /*
     * POST /solicitudes/sugerir-clasificacion
     * Genera una sugerencia de tipo y prioridad basada en la descripción del texto.
     * @return sugerencia con tipo, prioridad y justificación con HTTP 200
     */
    @PostMapping("/sugerir-clasificacion")
    public ResponseEntity<SugerenciaIAOutputDTO> sugerir(
            @Valid @RequestBody SugerenciaIAInputDTO dto) {

        Solicitud solicitudTemp = Solicitud.builder()
                .descripcion(dto.getDescripcion())
                .build();

        Map<String, Object> resultado = sugerenciaIAService.obtenerSugerenciaSimulada(solicitudTemp);

        SugerenciaIAOutputDTO response = SugerenciaIAOutputDTO.builder()
                .tipoSugerido((TipoSolicitud) resultado.get("tipo"))
                .prioridadSugerida((Prioridad) resultado.get("prioridad"))
                .justificacion((String) resultado.get("justificacion"))
                .build();

        return ResponseEntity.ok(response);
    }
}