package co.edu.uniquindio.sistematriage.controller;

import co.edu.uniquindio.sistematriage.domain.enums.Estado;
import co.edu.uniquindio.sistematriage.domain.enums.Prioridad;
import co.edu.uniquindio.sistematriage.domain.enums.TipoSolicitud;
import co.edu.uniquindio.sistematriage.domain.model.Solicitud;
import co.edu.uniquindio.sistematriage.domain.model.Usuario;
import co.edu.uniquindio.sistematriage.dto.request.*;
import co.edu.uniquindio.sistematriage.dto.response.HistorialAccionDTO;
import co.edu.uniquindio.sistematriage.dto.response.SolicitudResponseDTO;
import co.edu.uniquindio.sistematriage.exception.ResourceNotFoundException;
import co.edu.uniquindio.sistematriage.mapper.HistorialMapper;
import co.edu.uniquindio.sistematriage.mapper.SolicitudMapper;
import co.edu.uniquindio.sistematriage.repository.UsuarioRepository;
import co.edu.uniquindio.sistematriage.services.SolicitudService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST que expone los endpoints del ciclo de vida de las solicitudes académicas.
 *
 * Gestiona las operaciones de registro, consulta, clasificación, priorización,
 * asignación, atención, cierre e historial de solicitudes.
 *
 * Nota: El parámetro actorId se recibe como query param en esta versión
 * (Hito 2). En el Hito 3 será reemplazado por el usuario extraído del token JWT.
 */
@RestController
@RequestMapping("/solicitudes")
@RequiredArgsConstructor
@Validated
public class SolicitudController {

    private final SolicitudService solicitudService;
    private final UsuarioRepository usuarioRepository;
    private final SolicitudMapper solicitudMapper;
    private final HistorialMapper historialMapper;

    /*
     * POST /solicitudes
     * Registra una nueva solicitud académica en el sistema.
     * @param dto datos de la solicitud ingresados por el cliente
     * @return solicitud creada con HTTP 201
     */
    @PostMapping
    public ResponseEntity<SolicitudResponseDTO> registrar(
            @Valid @RequestBody SolicitudRegistroDTO dto) {

        Usuario solicitante = usuarioRepository
                .findById(UUID.fromString(dto.getIdSolicitante()))
                .orElseThrow(() -> new ResourceNotFoundException("Solicitante no encontrado"));

        Solicitud nueva = Solicitud.builder()
                .descripcion(dto.getDescripcion())
                .canalOrigen(dto.getCanalOrigen())
                .solicitante(solicitante)
                .build();

        Solicitud guardada = solicitudService.registrarSolicitud(nueva);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(solicitudMapper.toResponse(guardada));
    }

    /*
     * GET /solicitudes
     * Lista todas las solicitudes del sistema con filtros opcionales.
     * Filtra por estado, tipo de solicitud, prioridad, UUID del responsable asignado
     * @return lista de solicitudes que cumplen los criterios con HTTP 200
     */
    @GetMapping
    public ResponseEntity<List<SolicitudResponseDTO>> listar(
            @RequestParam(required = false) Estado estado,
            @RequestParam(required = false) TipoSolicitud tipo,
            @RequestParam(required = false) Prioridad prioridad,
            @RequestParam(required = false) UUID responsableId) {

        List<Solicitud> todas = solicitudService.listarSolicitudes();

        List<SolicitudResponseDTO> lista = todas.stream()
                .filter(s -> estado == null || s.getEstado() == estado)
                .filter(s -> tipo == null || s.getTipoSolicitud() == tipo)
                .filter(s -> prioridad == null || s.getPrioridad() == prioridad)
                .filter(s -> responsableId == null || s.getResponsable() != null && s.getResponsable().getIdUsuario().equals(responsableId))
                .map(solicitudMapper::toResponse)
                .toList();

        return ResponseEntity.ok(lista);
    }

    /*
     * GET /solicitudes/{id}
     * Consulta una solicitud específica por su identificador único.
     * @return solicitud encontrada con HTTP 200, o HTTP 404 si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<SolicitudResponseDTO> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(
                solicitudMapper.toResponse(solicitudService.obtenerSolicitudPorId(id))
        );
    }

    /*
     * PATCH /solicitudes/{id}/clasificar
     * Clasifica una solicitud asignándole un tipo específico.
     * @return solicitud actualizada con HTTP 200
     */
    @PatchMapping("/{id}/clasificar")
    public ResponseEntity<SolicitudResponseDTO> clasificar(
            @PathVariable UUID id,
            @Valid @RequestBody ClasificacionInputDTO dto,
            @RequestParam UUID actorId) {

        Usuario actor = usuarioRepository.findById(actorId)
                .orElseThrow(() -> new ResourceNotFoundException("Actor no encontrado"));
        return ResponseEntity.ok(
                solicitudMapper.toResponse(
                        solicitudService.clasificarSolicitud(id, dto.getTipo(), actor)
                )
        );
    }

    /*
     * PATCH /solicitudes/{id}/priorizar
     * Asigna una prioridad a la solicitud junto con su justificación.
     * @return solicitud actualizada con HTTP 200
     */
    @PatchMapping("/{id}/priorizar")
    public ResponseEntity<SolicitudResponseDTO> priorizar(
            @PathVariable UUID id,
            @Valid @RequestBody PriorizacionInputDTO dto,
            @RequestParam UUID actorId) {

        Usuario actor = usuarioRepository.findById(actorId)
                .orElseThrow(() -> new ResourceNotFoundException("Actor no encontrado"));
        return ResponseEntity.ok(
                solicitudMapper.toResponse(
                        solicitudService.priorizarSolicitud(id, dto.getPriodidad(), dto.getJustificacion(), actor)
                )
        );
    }

    /*
     * PATCH /solicitudes/{id}/asignar
     * Asigna un responsable autorizado a la solicitud.
     * Valida que el responsable exista y esté activo en el sistema.
     * @return solicitud actualizada con HTTP 200
     */
    @PatchMapping("/{id}/asignar")
    public ResponseEntity<SolicitudResponseDTO> asignar(
            @PathVariable UUID id,
            @Valid @RequestBody AsignacionInputDTO dto,
            @RequestParam UUID actorId) {

        Usuario actor = usuarioRepository.findById(actorId)
                .orElseThrow(() -> new ResourceNotFoundException("Actor no encontrado"));
        return ResponseEntity.ok(
                solicitudMapper.toResponse(
                        solicitudService.asignarResponsable(id, UUID.fromString(dto.getResponsableId()), actor)
                )
        );
    }

    /*
     * PATCH /solicitudes/{id}/atender
     * Registra un avance en la atención de la solicitud.
     * @return solicitud actualizada con HTTP 200
     */
    @PatchMapping("/{id}/atender")
    public ResponseEntity<SolicitudResponseDTO> atender(
            @PathVariable UUID id,
            @Valid @RequestBody AtencionInputDTO dto,
            @RequestParam UUID actorId) {

        Usuario actor = usuarioRepository.findById(actorId)
                .orElseThrow(() -> new ResourceNotFoundException("Actor no encontrado"));
        return ResponseEntity.ok(
                solicitudMapper.toResponse(
                        solicitudService.registrarAtencion(id, dto.getObservacion(), actor)
                )
        );
    }

    /*
     * PATCH /solicitudes/{id}/cerrar
     * Cierra definitivamente una solicitud.
     * @return solicitud cerrada con HTTP 200
     */
    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<SolicitudResponseDTO> cerrar(
            @PathVariable UUID id,
            @Valid @RequestBody CierreInputDTO dto,
            @RequestParam UUID actorId) {

        Usuario actor = usuarioRepository.findById(actorId)
                .orElseThrow(() -> new ResourceNotFoundException("Actor no encontrado"));
        return ResponseEntity.ok(
                solicitudMapper.toResponse(
                        solicitudService.cerrarSolicitud(id, dto.getObservacionCierre(), actor)
                )
        );
    }

    /*
     * GET /solicitudes/{id}/historial
     * Consulta el historial auditable completo de una solicitud.
     * Retorna todos los cambios de estado y acciones realizadas sobre ella.
     * @return lista de acciones del historial con HTTP 200
     */
    @PatchMapping("/{id}/historial")
    public ResponseEntity<List<HistorialAccionDTO>> historial(@PathVariable UUID id) {
        return ResponseEntity.ok(
                solicitudService.consultarHistorial(id)
                        .stream().map(historialMapper::toDTO).toList()
        );
    }

}
