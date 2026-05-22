package co.studyflow.patterns.factory;

import java.io.OutputStream;
import java.io.IOException;
import java.util.List;
import co.studyflow.model.Tarjeta;

/**
 * Interfaz para el patrón Abstract Factory
 * Define diferentes tipos de exportadores de mazos
 */
public interface Exportador {
    void exportar(List<Tarjeta> tarjetas, OutputStream output) throws IOException;
    String obtenerNombreArchivo(String nombreMazo);
    String obtenerTipoContenido();
}
