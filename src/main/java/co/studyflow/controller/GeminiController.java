package co.studyflow.controller;

import co.studyflow.dto.GeminiResponseDTO;
import co.studyflow.dto.GeminiRequestDTO;
import co.studyflow.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para integración con Gemini
 */
@RestController
@RequestMapping("/mazos")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "https://*.vercel.app"})
public class GeminiController {
    
    @Autowired
    private GeminiService geminiService;
    
    /**
     * POST /api/mazos/{mazoId}/analizar - Analizar mazo con Gemini
     */
    @PostMapping("/{mazoId}/analizar")
    public ResponseEntity<GeminiResponseDTO> analizarMazo(
            @PathVariable String mazoId,
            @RequestBody GeminiRequestDTO request
    ) {
        GeminiResponseDTO response = geminiService.analizarMazo(
                mazoId,
                request.getNumeroTarjetas()
        );
        return ResponseEntity.ok(response);
    }
}
