package co.studyflow.patterns.strategy;

import co.studyflow.model.Tarjeta;
import java.time.LocalDateTime;

/**
 * Interfaz Strategy para diferentes algoritmos de repetición
 */
public interface AlgoritmoRepeticion {
    /**
     * Calcula el próximo repaso de una tarjeta basado en la calificación
     */
    LocalDateTime calcularProximoRepaso(
        Tarjeta tarjeta,
        int calificacion,
        LocalDateTime ahora
    );
    
    /**
     * Actualiza el nivel de dificultad de la tarjeta
     */
    void actualizarNivelDificultad(Tarjeta tarjeta, int calificacion);
    
    String getNombre();
}
