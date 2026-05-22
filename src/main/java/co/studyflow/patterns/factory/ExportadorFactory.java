package co.studyflow.patterns.factory;

/**
 * Factory para crear exportadores - Abstract Factory Pattern
 */
public class ExportadorFactory {
    
    public enum TipoExportacion {
        JSON,
        CSV
    }
    
    public static Exportador crearExportador(TipoExportacion tipo) {
        return switch (tipo) {
            case JSON -> new ExportadorJSON();
            case CSV -> new ExportadorCSV();
        };
    }
    
    public static Exportador crearExportador(String tipoString) {
        try {
            TipoExportacion tipo = TipoExportacion.valueOf(tipoString.toUpperCase());
            return crearExportador(tipo);
        } catch (IllegalArgumentException e) {
            return new ExportadorJSON(); // default
        }
    }
}
