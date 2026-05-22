package co.studyflow.controller;

import co.studyflow.dto.SesionDTO;
import co.studyflow.dto.SesionActivaDTO;
import co.studyflow.dto.RespuestaRegistroDTO;
import co.studyflow.model.Calificacion;
import co.studyflow.model.Sesion;
import co.studyflow.model.Tarjeta;
import co.studyflow.service.SesionService;
import co.studyflow.util.EntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestión de sesiones de estudio
 */
@RestController
@RequestMapping("/sesiones")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "https://*.vercel.app"})
public class SesionController {
    
    @Autowired
    private SesionService sesionService;
    
    /**
     * POST /api/sesiones/iniciar - Iniciar nueva sesión
     */
    @PostMapping("/iniciar")
    public ResponseEntity<SesionActivaDTO> iniciarSesion(
            @RequestBody Map<String, String> request,
            @RequestHeader("X-Usuario-Id") String usuarioId
    ) {
        String mazoId = request.get("mazoId");
        
        Sesion sesion = sesionService.iniciarSesion(mazoId, usuarioId);
        
        // Obtener primera tarjeta
        List<Tarjeta> tarjetas = sesion.getMazo().getTarjetas() != null 
                ? sesion.getMazo().getTarjetas() 
                : List.of();
        
        SesionActivaDTO dto = new SesionActivaDTO();
        dto.setSesionId(sesion.getId());
        dto.setTotalTarjetas(tarjetas.size());
        dto.setTarjetasRestantes(tarjetas.size());
        dto.setIndice(0);
        
        if (!tarjetas.isEmpty()) {
            dto.setTarjetaActual(EntityMapper.toTarjetaDTO(tarjetas.get(0)));
        }
        
        return ResponseEntity.status(201).body(dto);
    }
    
    /**
     * GET /api/sesiones/{sesionId} - Obtener sesión activa
     */
    @GetMapping("/{sesionId}")
    public ResponseEntity<SesionActivaDTO> obtenerActiva(@PathVariable String sesionId) {
        Sesion sesion = sesionService.obtenerSesion(sesionId);
        
        List<Tarjeta> tarjetas = sesion.getMazo().getTarjetas() != null 
                ? sesion.getMazo().getTarjetas() 
                : List.of();
        
        int indice = sesion.getTarjetaActualIndex() != null ? sesion.getTarjetaActualIndex() : 0;
        
        SesionActivaDTO dto = new SesionActivaDTO();
        dto.setSesionId(sesion.getId());
        dto.setTotalTarjetas(tarjetas.size());
        dto.setTarjetasRestantes(Math.max(0, tarjetas.size() - indice));
        dto.setIndice(indice);
        
        if (indice < tarjetas.size()) {
            dto.setTarjetaActual(EntityMapper.toTarjetaDTO(tarjetas.get(indice)));
        }
        
        return ResponseEntity.ok(dto);
    }
    
    /**
     * POST /api/sesiones/{sesionId}/responder - Registrar respuesta
     */
    @PostMapping("/{sesionId}/responder")
    public ResponseEntity<RespuestaRegistroDTO> registrarRespuesta(
            @PathVariable String sesionId,
            @RequestBody Map<String, Object> request
    ) {
        String tarjetaId = (String) request.get("tarjetaId");
        String calificacionStr = (String) request.get("calificacion");
        Integer tiempoRespuesta = (Integer) request.getOrDefault("tiempoRespuesta", 0);
        
        Calificacion calificacion = Calificacion.valueOf(calificacionStr);
        
        sesionService.registrarRespuesta(sesionId, tarjetaId, calificacion, tiempoRespuesta);

        // Fetch updated session (index already incremented and saved by service)
        Sesion sesion = sesionService.obtenerSesion(sesionId);
        List<Tarjeta> tarjetas = sesion.getMazo().getTarjetas() != null
                ? sesion.getMazo().getTarjetas()
                : List.of();

        int indiceActual = sesion.getTarjetaActualIndex() != null ? sesion.getTarjetaActualIndex() : 0;

        RespuestaRegistroDTO dto = new RespuestaRegistroDTO();
        dto.setSesionId(sesionId);
        dto.setTarjetaId(tarjetaId);
        dto.setCalificacion(calificacion);

        boolean sesionCompletada = indiceActual >= tarjetas.size();
        dto.setSesionCompletada(sesionCompletada);

        if (!sesionCompletada && indiceActual < tarjetas.size()) {
            dto.setSiguienteTarjeta(EntityMapper.toTarjetaDTO(tarjetas.get(indiceActual)));
        }

        return ResponseEntity.ok(dto);
    }
    
    /**
     * POST /api/sesiones/{sesionId}/finalizar - Finalizar sesión
     */
    @PostMapping("/{sesionId}/finalizar")
    public ResponseEntity<SesionDTO> finalizarSesion(@PathVariable String sesionId) {
        Sesion sesion = sesionService.finalizarSesion(sesionId);
        return ResponseEntity.ok(EntityMapper.toSesionDTO(sesion));
    }
    
    /**
     * GET /api/mazos/{mazoId}/sesiones - Obtener historial
     */
    @GetMapping
    public ResponseEntity<List<SesionDTO>> obtenerHistorial(@RequestParam String mazoId) {
        List<Sesion> sesiones = sesionService.obtenerHistorial(mazoId);
        List<SesionDTO> dtos = sesiones.stream()
                .map(EntityMapper::toSesionDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
