package co.studyflow.infrastructure;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import co.studyflow.dto.GeminiResponseDTO;
import co.studyflow.dto.TarjetaSugeridaDTO;
import co.studyflow.model.Mazo;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Cliente HTTP para comunicación con la API de Gemini
 */
@Component
public class GeminiHttpClient {
    private static final Logger logger = LoggerFactory.getLogger(GeminiHttpClient.class);
    
    @Value("${gemini.api.key:}")
    private String apiKey;
    
    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent}")
    private String apiUrl;
    
    @Value("${gemini.max.timeout.seconds:30}")
    private int maxTimeout;
    
    private final OkHttpClient httpClient;
    private final Gson gson;
    
    public GeminiHttpClient() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(java.time.Duration.ofSeconds(30))
                .readTimeout(java.time.Duration.ofSeconds(60))
                .build();
        this.gson = new Gson();
    }
    
    /**
     * Envía un prompt a la API de Gemini
     */
    public String enviarPrompt(String prompt) throws IOException {
        if (apiKey == null || apiKey.isEmpty()) {
            logger.error("Gemini API key no configurada");
            throw new RuntimeException("API key de Gemini no configurada");
        }
        
        // Construir payload
        JsonObject payload = new JsonObject();
        
        JsonArray contents = new JsonArray();
        JsonObject content = new JsonObject();
        JsonArray parts = new JsonArray();
        
        JsonObject part = new JsonObject();
        part.addProperty("text", prompt);
        parts.add(part);
        
        content.add("parts", parts);
        contents.add(content);
        
        payload.add("contents", contents);

        // Configurar variabilidad para evitar respuestas cacheadas/idénticas entre llamadas
        JsonObject generationConfig = new JsonObject();
        generationConfig.addProperty("temperature", 0.9);
        generationConfig.addProperty("topP", 0.95);
        payload.add("generationConfig", generationConfig);

        // Configurar solicitud
        String urlConKey = apiUrl + "?key=" + apiKey;
        Request request = new Request.Builder()
                .url(urlConKey)
                .post(RequestBody.create(
                    payload.toString(),
                    MediaType.parse("application/json")
                ))
                .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                logger.error("Error en Gemini API: HTTP {} - Body: {}", response.code(), errorBody);
                throw new IOException("Error en Gemini API HTTP " + response.code() + ": " + errorBody);
            }
            
            String responseBody = response.body().string();
            logger.debug("Respuesta de Gemini: {}", responseBody);
            
            return responseBody;
        }
    }
    
    /**
     * Parsea la respuesta de Gemini y extrae el contenido de texto
     */
    public GeminiResponseDTO parsearRespuesta(String respuestaJson, Mazo mazo) {
        try {
            JsonObject response = gson.fromJson(respuestaJson, JsonObject.class);
            
            // Extraer el texto de la respuesta
            JsonArray candidates = response.getAsJsonArray("candidates");
            if (candidates == null || candidates.size() == 0) {
                logger.error("No se encontraron candidatos en la respuesta de Gemini");
                throw new RuntimeException("Respuesta inválida de Gemini");
            }
            
            JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
            JsonObject content = firstCandidate.getAsJsonObject("content");
            JsonArray parts = content.getAsJsonArray("parts");
            String textContent = parts.get(0).getAsJsonObject().get("text").getAsString();
            
            // Parsear el JSON de tarjetas
            List<TarjetaSugeridaDTO> tarjetas = parsearTarjetas(textContent);
            
            // Crear respuesta
            GeminiResponseDTO respuesta = new GeminiResponseDTO();
            respuesta.setMazoId(mazo.getId());
            respuesta.setTarjetasSugeridas(tarjetas);
            respuesta.setResumen("Se generaron " + tarjetas.size() + " tarjetas sugeridas");
            respuesta.setTemasDetectados(extraerTemas(mazo));
            respuesta.setTemasNoDetectados(new ArrayList<>());
            
            return respuesta;
            
        } catch (Exception e) {
            logger.error("Error al parsear respuesta de Gemini: {}", e.getMessage());
            throw new RuntimeException("Error al procesar la respuesta de Gemini", e);
        }
    }
    
    /**
     * Extrae las tarjetas sugeridas del texto
     */
    private List<TarjetaSugeridaDTO> parsearTarjetas(String texto) {
        try {
            // Limpiar el texto (puede tener markdown o caracteres adicionales)
            String cleaned = texto.trim();
            if (cleaned.startsWith("```json")) {
                cleaned = cleaned.substring(7);
            }
            if (cleaned.startsWith("```")) {
                cleaned = cleaned.substring(3);
            }
            if (cleaned.endsWith("```")) {
                cleaned = cleaned.substring(0, cleaned.length() - 3);
            }
            cleaned = cleaned.trim();
            
            JsonArray jsonArray = gson.fromJson(cleaned, JsonArray.class);
            List<TarjetaSugeridaDTO> tarjetas = new ArrayList<>();
            
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject obj = jsonArray.get(i).getAsJsonObject();
                TarjetaSugeridaDTO tarjeta = new TarjetaSugeridaDTO();
                tarjeta.setFrente(obj.has("frente") ? obj.get("frente").getAsString() : "");
                tarjeta.setReverso(obj.has("reverso") ? obj.get("reverso").getAsString() : "");
                tarjeta.setConfianza(obj.has("confianza") ? obj.get("confianza").getAsDouble() : 0.5);
                tarjetas.add(tarjeta);
            }
            
            return tarjetas;
        } catch (Exception e) {
            logger.error("Error al parsear tarjetas: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Extrae los temas del mazo existente
     */
    private List<String> extraerTemas(Mazo mazo) {
        if (mazo.getTarjetas() == null || mazo.getTarjetas().isEmpty()) {
            return new ArrayList<>();
        }
        
        return mazo.getTarjetas().stream()
                .limit(5)  // Primeros 5 conceptos
                .map(t -> t.getFrente().substring(0, Math.min(30, t.getFrente().length())))
                .collect(Collectors.toList());
    }
}
