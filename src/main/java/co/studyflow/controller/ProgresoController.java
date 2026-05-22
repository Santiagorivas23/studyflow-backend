package co.studyflow.controller;

import co.studyflow.dto.ProgresoDTO;
import co.studyflow.service.ProgresoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para estadísticas y progreso
 */
@RestController
@RequestMapping("/progreso")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "https://*.vercel.app"})
public class ProgresoController {
    
    @Autowired
    private ProgresoService progresoService;
    
    /**
     * GET /api/progreso/{mazoId} - Obtener progreso de un mazo
     */
    @GetMapping("/{mazoId}")
    public ResponseEntity<ProgresoDTO> obtenerDelMazo(@PathVariable String mazoId) {
        ProgresoDTO progreso = progresoService.obtenerProgresoPorMazo(mazoId);
        return ResponseEntity.ok(progreso);
    }
    
    /**
     * GET /api/progreso/globales - Obtener estadísticas globales
     */
    @GetMapping("/globales")
    public ResponseEntity<java.util.Map<String, Object>> obtenerGlobales(
            @RequestHeader("X-Usuario-Id") String usuarioId
    ) {
        java.util.Map<String, Object> stats = progresoService.obtenerEstadisticasGlobales(usuarioId);
        return ResponseEntity.ok(stats);
    }
}
