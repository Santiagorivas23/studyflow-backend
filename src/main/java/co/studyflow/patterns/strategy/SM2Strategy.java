package co.studyflow.patterns.strategy;

import co.studyflow.model.Tarjeta;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Implementación del algoritmo SM-2 (Spaced Repetition Algorithm 2)
 * Base de Anki y Duolingo
 */
public class SM2Strategy implements AlgoritmoRepeticion {
    
    // Factores de intervalo para SM-2
    private static final int[] INTERVALOS = {1, 3, 7, 14, 30, 60, 120};
    
    @Override
    public LocalDateTime calcularProximoRepaso(
            Tarjeta tarjeta,
            int calificacion,
            LocalDateTime ahora
    ) {
        int nuevoIntervalo = calcularIntervalo(tarjeta.getNivelDificultad(), calificacion);
        return ahora.plus(nuevoIntervalo, ChronoUnit.DAYS);
    }
    
    /**
     * Calcula el intervalo en días basado en el nivel de dificultad y la calificación
     */
    private int calcularIntervalo(int nivelActual, int calificacion) {
        if (calificacion == 1) { // NO_LA_SUPE
            return 1; // Repasa al día siguiente
        }
        
        if (nivelActual >= INTERVALOS.length - 1) {
            return INTERVALOS[INTERVALOS.length - 1];
        }
        
        return INTERVALOS[Math.max(0, nivelActual)];
    }
    
    @Override
    public void actualizarNivelDificultad(Tarjeta tarjeta, int calificacion) {
        int nivelActual = tarjeta.getNivelDificultad();
        
        switch (calificacion) {
            case 1: // NO_LA_SUPE
                tarjeta.setNivelDificultad(Math.max(0, nivelActual - 2));
                break;
            case 2: // DIFICIL
                tarjeta.setNivelDificultad(nivelActual);
                break;
            case 3: // BIEN
                tarjeta.setNivelDificultad(Math.min(5, nivelActual + 1));
                break;
            case 4: // FACIL
                tarjeta.setNivelDificultad(Math.min(5, nivelActual + 2));
                break;
        }
    }
    
    @Override
    public String getNombre() {
        return "SM-2 (Spaced Repetition)";
    }
}
