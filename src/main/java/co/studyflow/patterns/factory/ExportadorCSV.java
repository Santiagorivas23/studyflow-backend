package co.studyflow.patterns.factory;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.util.List;
import co.studyflow.model.Tarjeta;

/**
 * Exportador CSV - implementación del Abstract Factory
 */
public class ExportadorCSV implements Exportador {
    
    @Override
    public void exportar(List<Tarjeta> tarjetas, OutputStream output) throws IOException {
        try (OutputStreamWriter writer = new OutputStreamWriter(output);
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT
                     .withHeader("ID", "Frente", "Reverso", "Tipo", "Etiquetas", 
                                 "Nivel Dificultad", "Intentos", "Aciertos", "Con Pista", "Pista"))) {
            
            for (Tarjeta tarjeta : tarjetas) {
                printer.printRecord(
                    tarjeta.getId(),
                    tarjeta.getFrente(),
                    tarjeta.getReverso(),
                    tarjeta.getTipo().name(),
                    tarjeta.getEtiquetas(),
                    tarjeta.getNivelDificultad(),
                    tarjeta.getIntentosTotales(),
                    tarjeta.getAciertos(),
                    tarjeta.getConPista(),
                    tarjeta.getPista()
                );
            }
            printer.flush();
        }
    }
    
    @Override
    public String obtenerNombreArchivo(String nombreMazo) {
        return nombreMazo.replaceAll("\\s+", "_") + ".csv";
    }
    
    @Override
    public String obtenerTipoContenido() {
        return "text/csv";
    }
}
