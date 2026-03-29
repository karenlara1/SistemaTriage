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
 * Controller REST que expone el endpoint de sugerencia automática mediante IA.
 * Permite al sistema sugerir un tipo de solicitud y una prioridad
 *
 */
@RestController
@RequestMapping("/solicitudes")
@RequiredArgsConstructor
public class SugerenciaIAController {

    private final SugerenciaIAService sugerenciaIAService;

    /*
     * POST /solicitudes/sugerir-clasificacion
     * Genera una sugerencia de clasificación y prioridad basada en la descripción.
     * Construye una solicitud temporal con la descripción recibida y la pasa
     * al servicio de IA simulada para obtener tipo y prioridad sugeridos.
     * @return sugerencia con tipo y prioridad con HTTP 200
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
                .build();

        return ResponseEntity.ok(response);
    }

}
