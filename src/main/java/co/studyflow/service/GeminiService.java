package co.studyflow.service;

import co.studyflow.dto.GeminiResponseDTO;
import co.studyflow.exception.ResourceNotFoundException;
import co.studyflow.infrastructure.GeminiHttpClient;
import co.studyflow.model.Mazo;
import co.studyflow.patterns.facade.GeminiAnalysisFacade;
import co.studyflow.repository.MazoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para integración con Gemini API
 * Usa el patrón Facade para simplificar la integración
 */
@Service
@Transactional(readOnly = true)
public class GeminiService {
    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);
    
    @Autowired
    private MazoRepository mazoRepository;
    
    @Autowired
    private GeminiHttpClient geminiClient;
    
    private GeminiAnalysisFacade facade;
    
    @Autowired
    public void setGeminiClient(GeminiHttpClient geminiClient) {
        this.geminiClient = geminiClient;
        this.facade = new GeminiAnalysisFacade(geminiClient);
    }
    
    /**
     * Analiza un mazo y retorna tarjetas sugeridas
     */
    public GeminiResponseDTO analizarMazo(String mazoId, Integer numeroTarjetas) {
        Mazo mazo = mazoRepository.findById(mazoId)
                .orElseThrow(() -> new ResourceNotFoundException("Mazo no encontrado"));
        
        if (numeroTarjetas == null || numeroTarjetas <= 0) {
            numeroTarjetas = 5;
        }
        
        logger.info("Analizando mazo con Gemini: {}", mazoId);
        
        try {
            return facade.analizarYCompletar(mazo, numeroTarjetas);
        } catch (Exception e) {
            logger.error("Error al analizar mazo con Gemini: {}", e.getMessage());
            throw e;
        }
    }
}
