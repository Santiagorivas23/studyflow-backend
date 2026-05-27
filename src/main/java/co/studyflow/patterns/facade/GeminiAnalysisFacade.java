package co.studyflow.patterns.facade;

import co.studyflow.dto.GeminiResponseDTO;
import co.studyflow.infrastructure.GeminiHttpClient;
import co.studyflow.model.Mazo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Facade Pattern para simplificar la integración con Gemini
 * Oculta toda la complejidad de: construcción de prompts, llamadas HTTP, parseo de JSON, etc.
 */
public class GeminiAnalysisFacade {
    private static final Logger logger = LoggerFactory.getLogger(GeminiAnalysisFacade.class);
    private final GeminiHttpClient geminiClient;
    
    public GeminiAnalysisFacade(GeminiHttpClient geminiClient) {
        this.geminiClient = geminiClient;
    }
    
    /**
     * Analiza un mazo y retorna tarjetas sugeridas
     * El cliente solo llama este método y recibe objetos listos para usar
     */
    public GeminiResponseDTO analizarYCompletar(Mazo mazo, int numeroTarjetas) {
        try {
            logger.info("Iniciando análisis de mazo: {}", mazo.getNombre());
            
            // Construir prompt
            String prompt = construirPrompt(mazo, numeroTarjetas);
            
            // Llamar a Gemini
            String respuesta = geminiClient.enviarPrompt(prompt);
            
            // Parsear respuesta y mapear a objetos Tarjeta
            GeminiResponseDTO response = geminiClient.parsearRespuesta(respuesta, mazo);
            
            logger.info("Análisis completado. Tarjetas sugeridas: {}", response.getTarjetasSugeridas().size());
            return response;
            
        } catch (Exception e) {
            logger.error("Error al analizar mazo con Gemini: {}", e.getMessage());
            throw new RuntimeException("No fue posible analizar el mazo en este momento. Intenta de nuevo.", e);
        }
    }
    
    /**
     * Construye el prompt estructurado para Gemini
     */
    private String construirPrompt(Mazo mazo, int numeroTarjetas) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Eres un asistente académico especializado en identificar vacíos temáticos.\n\n");
        prompt.append("Dado el siguiente mazo de estudio con el tema \"").append(mazo.getNombre()).append("\":\n\n");
        prompt.append("TARJETAS QUE YA EXISTEN EN EL MAZO (NO las repitas ni generes variaciones sintácticas — mismo concepto con otras palabras):\n");

        // Agregar tarjetas existentes
        if (mazo.getTarjetas() != null && !mazo.getTarjetas().isEmpty()) {
            mazo.getTarjetas().forEach(t ->
                prompt.append("- ").append(t.getFrente())
                      .append(" → ").append(t.getReverso()).append("\n")
            );
        } else {
            prompt.append("(ninguna todavía)\n");
        }

        prompt.append("\nTu tarea:\n");
        prompt.append("1. Identificar qué conceptos importantes del tema \"").append(mazo.getNombre())
              .append("\" NO están cubiertos por las tarjetas anteriores.\n");
        prompt.append("2. Generar exactamente ").append(numeroTarjetas)
              .append(" tarjetas NUEVAS que cubran ASPECTOS DIFERENTES a los ya existentes.\n");
        prompt.append("3. No repitas conceptos ya presentes ni reformules preguntas existentes con otras palabras.\n");
        prompt.append("4. Responder ÚNICAMENTE con un array JSON válido con el siguiente formato:\n");
        prompt.append("[\n");
        prompt.append("  { \"frente\": \"pregunta o concepto\", \"reverso\": \"respuesta o definición\" }\n");
        prompt.append("]\n");
        prompt.append("Sin texto adicional. Sin explicaciones. Solo el JSON.\n");

        String seed = UUID.randomUUID().toString().substring(0, 8);
        prompt.append("\nID de generación (ignora pero usa para variar): ").append(seed).append("\n");

        return prompt.toString();
    }
}
