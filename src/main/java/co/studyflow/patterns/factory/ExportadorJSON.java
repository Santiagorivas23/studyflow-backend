package co.studyflow.patterns.factory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import co.studyflow.model.Tarjeta;
import co.studyflow.util.LocalDateTimeAdapter;

/**
 * Exportador JSON - implementación del Abstract Factory
 */
public class ExportadorJSON implements Exportador {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    
    @Override
    public void exportar(List<Tarjeta> tarjetas, OutputStream output) throws IOException {
        String json = gson.toJson(tarjetas);
        try (OutputStreamWriter writer = new OutputStreamWriter(output, StandardCharsets.UTF_8)) {
            writer.write(json);
            writer.flush();
        }
    }
    
    @Override
    public String obtenerNombreArchivo(String nombreMazo) {
        return nombreMazo.replaceAll("\\s+", "_") + ".json";
    }
    
    @Override
    public String obtenerTipoContenido() {
        return "application/json";
    }
}
