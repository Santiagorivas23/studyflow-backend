package co.studyflow.controller;

import co.studyflow.dto.ProgresoDTO;
import co.studyflow.service.ProgresoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
     * GET /api/progreso/globales - Obtener estadísticas globales del usuario autenticado
     */
    @GetMapping("/globales")
    public ResponseEntity<Map<String, Object>> obtenerGlobales(Authentication auth) {
        Map<String, Object> stats = progresoService.obtenerEstadisticasGlobales(auth.getName());
        return ResponseEntity.ok(stats);
    }
}
